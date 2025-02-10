package com.example.diaryapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    val noteId: Int = 0,

    var title: String = "",
    var content: String = "",
    var date: Date = Date(),
    var feeling: Int = 1,
    var images: String? = null
)
