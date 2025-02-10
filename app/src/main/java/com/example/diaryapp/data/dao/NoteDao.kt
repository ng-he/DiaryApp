package com.example.diaryapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.diaryapp.data.entity.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM note ORDER BY note_id DESC, date DESC")
    suspend fun getAllNotes(): List<Note>

    @Insert
    suspend fun insertNote(note: Note): Long // Return the ID of the inserted note

    @Delete
    suspend fun deleteNote(note: Note): Int

    @Update
    suspend fun updateNote(note: Note): Int

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    suspend fun getNote(noteId: Int): Note
}