package com.example.myapplication.api

data class TodoItem(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)

