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

class CryptoService(private val apiKey: String, apiUrl: String) : FinanceService {

    companion object {
        val logger by logger()
    }

    private val client = HttpClient(CIO) {
        install(HttpTimeout)
        install(JsonFeature)
    }

    private val priceCache = PriceCache()
    private val priceUrl = "$apiUrl/v1/cryptocurrency/listings/latest"


    override suspend fun getCurrentPrice(ticker: String): String? {
        return if (priceCache.isValidCache(ticker)) {
            priceCache[ticker]?.price
        } else {
            refreshCache()
            priceCache[ticker]?.price
        }
    }

    private suspend fun refreshCache() {
        try {
            val response: HttpResponse = client.request(priceUrl) {
                method = HttpMethod.Get
                headers {
                    accept(ContentType.Application.Json)
                    header("X-CMC_PRO_API_KEY", apiKey)
                }
            }
            val result = response.receive<JsonObject>()["data"].asJsonArray
            result.map { priceCache[it.asJsonObject["symbol"].toString()] = it.asJsonObject["quote"].asJsonObject["USD"].asJsonObject["price"].toString() }
        } catch (ex: Exception) {
            logger.error("Exception while refreshing cache: $ex")
        }
    }

}