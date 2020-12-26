package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import com.example.factory.DatabaseFactory
import com.example.web.todos
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    DatabaseFactory.init()

    install(Routing) {
        todos()
    }
}

