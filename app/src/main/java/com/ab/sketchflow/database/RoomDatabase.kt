package com.ab.sketchflow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ab.sketchflow.dao.DraftDao
import com.ab.sketchflow.model.DraftEntity

@Database(entities = [DraftEntity::class], version = 1)
abstract class SketchFlowDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao

    companion object {
        @Volatile
        private var instance: SketchFlowDatabase? = null

        fun getDatabase(context: Context): SketchFlowDatabase {
            var tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                tempInstance = instance
                if (tempInstance == null) {
                    tempInstance = Room.databaseBuilder(
                        context.applicationContext,
                        SketchFlowDatabase::class.java,
                        "sketchflow.db"
                    ).build()
                    instance = tempInstance
                }
            }

            return tempInstance!!
        }
    }
}