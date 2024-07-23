package de.fhe.ai.colivingpilot.storage

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.model.ShoppingListItem
import de.fhe.ai.colivingpilot.model.Task
import de.fhe.ai.colivingpilot.model.User

@Database(
    entities = [User::class, Task::class, ShoppingListItem::class],
    version = 4
)
abstract class WgDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun shoppingListItemDao(): ShoppingListItemDao

    companion object {
        @Volatile
        private var instance: WgDatabase? = null

        fun getInstance(context: Context): WgDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): WgDatabase {
            return Room.databaseBuilder(context.applicationContext, WgDatabase::class.java, "wg_db")
                .addCallback(createCallback)
                .build()
        }

        private val createCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.i(CoLiPiApplication.LOG_TAG, "Database created")
            }
        }
    }

}