package com.example.web

import com.example.factory.DatabaseFactory.dbQuery
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*

object Todos : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val task: Column<String> = varchar("task", 4000)
}

data class NewTodo(
    val id: Int?,
    val task: String
)

data class Response(
    val id: Int,
    val task: String
)

fun convertTodo(row: ResultRow): Response =
    Response(
        id = row[Todos.id],
        task = row[Todos.task]
    )

suspend fun addTodo(todo: NewTodo): Response {
    var key = 0
    dbQuery {
        key = (Todos.insert { it[task] = todo.task } get Todos.id)
    }
    return getTodo(key)!!
}

suspend fun getTodo(id: Int): Response? = dbQuery {
    Todos.select {
        (Todos.id eq id)
    }.mapNotNull { convertTodo(it) }
        .singleOrNull()
}

suspend fun getAllTodos(): List<Response> = dbQuery {
    Todos.selectAll().map { convertTodo(it) }
}

fun Route.todos() {
    route("/todos") {
        get("/") {
            call.respond(getAllTodos())
        }

        post("/") {
            val newTodo = call.receive<NewTodo>()
            call.respond(HttpStatusCode.Created, addTodo(newTodo))
        }
    }
}