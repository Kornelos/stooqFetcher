package com.kornelos.services

class CompositeService(
    private val yahooFinanceService: YahooFinanceService,
    private val stooqService: StooqService
) : FinanceService {
    override suspend fun getCurrentPrice(ticker: String): String? =
        yahooFinanceService.getCurrentPrice(ticker) ?: stooqService.getCurrentPrice(ticker)
}