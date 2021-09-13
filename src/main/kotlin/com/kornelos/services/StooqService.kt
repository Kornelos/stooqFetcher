package com.kornelos.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class StooqService {
    private val client = HttpClient(CIO)
    private val priceRegex = Regex("(Kurs).*>([0-9]+\\.[0-9])</span></b>")

    suspend fun getCurrentPrice(ticker: String): String? {
        val price = priceRegex.find(getSiteBody(ticker))
        return price?.groupValues?.last()
    }

    private suspend fun getSiteBody(ticker: String): String {
        val response: HttpResponse = client.request("https://stooq.pl/q/?s=$ticker"){
            method = HttpMethod.Get
        }
        return response.receive()
    }
}