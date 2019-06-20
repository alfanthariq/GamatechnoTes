package com.alfanthariq.skeleton.data.local

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alfanthariq.skeleton.data.local.sqlitehelper.AssetSQLiteOpenHelperFactory
import com.alfanthariq.skeleton.data.model.*
import java.io.File

@Database(entities = [LogKegiatan::class, Users::class, Messages::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun UserDAO(): UsersDAO
    abstract fun MessageDAO(): MessageDAO

    companion object {
        private val databaseName = "data.db"
        private val folderPath = Environment.getExternalStorageDirectory().toString() + "/esign/database/"
        var database: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (database == null) {
                synchronized(AppDatabase::class.java) {
                    if (database == null) {
                        database = buildDatabase(context)
                    }
                }
            }
            return database
        }

        fun buildDatabase(applicationContext: Context): AppDatabase? {
            val isset = setFolderDatabase()
            when (isset) {
                true -> return Room.databaseBuilder(applicationContext, AppDatabase::class.java, databaseName)
                        //.openHelperFactory(AssetSQLiteOpenHelperFactory())
                        //.addMigrations(MIGRATION_1_2)
                        .build()
                else -> return null
            }
        }

        private fun setFolderDatabase(): Boolean {
            val folder = File(folderPath)
            var success = true
            if (!folder.exists()) {
                success = folder.mkdirs()
            }
            if (success) {
                println("Sukses buat folder database!")
            } else {
                println("Tidak bisa membuat folder !")
            }
            return success
        }

        /*private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE finger_upt (" +
                        "    upt_code TEXT NOT NULL," +
                        "    upt_name TEXT," +
                        "    upt_ip TEXT," +
                        "    status INTEGER NOT NULL," +
                        "    PRIMARY KEY (" +
                        "        upt_code" +
                        "    )" +
                        ");")
            }
        }*/
    }

}