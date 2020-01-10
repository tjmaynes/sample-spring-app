package com.tjmaynes.recipebook.core.types

data class PaginatedRequest(
    val pageNumber: Int,
    val pageSize: Int
)

data class PaginatedResponse<T>(
        val items: List<T>,
        val pageNumber: Int,
        val pageSize: Int
)
