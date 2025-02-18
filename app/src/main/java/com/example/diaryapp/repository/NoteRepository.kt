package com.example.diaryapp.repository

import androidx.lifecycle.LiveData
import com.example.diaryapp.data.NoteDao
import com.example.diaryapp.model.Note

class NoteRepository(private val noteDao: NoteDao) : INoteRepository {
    override val readAllNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
}