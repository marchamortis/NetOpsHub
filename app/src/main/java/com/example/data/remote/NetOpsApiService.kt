package com.example.data.remote

import com.example.data.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface NetOpsApiService {

    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Academy Endpoints for Cloudflare D1
    @GET("academy/articles")
    suspend fun getArticles(
        @Query("category") category: String? = null,
        @Query("q") query: String? = null
    ): Response<ArticlesResponse>

    @GET("academy/articles/{id}")
    suspend fun getArticleById(@Path("id") id: String): Response<Map<String, Any>>

    @POST("academy/articles")
    suspend fun createArticle(
        @Header("Authorization") authHeader: String,
        @Body article: RemoteArticle
    ): Response<ArticleActionResponse>

    @PUT("academy/articles/{id}")
    suspend fun updateArticle(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String,
        @Body article: RemoteArticle
    ): Response<ArticleActionResponse>

    @DELETE("academy/articles/{id}")
    suspend fun deleteArticle(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String
    ): Response<ArticleActionResponse>

    @POST("academy/bulk-sync")
    suspend fun bulkSync(
        @Header("Authorization") authHeader: String,
        @Body request: BulkSyncRequest
    ): Response<BulkSyncResponse>

    @GET("academy/sources")
    suspend fun getOnlineSources(): Response<OnlineSourcesResponse>

    // Real-Time Consolidated Sync (All Company Data & Knowledge)
    // v2.0.1: /sync/all now requires a valid session token
    @GET("sync/all")
    suspend fun syncAll(
        @Header("Authorization") authHeader: String
    ): Response<SyncAllResponse>

    // ==================== v2.0.1: Phone Book ====================
    @GET("phonebook")
    suspend fun getPhonebook(
        @Header("Authorization") authHeader: String
    ): Response<PhonebookResponse>

    @POST("phonebook")
    suspend fun createPhoneEntry(
        @Header("Authorization") authHeader: String,
        @Body entry: RemotePhoneEntry
    ): Response<PhoneEntryActionResponse>

    @POST("phonebook/bulk")
    suspend fun bulkImportPhonebook(
        @Header("Authorization") authHeader: String,
        @Body request: PhonebookBulkRequest
    ): Response<PhoneEntryActionResponse>

    @PUT("phonebook/{id}")
    suspend fun updatePhoneEntry(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String,
        @Body entry: RemotePhoneEntry
    ): Response<PhoneEntryActionResponse>

    @DELETE("phonebook/{id}")
    suspend fun deletePhoneEntry(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, Any>>

    // ==================== v2.0.1: OTA update check ====================
    @GET("app/version")
    suspend fun checkAppVersion(): Response<AppVersionResponse>

    // v2.0.1: verify the current session and restore user profile
    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") authHeader: String): Response<LoginResponse>

    @POST("servers")
    suspend fun createServer(
        @Header("Authorization") authHeader: String,
        @Body server: RemoteServer
    ): Response<ServerActionResponse>

    @PUT("servers/{id}")
    suspend fun updateServer(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String,
        @Body server: RemoteServer
    ): Response<ServerActionResponse>

    @DELETE("servers/{id}")
    suspend fun deleteServer(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, Any>>

    // User Management & Online Operations
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") authHeader: String
    ): Response<Map<String, Any>>

    @POST("users")
    suspend fun createUser(
        @Header("Authorization") authHeader: String,
        @Body request: CreateUserRequest
    ): Response<Map<String, Any>>

    @PUT("users/{id}/password")
    suspend fun updatePassword(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String,
        @Body request: UpdatePasswordRequest
    ): Response<Map<String, Any>>

    @PUT("users/{id}/role")
    suspend fun updateRole(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String,
        @Body request: UpdateRoleRequest
    ): Response<Map<String, Any>>

    @PUT("users/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String,
        @Body request: UpdateStatusRequest
    ): Response<Map<String, Any>>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String
    ): Response<Map<String, Any>>
}

object ApiClient {
    // Configurable endpoint pointing to deployed Cloudflare Worker API
    var baseUrl: String = "https://netops-hub-api.mortavox.workers.dev/api/v1/"

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        // v2.0.1 SECURITY: HTTP body logging only in debug builds.
        // Release builds must never leak credentials/infrastructure to logcat.
        if (com.example.BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }
        builder.build()
    }

    fun getService(url: String = baseUrl): NetOpsApiService {
        val normalizedUrl = if (url.endsWith("/")) url else "$url/"
        return Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(NetOpsApiService::class.java)
    }
}
