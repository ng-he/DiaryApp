package com.example.diaryapp.repository

import androidx.lifecycle.LiveData
import com.example.diaryapp.model.Note

interface INoteRepository {
    val readAllNotes: LiveData<List<Note>>

    suspend fun updateNote(note: Note)

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)
}