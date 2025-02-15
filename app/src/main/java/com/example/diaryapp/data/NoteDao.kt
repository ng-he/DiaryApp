package com.example.diaryapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.diaryapp.model.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM note ORDER BY note_id DESC, date DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert
    suspend fun insertNote(note: Note): Long // Return the ID of the inserted note

    @Delete
    suspend fun deleteNote(note: Note): Int

    @Update
    suspend fun updateNote(note: Note): Int

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    suspend fun getNote(noteId: Int): Note
}