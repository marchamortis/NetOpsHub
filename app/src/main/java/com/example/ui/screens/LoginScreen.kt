package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.R
import com.example.data.local.UserEntity
import com.example.data.security.BiometricSession
import com.example.data.security.SecureVault
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalNetOpsColors
import com.example.ui.theme.TerminalGreen
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    currentLang: String,
    onLangChange: (String) -> Unit,
    onLoginSuccess: (UserEntity, Boolean) -> Unit,
    onAuthenticate: suspend (String, String) -> Result<UserEntity>,
    onChangePassword: suspend (String, String) -> Unit,
    onBiometricLogin: (suspend (String) -> Result<UserEntity>)? = null,
    lastUsername: String? = null
) {
    val tokens = LocalNetOpsColors.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf(lastUsername ?: "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pendingUserForPasswordChange by remember { mutableStateOf<UserEntity?>(null) }

    val isPersian = currentLang == "fa"

    fun performLogin(u: String, p: String) {
        if (u.isBlank()) {
            errorMessage = if (isPersian) "لطفاً نام کاربری را وارد کنید" else "Please enter your username"
            return
        }
        if (p.isBlank()) {
            errorMessage = if (isPersian) "لطفاً رمز عبور را وارد کنید" else "Please enter your password"
            return
        }
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            val result = onAuthenticate(u, p)
            isLoading = false
            result.onSuccess { user ->
                if (user.mustChangePassword) {
                    pendingUserForPasswordChange = user
                } else {
                    Toast.makeText(
                        context,
                        if (isPersian) "خوش آمدید ${user.displayName}" else "Welcome, ${user.displayName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onLoginSuccess(user, rememberMe)
                    // v2.0.1: offer secure biometric enrollment right after a
                    // successful password login (CryptoObject-protected)
                    if (rememberMe && !BiometricSession.isEnrolled(context) && onBiometricLogin != null) {
                        (context as? androidx.fragment.app.FragmentActivity)?.let { activity ->
                            BiometricSession.enroll(
                                activity = activity,
                                context = context,
                                username = user.username,
                                displayName = user.displayName,
                                role = user.role,
                                isPersian = isPersian
                            ) { enrolled ->
                                if (enrolled) {
                                    Toast.makeText(
                                        context,
                                        if (isPersian) "ورود بایومتریک برای دفعات بعد فعال شد" else "Biometric unlock enabled for next launches",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }.onFailure { ex ->
                errorMessage = ex.message ?: if (isPersian) "احراز هویت با خطا مواجه شد" else "Authentication failed"
            }
        }
    }

    fun triggerBiometricAuth() {
        val activity = context as? FragmentActivity
        if (activity == null) {
            Toast.makeText(context, "Biometric requires FragmentActivity", Toast.LENGTH_SHORT).show()
            return
        }

        // --------------------------------------------------------------
        // v2.0.1 SECURE FLOW: CryptoObject-protected quick unlock.
        // Only the enrolled account can be restored, and the stored session
        // blob is decryptable exclusively through a fresh biometric scan.
        // --------------------------------------------------------------
        if (BiometricSession.isEnrolled(context)) {
            BiometricSession.unlock(
                activity = activity,
                context = context,
                isPersian = isPersian,
                onAuthenticated = { enrolled ->
                    coroutineScope.launch {
                        val res = onBiometricLogin?.invoke(enrolled.username)
                            ?: onAuthenticate(enrolled.username, password)
                        res.onSuccess { user ->
                            if (user.mustChangePassword) {
                                pendingUserForPasswordChange = user
                            } else {
                                Toast.makeText(
                                    context,
                                    if (isPersian) "ورود بایومتریک موفق بود. خوش آمدید ${user.displayName}" else "Biometrics verified. Welcome, ${user.displayName}!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onLoginSuccess(user, true)
                            }
                        }.onFailure { ex ->
                            Toast.makeText(context, ex.message ?: "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
            )
            return
        }

        // Not enrolled yet: guide the user (enrollment happens automatically
        // after the next successful password login)
        Toast.makeText(
            context,
            if (isPersian)
                "برای فعال‌سازی ورود بایومتریک، ابتدا یک‌بار با رمز عبور وارد شوید"
            else
                "Sign in once with your password to enable biometric unlock",
            Toast.LENGTH_LONG
        ).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("login_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // Top Bar with Security Badge & Language Switcher
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // NOC Status Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(tokens.glassSurface)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TerminalGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SECURE GATEWAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TerminalGreen,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    // Language Toggle (English / فارسی)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = tokens.glassSurface,
                        border = BorderStroke(1.dp, tokens.glassBorder),
                        modifier = Modifier.clickable {
                            val nextLang = if (currentLang == "en") "fa" else "en"
                            onLangChange(nextLang)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = tokens.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPersian) "فارسی (FA)" else "English (EN)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.textPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Header Title and Shield Logo
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(tokens.primary.copy(alpha = 0.35f), tokens.accent.copy(alpha = 0.2f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Shield",
                            tint = tokens.primary,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = tokens.textPrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPersian) "به سامانه NetOps Hub خوش آمدید" else "Welcome to NetOps Hub",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPersian)
                            "لطفاً برای ورود به پنل عملیات شبکه و زیرساخت، اطلاعات کاربری خود را وارد نمایید."
                        else
                            "Please sign in with your enterprise credentials to access NOC infrastructure.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = tokens.textSecondary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Clean Dedicated Login Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = if (isPersian) "ورود به حساب کاربری" else "Account Sign-In",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = tokens.textPrimary
                            )
                        )

                        // Username Input
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                errorMessage = null
                            },
                            label = { Text(if (isPersian) "نام کاربری" else "Username") },
                            placeholder = { Text(if (isPersian) "شناسه کاربری شما..." else "Enter your username...") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = tokens.primary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = tokens.primary,
                                unfocusedBorderColor = tokens.glassBorder,
                                focusedLabelColor = tokens.primary,
                                unfocusedLabelColor = tokens.textSecondary
                            )
                        )

                        // Password Input
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                            },
                            label = { Text(if (isPersian) "رمز عبور" else "Password") },
                            placeholder = { Text(if (isPersian) "رمز عبور سازمانی..." else "Enter your password...") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = tokens.primary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = tokens.textSecondary
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { performLogin(username, password) }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = tokens.primary,
                                unfocusedBorderColor = tokens.glassBorder,
                                focusedLabelColor = tokens.primary,
                                unfocusedLabelColor = tokens.textSecondary
                            )
                        )

                        // Remember Me & Biometric Quick Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { rememberMe = !rememberMe }
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = tokens.primary,
                                        checkmarkColor = tokens.background
                                    )
                                )
                                Text(
                                    text = if (isPersian) "مرا به خاطر بسپار" else "Remember me",
                                    style = MaterialTheme.typography.bodySmall.copy(color = tokens.textPrimary)
                                )
                            }

                            // Biometric Button
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = tokens.surface,
                                border = BorderStroke(1.dp, tokens.accent.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { triggerBiometricAuth() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Biometric Login",
                                        tint = tokens.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPersian) "بایومتریک" else "Biometrics",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = tokens.accent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        // Error Banner
                        AnimatedVisibility(visible = errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        // Submit Sign In Button
                        Button(
                            onClick = { performLogin(username, password) },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tokens.primary,
                                contentColor = tokens.background
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = tokens.background,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(if (isPersian) "در حال اعتبارسنجی..." else "Authenticating...")
                            } else {
                                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPersian) "ورود به سامانه" else "Sign In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Force Password Change Dialog on First Login
    if (pendingUserForPasswordChange != null) {
        val user = pendingUserForPasswordChange!!
        ForcePasswordChangeDialog(
            user = user,
            isPersian = isPersian,
            onDismiss = { pendingUserForPasswordChange = null },
            onPasswordSuccessfullyChanged = { newPass ->
                coroutineScope.launch {
                    onChangePassword(user.id, newPass)
                    val updatedUser = user.copy(passwordHash = SecureVault.hashPassword(newPass), mustChangePassword = false)
                    pendingUserForPasswordChange = null
                    Toast.makeText(
                        context,
                        if (isPersian) "رمز عبور جدید با موفقیت تنظیم شد. خوش آمدید!" else "New secure password set successfully. Welcome!",
                        Toast.LENGTH_SHORT
                    ).show()
                    onLoginSuccess(updatedUser, rememberMe)
                }
            }
        )
    }
}

/**
 * Enterprise Password Complexity Validation Rule
 * Must be at least 8 chars, containing uppercase, lowercase, digit, and special char.
 */
fun validatePasswordComplexity(pass: String): Map<String, Boolean> {
    return mapOf(
        "length" to (pass.length >= 8),
        "upper" to pass.any { it.isUpperCase() },
        "lower" to pass.any { it.isLowerCase() },
        "digit" to pass.any { it.isDigit() },
        "special" to pass.any { !it.isLetterOrDigit() }
    )
}

@Composable
fun ForcePasswordChangeDialog(
    user: UserEntity,
    isPersian: Boolean,
    onDismiss: () -> Unit,
    onPasswordSuccessfullyChanged: (String) -> Unit
) {
    val tokens = LocalNetOpsColors.current
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val checks = remember(newPassword) { validatePasswordComplexity(newPassword) }
    val isAllValid = checks.values.all { it } && (newPassword == confirmPassword) && newPassword.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = tokens.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPersian) "تغییر اجباری رمز عبور (ورود نخست)" else "Mandatory Password Change (First Login)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isPersian)
                        "همکار گرامی (${user.displayName})، طبق خط‌مشی امنیتی سازمان، در اولین ورود موظف به تغییر رمز موقت به یک رمز عبور امن و ترکیبی هستید."
                    else
                        "Hello ${user.displayName}. Enterprise security policy requires setting a strong, personal combination password on your first login.",
                    style = MaterialTheme.typography.bodySmall.copy(color = tokens.textSecondary)
                )

                // New Password Input
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMsg = null
                    },
                    label = { Text(if (isPersian) "رمز عبور جدید" else "New Combination Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm Password Input
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMsg = null
                    },
                    label = { Text(if (isPersian) "تکرار رمز عبور جدید" else "Confirm New Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Password Combination Checklist
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.surface,
                    border = BorderStroke(1.dp, tokens.glassBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isPersian) "الزامات امنیتی ترکیب رمز عبور:" else "Password Combination Requirements:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = tokens.primary)
                        )
                        ComplexityRuleItem(
                            label = if (isPersian) "حداقل ۸ کاراکتر" else "At least 8 characters",
                            isValid = checks["length"] == true
                        )
                        ComplexityRuleItem(
                            label = if (isPersian) "حداقل یک حرف بزرگ انگلیسی (A-Z)" else "At least one uppercase letter (A-Z)",
                            isValid = checks["upper"] == true
                        )
                        ComplexityRuleItem(
                            label = if (isPersian) "حداقل یک حرف کوچک انگلیسی (a-z)" else "At least one lowercase letter (a-z)",
                            isValid = checks["lower"] == true
                        )
                        ComplexityRuleItem(
                            label = if (isPersian) "حداقل یک عدد (0-9)" else "At least one numeric digit (0-9)",
                            isValid = checks["digit"] == true
                        )
                        ComplexityRuleItem(
                            label = if (isPersian) "حداقل یک کاراکتر ویژه (@#$%^&*...)" else "At least one special character (@#$%...)",
                            isValid = checks["special"] == true
                        )
                        if (confirmPassword.isNotEmpty()) {
                            ComplexityRuleItem(
                                label = if (isPersian) "تطابق هر دو رمز عبور" else "Passwords match",
                                isValid = newPassword == confirmPassword
                            )
                        }
                    }
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!checks.values.all { it }) {
                        errorMsg = if (isPersian) "لطفاً تمامی الزامات ترکیب رمز عبور را رعایت فرمایید." else "Please satisfy all combination criteria."
                        return@Button
                    }
                    if (newPassword != confirmPassword) {
                        errorMsg = if (isPersian) "رمز عبور جدید با تکرار آن یکسان نیست." else "Passwords do not match."
                        return@Button
                    }
                    onPasswordSuccessfullyChanged(newPassword.trim())
                },
                enabled = isAllValid,
                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
            ) {
                Text(if (isPersian) "تأیید و ورود به سیستم" else "Save & Access System")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isPersian) "انصراف" else "Cancel")
            }
        }
    )
}

@Composable
private fun ComplexityRuleItem(label: String, isValid: Boolean) {
    val tokens = LocalNetOpsColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isValid) TerminalGreen else tokens.textMuted,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isValid) tokens.textPrimary else tokens.textMuted,
                fontSize = 11.sp
            )
        )
    }
}
