package com.example.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricPrompt
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import org.json.JSONObject

/**
 * BiometricSession (v2.0.1)
 *
 * Implements the canonical AndroidX "CryptoObject" pattern for biometric
 * quick-unlock, replacing the previous lookup-only biometric login:
 *
 *  1. ENROLLMENT (right after a successful password login):
 *     a Cipher in ENCRYPT_MODE bound to a Keystore key that requires
 *     user authentication is handed to BiometricPrompt via CryptoObject.
 *     Only after a successful fingerprint/face scan is the session blob
 *     (username + role) encrypted with that authenticated cipher.
 *
 *  2. UNLOCK (next app launches):
 *     a Cipher in DECRYPT_MODE, initialized with the stored IV, is handed
 *     to BiometricPrompt via CryptoObject. The session payload can only be
 *     decrypted after a fresh biometric authentication.
 *
 *  * The key is invalidated automatically when new biometrics are enrolled
 *    (setInvalidatedByBiometricEnrollment), forcing a password re-login.
 *  * No password or token is ever stored in plaintext.
 */
object BiometricSession {

    private const val KEY_ALIAS = "netops_biometric_gate_key"
    private const val KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORM = "AES/GCM/NoPadding"
    private const val PREFS = "netops_biometric_session"
    private const val K_BLOB = "session_blob"
    private const val K_IV = "session_iv"
    private const val K_USER = "enrolled_username"

    data class EnrolledUser(val username: String, val displayName: String, val role: String)

    // ------------------------------------------------------------------
    // Enrollment state
    // ------------------------------------------------------------------

    fun enrolledUsername(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(K_USER, null)

    fun isEnrolled(context: Context): Boolean = enrolledUsername(context) != null

    fun revoke(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    // ------------------------------------------------------------------
    // Crypto ciphers for the BiometricPrompt CryptoObject
    // ------------------------------------------------------------------

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }

        val generator = KeyGenerator.getInstance("AES", KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(-1) // auth per crypto operation
                .setInvalidatedByBiometricEnrollment(true)
                .build()
        )
        return generator.generateKey()
    }

    fun encryptCipher(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        return cipher
    }

    fun decryptCipher(context: Context): Cipher? {
        val iv = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(K_IV, null) ?: return null
        return try {
            val cipher = Cipher.getInstance(TRANSFORM)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateKey(),
                javax.crypto.spec.GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP))
            )
            cipher
        } catch (_: Exception) {
            null
        }
    }

    // ------------------------------------------------------------------
    // Enrollment prompt (called right after successful password login)
    // ------------------------------------------------------------------

    fun enroll(
        activity: FragmentActivity,
        context: Context,
        username: String,
        displayName: String,
        role: String,
        isPersian: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        val cipher = try {
            encryptCipher()
        } catch (_: Exception) {
            onResult(false)
            return
        }

        val prompt = BiometricPrompt(
            activity,
            androidx.core.content.ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    try {
                        val crypto = result.cryptoObject?.cipher ?: throw IllegalStateException()
                        val payload = JSONObject().apply {
                            put("username", username)
                            put("display_name", displayName)
                            put("role", role)
                            put("enrolled_at", System.currentTimeMillis())
                        }.toString()
                        val encrypted = crypto.doFinal(payload.toByteArray(StandardCharsets.UTF_8))
                        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                            .putString(K_BLOB, Base64.encodeToString(encrypted, Base64.NO_WRAP))
                            .putString(K_IV, Base64.encodeToString(crypto.iv, Base64.NO_WRAP))
                            .putString(K_USER, username)
                            .apply()
                        onResult(true)
                    } catch (_: Exception) {
                        onResult(false)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onResult(false)
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(if (isPersian) "فعال‌سازی ورود بایومتریک" else "Enable Biometric Unlock")
            .setSubtitle(
                if (isPersian) "برای ثبت سریع جلسه ورود، اثر انگشت / چهره خود را تأیید کنید"
                else "Confirm your fingerprint / face to securely store the session"
            )
            .setNegativeButtonText(if (isPersian) "فعال‌سازی نکن" else "Not now")
            .build()

        prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
    }

    // ------------------------------------------------------------------
    // Unlock prompt
    // ------------------------------------------------------------------

    fun unlock(
        activity: FragmentActivity,
        context: Context,
        isPersian: Boolean,
        onAuthenticated: (EnrolledUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val username = enrolledUsername(context)
        val cipher = decryptCipher(context)
        if (username == null || cipher == null) {
            onError(
                if (isPersian) "جلسه بایومتریک یافت نشد؛ ابتدا با رمز عبور وارد شوید."
                else "No biometric session found; sign in with your password first."
            )
            return
        }

        val prompt = BiometricPrompt(
            activity,
            androidx.core.content.ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    try {
                        val crypto = result.cryptoObject?.cipher ?: throw IllegalStateException()
                        val blob = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                            .getString(K_BLOB, null) ?: throw IllegalStateException()
                        val plain = String(
                            crypto.doFinal(Base64.decode(blob, Base64.NO_WRAP)),
                            StandardCharsets.UTF_8
                        )
                        val json = JSONObject(plain)
                        onAuthenticated(
                            EnrolledUser(
                                username = json.optString("username", username),
                                displayName = json.optString("display_name", username),
                                role = json.optString("role", "Viewer")
                            )
                        )
                    } catch (_: Exception) {
                        // Key invalidated (new biometrics) or tampered payload
                        revoke(context)
                        onError(
                            if (isPersian)
                                "جلسه بایومتریک باطل شد (تغییر اطلاعات بیومتریک). لطفاً با رمز عبور وارد شوید."
                            else
                                "Biometric session invalidated (biometrics changed). Please sign in with your password."
                        )
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        onError(errString.toString())
                    }
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(if (isPersian) "ورود بایومتریک امن" else "Secure Biometric Unlock")
            .setSubtitle(
                if (isPersian) "احراز هویت بیومتریک برای ورود به سامانه NetOps Hub"
                else "Confirm biometric credential to access NetOps Hub"
            )
            .setNegativeButtonText(if (isPersian) "انصراف" else "Cancel")
            .build()

        prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
    }
}
