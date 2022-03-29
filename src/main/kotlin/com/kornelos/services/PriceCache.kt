package com.kornelos.services

import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class PriceCache(private val cacheValidity: Duration = Duration.ofDays(1)) {

    private val cache = ConcurrentHashMap<String, CachedPrice>()

    operator fun get(ticker: String): CachedPrice? {
        return cache[ticker]
    }

    fun isValidCache(ticker: String): Boolean {
        return cache.contains(ticker) && cache[ticker]!!.createdAt.isBefore(LocalDateTime.now().minus(cacheValidity))
    }

    operator fun set(ticker: String, value: String) {
        cache[ticker] = CachedPrice(price = value)
    }

    data class CachedPrice(
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val price: String
    )
}