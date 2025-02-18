package com.example.diaryapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.diaryapp.common.DatabaseActionState
import com.example.diaryapp.data.AppDatabase
import com.example.diaryapp.model.Note
import com.example.diaryapp.repository.INoteRepository
import com.example.diaryapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(application: Application, private val repository: INoteRepository): AndroidViewModel(application) {
    var readAllNotes: LiveData<List<Note>> = repository.readAllNotes

    private fun launchAction(
        action: suspend() -> Unit,
        callback: (DatabaseActionState) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                action()
                withContext(Dispatchers.Main) {
                    callback(DatabaseActionState.Success)
                }
            } catch (ex: Exception) {
                ex.message?.let { Log.e("NoteViewModel", it) }
                withContext(Dispatchers.Main) {
                    callback(DatabaseActionState.Error(ex))
                }
            }
        }
    }

    fun insertNote(note: Note, callback: (DatabaseActionState) -> Unit) = launchAction( { repository.insertNote(note) }, callback)

    fun updateNote(note: Note, callback: (DatabaseActionState) -> Unit) = launchAction( { repository.updateNote(note) }, callback)

    fun deleteNote(note: Note, callback: (DatabaseActionState) -> Unit) = launchAction( { repository.deleteNote(note) }, callback)
}