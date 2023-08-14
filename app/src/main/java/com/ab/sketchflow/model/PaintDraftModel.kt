package com.ab.sketchflow.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "DraftEntity", indices = [Index(value = ["id"], unique = true)])
data class DraftEntity(
    @ColumnInfo
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bitmap_data") var bitmapData: ByteArray,
    val name : String = "",
    val url : String = ""
)