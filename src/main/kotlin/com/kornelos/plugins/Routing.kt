package com.kornelos.plugins

import com.kornelos.services.CompositeService
import com.kornelos.services.StooqService
import com.kornelos.services.YahooFinanceService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    val stooqService = StooqService()
    val yahooFinanceService = YahooFinanceService(
        environment.config.property("yahoo.apiKey").getString(),
        environment.config.property("yahoo.apiUrl").getString(),
    )
    val compositeService = CompositeService(yahooFinanceService, stooqService)
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
        get("/api/price/{ticker}") {
            val ticker: String? = call.parameters["ticker"]
            if (ticker != null) {
                val price = compositeService.getCurrentPrice(ticker)
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
