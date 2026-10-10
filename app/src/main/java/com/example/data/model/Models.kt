package com.example.data.model

import com.squareup.moshi.JsonClass

enum class UserRole(val display: String) {
    CREATOR("Creator"),
    ADMIN("Admin"),
    EDITOR("Editor"),
    VIEWER("Viewer");

    companion object {
        fun fromString(role: String): UserRole {
            return when (role.trim().lowercase()) {
                "creator" -> CREATOR
                "admin" -> ADMIN
                "editor" -> EDITOR
                else -> VIEWER
            }
        }
    }
}

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String,
    val display_name: String = "",
    val email: String,
    val role: String,
    val is_active: Boolean = true,
    val must_change_password: Boolean = false
)

@JsonClass(generateAdapter = true)
data class CreateUserRequest(
    val username: String,
    val display_name: String,
    val email: String,
    val password: String,
    val role: String,
    val must_change_password: Boolean = true
)

@JsonClass(generateAdapter = true)
data class UpdatePasswordRequest(
    val password: String,
    val must_change_password: Boolean = false
)

@JsonClass(generateAdapter = true)
data class RemoteUser(
    val id: String,
    val username: String,
    val display_name: String? = null,
    val email: String? = null,
    val role: String,
    val is_active: Int? = 1,
    val must_change_password: Int? = 1
)

@JsonClass(generateAdapter = true)
data class UpdateRoleRequest(
    val role: String
)

@JsonClass(generateAdapter = true)
data class UpdateStatusRequest(
    val is_active: Boolean
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val token: String? = null,
    val expires_at: String? = null,
    val user: User? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class HealthResponse(
    val status: String,
    val version: String? = null,
    val timestamp: String? = null,
    val database: String? = null,
    val service: String? = null
)

@JsonClass(generateAdapter = true)
data class RemoteArticle(
    val id: String,
    val title: String,
    val title_fa: String? = null,
    val summary: String? = null,
    val summary_fa: String? = null,
    val content: String,
    val category_slug: String,
    val subcategory: String? = null,
    val vendor: String? = null,
    val difficulty: String? = "Intermediate",
    val tags: String? = null,
    val commands_json: String? = "[]",
    val warnings: String? = null,
    val expected_results: String? = null,
    val verification: String? = null,
    val troubleshooting: String? = null
)

@JsonClass(generateAdapter = true)
data class ArticlesResponse(
    val articles: List<RemoteArticle>? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ArticleActionResponse(
    val message: String? = null,
    val id: String? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class BulkSyncRequest(
    val articles: List<RemoteArticle>
)

@JsonClass(generateAdapter = true)
data class BulkSyncResponse(
    val message: String? = null,
    val count: Int = 0,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class OnlineSource(
    val id: String,
    val name: String,
    val name_fa: String? = null,
    val category: String,
    val url: String,
    val description: String,
    val description_fa: String? = null
)

@JsonClass(generateAdapter = true)
data class OnlineSourcesResponse(
    val sources: List<OnlineSource>? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class RemoteServer(
    val id: String,
    val server_num: Int? = 0,
    val vcenter: String? = "",
    val server_host: String? = "",
    val name: String,
    val ip: String? = "",
    val service: String? = "",
    val port_rdp: String? = "-",
    val status: String? = "ok",
    val site: String,
    val os: String? = "Windows/Linux",
    val notes: String? = ""
)

@JsonClass(generateAdapter = true)
data class RemoteSite(
    val id: String,
    val name: String,
    val description: String? = ""
)

@JsonClass(generateAdapter = true)
data class RemoteVcenter(
    val id: String,
    val name: String,
    val ip: String? = "",
    val site: String? = "",
    val description: String? = ""
)

@JsonClass(generateAdapter = true)
data class RemoteRahkaran(
    val id: String,
    val row_no: Int? = 0,
    val name_fa: String,
    val ip: String,
    val port_app: String? = "",
    val port_web: String? = "",
    val url: String? = "",
    val service_name: String? = "",
    val notes: String? = ""
)

@JsonClass(generateAdapter = true)
data class SyncAllResponse(
    val timestamp: String? = null,
    val servers: List<RemoteServer>? = null,
    val sites: List<RemoteSite>? = null,
    val vcenters: List<RemoteVcenter>? = null,
    val rahkaran: List<RemoteRahkaran>? = null,
    val articles: List<RemoteArticle>? = null,
    val users: List<RemoteUser>? = null,
    val phonebook: List<RemotePhoneEntry>? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ServerActionResponse(
    val server: RemoteServer? = null,
    val message: String? = null,
    val error: String? = null
)

// ==================== v2.0.1: Phone Book (دفترچه تلفن) ====================
@JsonClass(generateAdapter = true)
data class RemotePhoneEntry(
    val id: String? = null,
    val first_name: String = "",
    val last_name: String? = "",
    val phone: String? = "",
    val company: String? = "",
    val extension: String? = "",
    val notes: String? = ""
)

@JsonClass(generateAdapter = true)
data class PhonebookResponse(
    val phonebook: List<RemotePhoneEntry>? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class PhonebookBulkRequest(
    val entries: List<RemotePhoneEntry>
)

@JsonClass(generateAdapter = true)
data class PhoneEntryActionResponse(
    val entry: RemotePhoneEntry? = null,
    val message: String? = null,
    val count: Int? = null,
    val error: String? = null
)

// ==================== v2.0.1: OTA in-app update ====================
@JsonClass(generateAdapter = true)
data class AppVersionResponse(
    val latest_version_code: Int = 0,
    val latest_version_name: String? = null,
    val changelog_en: String? = "",
    val changelog_fa: String? = "",
    val apk_url: String? = "",
    val exe_url: String? = "",
    val mandatory: Boolean = false,
    val published_at: String? = null,
    val error: String? = null
)
