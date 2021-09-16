package com.kornelos.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class StooqService {
    private val client = HttpClient(CIO)
    private val priceRegex = Regex("(Kurs).*>([0-9]+\\.[0-9]+)</span></b>") // todo: change for more generic one

    private val userAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36",
        "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.18",
        "Opera/9.80 (Linux armv7l) Presto/2.12.407 Version/12.51 , D50u-D1-UHD/V1.5.16-UHD (Vizio, D50u-D1, Wireless)",
        "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1"
    )

    suspend fun getCurrentPrice(ticker: String): String? {
        val price = priceRegex.find(getSiteBody(ticker))
        return price?.groupValues?.last()
    }

    private suspend fun getSiteBody(ticker: String): String {
        val response: HttpResponse = client.request("https://stooq.pl/q/?s=$ticker"){
            method = HttpMethod.Get
            userAgent(generateUserAgent())
            timeout {
                connectTimeoutMillis = 5000
                requestTimeoutMillis = 50000
            }
        }
        return response.receive()
    }



    private fun generateUserAgent(): String {
        return userAgents.random()
    }
}
