package com.tjmaynes.recipebook.core.types

import arrow.core.Either
import arrow.core.Option

interface IRepository<T> {
    suspend fun getAll(request: PaginatedRequest): Either<Exception, List<T>>
    suspend fun getById(id: String): Either<Exception, Option<T>>
    suspend fun addItem(item: T): Either<Exception, T>
    suspend fun updateItem(item: T): Either<Exception, T>
    suspend fun removeItem(id: String): Either<Exception, String>
}
