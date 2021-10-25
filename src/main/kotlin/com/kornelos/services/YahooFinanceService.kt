package com.kornelos.services

import com.google.gson.JsonObject
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

class YahooFinanceService(private val apiKey: String, private val apiUrl: String ) : FinanceService {

    private val client = HttpClient(CIO) {
        install(HttpTimeout)
        install(JsonFeature)
    }

    private val priceCache = ConcurrentHashMap<String, CachedPrice>()
    private val priceUrl = "$apiUrl/v6/finance/quote"

    override suspend fun getCurrentPrice(ticker: String): String? {
        if (priceCache.isValidCache(ticker)) {
            return priceCache[ticker]!!.price
        }

        val response: HttpResponse = client.request("$priceUrl?symbols=$ticker") {
            method = HttpMethod.Get
            headers {
                accept(ContentType.Application.Json)
                set("X-API-KEY", apiKey)
            }
        }
        return try {
            val result = response.receive<JsonObject>()["quoteResponse"].asJsonObject
            result["result"].asJsonArray.first().asJsonObject["regularMarketPrice"].toString()
        } catch(ex: Exception){
            //todo log
            null
        }

    }
}

private fun ConcurrentHashMap<String, CachedPrice>.isValidCache(ticker: String): Boolean {
    return this.contains(ticker) && this[ticker]!!.createdAt.isBefore(LocalDateTime.now().minusDays(1))
}

data class CachedPrice(
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val price: String
)




