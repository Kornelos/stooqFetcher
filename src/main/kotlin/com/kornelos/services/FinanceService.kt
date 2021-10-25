package com.kornelos.services

interface FinanceService {
    suspend fun getCurrentPrice(ticker: String): String?

}
