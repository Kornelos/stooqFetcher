package com.kornelos.plugins

import com.kornelos.services.CompositeService
import com.kornelos.services.CryptoService
import com.kornelos.services.FinanceService
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
    val cryptoService = CryptoService(
        environment.config.property("coinMarketCap.apiKey").getString(),
        environment.config.property("coinMarketCap.apiUrl").getString(),
    )
    routing {
        get("/") {
            call.respondText("UP")
        }
        get("/api/stooq/{ticker}") {
            val ticker = call.parameters["ticker"]
            val price = callService(stooqService, ticker)
            price?.let { call.respondText(it) } ?: log.info("Price not found for $ticker")
            call.response.status(HttpStatusCode.BadRequest)
        }
        get("/api/price/{ticker}") {
            val ticker = call.parameters["ticker"]
            val price = callService(compositeService, ticker)
            price?.let { call.respondText(it) } ?: log.info("Price not found for $ticker")
            call.response.status(HttpStatusCode.BadRequest)
        }

        get("/api/crypto/{ticker}") {
            val ticker = call.parameters["ticker"]
            val price = callService(cryptoService, ticker)
            price?.let { call.respondText(it) } ?: log.info("Price not found for $ticker")
            call.response.status(HttpStatusCode.BadRequest)
        }
    }

}

suspend fun callService(service: FinanceService, ticker: String?): String? = ticker?.let { service.getCurrentPrice(it) }