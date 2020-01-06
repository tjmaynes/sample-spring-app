package com.tjmaynes.recipebook.persistence

import arrow.core.Either
import arrow.core.Option
import com.tjmaynes.recipebook.core.types.PaginatedRequest

interface IDatabaseAdapter<T> {
    suspend fun find(request: PaginatedRequest): Either<Throwable, List<T>>
    suspend fun findById(id: String): Either<Throwable, Option<T>>
    suspend fun insert(item: T): Either<Throwable, T>
    suspend fun update(item: T): Either<Throwable, T>
    suspend fun remove(id: String): Either<Throwable, Option<String>>
}
