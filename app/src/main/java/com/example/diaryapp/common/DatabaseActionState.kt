package com.example.diaryapp.common

sealed class DatabaseActionState {
    data object Success : DatabaseActionState()
    class Error(val ex: Exception) : DatabaseActionState()
}