package com.kornelos.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class StooqService {
    private val client = HttpClient(CIO)
    private val priceRegex = Regex("(Kurs).*>([0-9]+\\.[0-9]+)</span></b>") // todo: change for more generic one

    suspend fun getCurrentPrice(ticker: String): String? {
        val price = priceRegex.find(getSiteBody(ticker))
        return price?.groupValues?.last()
    }

    private suspend fun getSiteBody(ticker: String): String {
        val response: HttpResponse = client.request("https://stooq.pl/q/?s=$ticker"){
            method = HttpMethod.Get
            userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1")
        }
        return response.receive()
    }
}
