package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NetOpsDao {

    // Servers
    @Query("SELECT * FROM servers ORDER BY serverNum ASC, name ASC")
    fun getAllServers(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM servers WHERE site = :site ORDER BY serverNum ASC")
    fun getServersBySite(site: String): Flow<List<ServerEntity>>

    @Query("SELECT * FROM servers WHERE name LIKE '%' || :query || '%' OR ip LIKE '%' || :query || '%' OR service LIKE '%' || :query || '%' OR site LIKE '%' || :query || '%'")
    fun searchServers(query: String): Flow<List<ServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<ServerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerEntity)

    @Update
    suspend fun updateServer(server: ServerEntity)

    @Delete
    suspend fun deleteServer(server: ServerEntity)

    // Sites & vCenters
    @Query("SELECT * FROM sites ORDER BY name ASC")
    fun getAllSites(): Flow<List<SiteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSites(sites: List<SiteEntity>)

    @Query("SELECT * FROM vcenters ORDER BY name ASC")
    fun getAllVcenters(): Flow<List<VcenterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVcenters(vcenters: List<VcenterEntity>)

    // Rahkaran
    @Query("SELECT * FROM rahkaran ORDER BY rowNo ASC")
    fun getAllRahkaran(): Flow<List<RahkaranEntity>>

    @Query("SELECT * FROM rahkaran WHERE nameFa LIKE '%' || :query || '%' OR ip LIKE '%' || :query || '%'")
    fun searchRahkaran(query: String): Flow<List<RahkaranEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRahkaran(list: List<RahkaranEntity>)

    // Academy Articles
    @Query("SELECT * FROM articles ORDER BY title ASC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE categorySlug = :category ORDER BY title ASC")
    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: String): ArticleEntity?

    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchArticles(query: String): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Update
    suspend fun updateArticle(article: ArticleEntity)

    @Delete
    suspend fun deleteArticle(article: ArticleEntity)

    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun deleteArticleById(id: String)

    // Ports
    @Query("SELECT * FROM ports ORDER BY portNumber ASC")
    fun getAllPorts(): Flow<List<PortEntity>>

    @Query("SELECT * FROM ports WHERE CAST(portNumber AS TEXT) LIKE '%' || :query || '%' OR service LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchPorts(query: String): Flow<List<PortEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPorts(ports: List<PortEntity>)

    // HTTP Errors
    @Query("SELECT * FROM http_errors ORDER BY code ASC")
    fun getAllHttpErrors(): Flow<List<HttpErrorEntity>>

    @Query("SELECT * FROM http_errors WHERE CAST(code AS TEXT) LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%'")
    fun searchHttpErrors(query: String): Flow<List<HttpErrorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHttpErrors(errors: List<HttpErrorEntity>)

    // Wireshark Filters
    @Query("SELECT * FROM wireshark_filters ORDER BY id ASC")
    fun getAllWiresharkFilters(): Flow<List<WiresharkEntity>>

    @Query("SELECT * FROM wireshark_filters WHERE filterSyntax LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchWiresharkFilters(query: String): Flow<List<WiresharkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWiresharkFilters(filters: List<WiresharkEntity>)

    // Favorites
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE articleId = :articleId)")
    fun isFavorite(articleId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE articleId = :articleId")
    suspend fun removeFavorite(articleId: String)

    // Notes
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE articleId = :articleId ORDER BY updatedAt DESC")
    fun getNotesForArticle(articleId: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // Table Counts (for pre-population verification)
    @Query("SELECT COUNT(*) FROM servers")
    suspend fun getServerCount(): Int

    // User Management (Creator & Admin)
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("UPDATE users SET role = :newRole WHERE id = :userId")
    suspend fun updateUserRole(userId: String, newRole: String)

    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, isActive: Boolean)

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :userId")
    suspend fun updateUserPassword(userId: String, passwordHash: String)

    @Query("UPDATE users SET passwordHash = :passwordHash, mustChangePassword = :mustChange WHERE id = :userId")
    suspend fun updateUserPasswordAndFlag(userId: String, passwordHash: String, mustChange: Boolean)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    // ==================== Phone Book (دفترچه تلفن) ====================
    @Query("SELECT * FROM phonebook ORDER BY lastName ASC, firstName ASC")
    fun getAllPhoneEntries(): Flow<List<PhoneEntryEntity>>

    @Query("SELECT * FROM phonebook WHERE firstName LIKE '%' || :query || '%' OR lastName LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' OR company LIKE '%' || :query || '%' OR extension LIKE '%' || :query || '%' ORDER BY lastName ASC, firstName ASC")
    fun searchPhoneEntries(query: String): Flow<List<PhoneEntryEntity>>

    @Query("SELECT * FROM phonebook WHERE id = :id LIMIT 1")
    suspend fun getPhoneEntryById(id: String): PhoneEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoneEntries(entries: List<PhoneEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoneEntry(entry: PhoneEntryEntity)

    @Update
    suspend fun updatePhoneEntry(entry: PhoneEntryEntity)

    @Delete
    suspend fun deletePhoneEntry(entry: PhoneEntryEntity)

    @Query("DELETE FROM phonebook")
    suspend fun clearPhonebook()

    @Query("SELECT COUNT(*) FROM phonebook")
    suspend fun getPhonebookCount(): Int
}
