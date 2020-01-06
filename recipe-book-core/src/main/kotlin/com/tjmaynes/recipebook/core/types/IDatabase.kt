package com.tjmaynes.recipebook.core.types

import arrow.core.Either
import arrow.core.Option

interface IDatabase<T> {
    suspend fun find(request: PaginatedRequest): Either<Exception, List<T>>
    suspend fun findById(id: String): Either<Exception, Option<T>>
    suspend fun insert(item: T): Either<Exception, T>
}
