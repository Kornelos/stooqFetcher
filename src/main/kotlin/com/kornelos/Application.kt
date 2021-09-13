package com.kornelos

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kornelos.plugins.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
        configureRouting()
    }.start(wait = true)
}
