package com.tjmaynes.recipebook.core.types

data class PaginatedRequest(
        val pageNumber: Int,
        val pageSize: Int
)