package com.tjmaynes.recipebook.core.types

import arrow.core.Option

interface IDatabase<T> {
    suspend fun find(request: PaginatedRequest): List<T>
    suspend fun findById(id: String): Option<T>
    suspend fun insert(item: T): Option<T>
}
