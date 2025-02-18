package com.example.diaryapp.common

import com.example.diaryapp.model.Note

sealed class AppAction(var note: Note) {
    class Detail(note: Note): AppAction(note)
    class Edit(note: Note): AppAction(note)
    class Create(note: Note): AppAction(note)
}