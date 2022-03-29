package com.kornelos.services

import com.google.gson.JsonObject
import com.kornelos.logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class YahooFinanceService(private val apiKey: String, apiUrl: String) : FinanceService {

    companion object {
        val logger by logger()
    }

    private val client = HttpClient(CIO) {
        install(HttpTimeout)
        install(JsonFeature)
    }

    private val priceUrl = "$apiUrl/v6/finance/quote"
    private val priceCache = PriceCache()

    override suspend fun getCurrentPrice(ticker: String): String? {
        if (priceCache.isValidCache(ticker)) {
            return priceCache[ticker]!!.price
        }

        val response: HttpResponse = client.request(priceUrl) {
            method = HttpMethod.Get
            headers {
                parameter("symbols", ticker)
                accept(ContentType.Application.Json)
                header("X-API-KEY", apiKey)
            }
        }
        return try {
            val result = response.receive<JsonObject>()["quoteResponse"].asJsonObject
            result["result"].asJsonArray.first().asJsonObject["regularMarketPrice"].toString()
        } catch(ex: Exception){
            logger.error("Exception while getting price: $ex")
            null
        }

    }
}




