package com.example.myapplication

import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.RetrofitClient
import com.example.myapplication.api.TodoItem
import com.example.myapplication.api.TodoService
import com.example.myapplication.database.DBHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val todoService = RetrofitClient.instance.create(TodoService::class.java)

        setContent {
            ToDoListApp(todoService)
        }
    }
}

@Composable
fun ToDoListApp(todoService: TodoService) {
    val todosState = remember { mutableStateOf<List<TodoItem>>(emptyList()) }

    LaunchedEffect(true) {
        val response = todoService.listTodos()
        if (response.isSuccessful) {
            todosState.value = response.body() ?: emptyList()
        } else {
            Log.e("MainActivity", "Error fetching todos")
        }

    }

    TodoList(todosState.value)
}

@SuppressLint("Range")
@Composable
fun TodoList(todos: List<TodoItem>) {
    val tasks = remember { mutableStateListOf<String>() }
    
    val dbHelper = DBHelper(LocalContext.current)

    MaterialTheme {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight()) {

            Text(text = "ToDoList", fontWeight= FontWeight.Bold, fontSize = TextUnit(value = 26.0f, type = TextUnitType.Sp))

            Spacer(modifier = Modifier.padding(20.dp))

            LazyColumn {
                items(todos.size) { index ->
                    if(index < 5) {
                        Text(text = "[${index + 1}] " + todos[index].title)
                        Spacer(modifier = Modifier.padding(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.padding(20.dp))

            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Text(text = "-----------------------------------------------------")
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Row {
                    Button(
                        onClick = {
                            if (todos.isNotEmpty()) {
                                todos.forEachIndexed { index, todoItem ->
                                    if(index < 5) {
                                        dbHelper.addTask(todoItem.title)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("데이터 저장")
                    }

                    Spacer(modifier = Modifier.padding(10.dp))

                    Button(
                        onClick = {
                            tasks.clear()
                            val cursor = dbHelper.getAllTasks()
                            while (cursor.moveToNext()) {
                                val task =
                                    cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TASK))

                                tasks.add(task)
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("데이터 가져오기")
                    }
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            if(tasks.size > 0) {
                Text(text = "저장 데이터 리스트")
                Spacer(modifier = Modifier.padding(10.dp))
            }
            
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                items(tasks.size) { index ->
                    Text(tasks[index])
                    Spacer(modifier = Modifier.padding(10.dp))
                }
            }

        }
    }
}


@Composable
fun TodoList() {
    val todoService = RetrofitClient.instance.create(TodoService::class.java)
    ToDoListApp(todoService)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TodoList()
}
