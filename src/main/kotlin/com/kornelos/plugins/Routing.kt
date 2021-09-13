package com.kornelos.plugins

import com.kornelos.services.StooqService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    val stooqService = StooqService()

    routing {
        get("/") {
            call.respondText("UP")
        }
        get("/api/stooq/{ticker}") {
            val ticker: String? = call.parameters["ticker"]
            if (ticker != null) {
                val price = stooqService.getCurrentPrice(ticker)
                if (price != null) {
                    call.respondText(price)
                } else {
                    log.info("Price not found for $ticker")
                }
            }
            call.response.status(HttpStatusCode.BadRequest)
        }
    }

}
