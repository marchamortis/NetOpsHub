package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ServerEntity::class,
        SiteEntity::class,
        VcenterEntity::class,
        RahkaranEntity::class,
        ArticleEntity::class,
        PortEntity::class,
        HttpErrorEntity::class,
        WiresharkEntity::class,
        FavoriteEntity::class,
        NoteEntity::class,
        UserEntity::class,
        PhoneEntryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class NetOpsDatabase : RoomDatabase() {

    abstract fun dao(): NetOpsDao

    companion object {
        @Volatile
        private var INSTANCE: NetOpsDatabase? = null

        fun getInstance(context: Context): NetOpsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NetOpsDatabase::class.java,
                    "netops_hub_master.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate database in background coroutine on first creation
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).dao()
                                dao.insertSites(SeedData.sites)
                                dao.insertVcenters(SeedData.vcenters)
                                dao.insertServers(SeedData.servers)
                                dao.insertRahkaran(SeedData.rahkaranCompanies)
                                dao.insertArticles(SeedData.articles)
                                dao.insertPorts(SeedData.ports)
                                dao.insertHttpErrors(SeedData.httpErrors)
                                dao.insertWiresharkFilters(SeedData.wiresharkFilters)
                                dao.insertUsers(SeedData.defaultUsers)
                                dao.insertPhoneEntries(SeedData.phonebookEntries)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
