package com.kornelos.plugins

import com.kornelos.services.StooqService
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    val stooqService = StooqService()

    routing {
        get("/") {
            call.respondText("UP")
        }
        get("/api/stooq/{ticker}"){
            val ticker: String? = call.parameters["ticker"]
            if (ticker != null) {
                stooqService.getCurrentPrice(ticker)?.let { it1 -> call.respondText(it1) }
            }
            call.response.status(HttpStatusCode.BadRequest)
        }
    }

}
