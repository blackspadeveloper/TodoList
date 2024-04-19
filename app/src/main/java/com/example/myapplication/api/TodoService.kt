package com.example.myapplication.api

import retrofit2.Response
import retrofit2.http.GET

interface TodoService {

    @GET("todos")
    suspend fun listTodos(): Response<List<TodoItem>>

}