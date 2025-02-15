package com.example.diaryapp.repository

import androidx.lifecycle.LiveData
import com.example.diaryapp.data.NoteDao
import com.example.diaryapp.model.Note

class NoteRepository(private val noteDao: NoteDao) {
    val readAllNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
}