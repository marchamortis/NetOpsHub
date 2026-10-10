package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey val id: String,
    val serverNum: Int,
    val vcenter: String,
    val serverHost: String,
    val name: String,
    val ip: String,
    val service: String,
    val portRdp: String,
    val status: String, // 'ok', 'off'
    val site: String,
    val os: String = "Windows/Linux",
    val notes: String = ""
)

@Entity(tableName = "sites")
data class SiteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String
)

@Entity(tableName = "vcenters")
data class VcenterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val ip: String,
    val site: String,
    val description: String
)

@Entity(tableName = "rahkaran")
data class RahkaranEntity(
    @PrimaryKey val id: String,
    val rowNo: Int,
    val nameFa: String,
    val ip: String,
    val portApp: String,
    val portWeb: String,
    val url: String,
    val serviceName: String,
    val notes: String
)

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleFa: String = "",
    val summary: String,
    val summaryFa: String = "",
    val content: String,
    val categorySlug: String,
    val subcategory: String = "",
    val vendor: String = "Generic",
    val difficulty: String = "Intermediate",
    val tags: String = "",
    val commandsJson: String = "[]",
    val warnings: String = "",
    val expectedResults: String = "",
    val verification: String = "",
    val troubleshooting: String = "",
    val relatedArticles: String = ""
)

@Entity(tableName = "ports")
data class PortEntity(
    @PrimaryKey val id: String,
    val portNumber: Int,
    val protocol: String,
    val service: String,
    val description: String,
    val commonUsage: String = "",
    val securityNotes: String = ""
)

@Entity(tableName = "http_errors")
data class HttpErrorEntity(
    @PrimaryKey val id: String,
    val code: Int,
    val category: String,
    val name: String,
    val meaning: String,
    val causes: String,
    val troubleshooting: String,
    val example: String
)

@Entity(tableName = "wireshark_filters")
data class WiresharkEntity(
    @PrimaryKey val id: String,
    val filterSyntax: String,
    val title: String,
    val description: String,
    val purpose: String,
    val example: String,
    val useCase: String
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val articleId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val articleId: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val role: String, // 'Creator', 'Admin', 'Editor', 'Viewer'
    val isActive: Boolean = true,
    // v2.0.1: passwords are stored ONLY as PBKDF2-SHA256 hashes
    // (format: pbkdf2$100000$<saltHex>$<hashHex>) - never in plaintext.
    val passwordHash: String = "",
    val mustChangePassword: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    // v2.0.1: transient field used once at creation time to deliver the initial
    // password over TLS to the server. It is NOT persisted in Room (@Ignore,
    // declared in the class body so the Room constructor stays fully matchable)
    // and is immediately replaced by a PBKDF2 hash.
    @androidx.room.Ignore
    var plainPassword: String = ""
}

@Entity(tableName = "phonebook")
data class PhoneEntryEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String = "",
    val phone: String = "",
    val company: String = "",
    val extension: String = "",
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
