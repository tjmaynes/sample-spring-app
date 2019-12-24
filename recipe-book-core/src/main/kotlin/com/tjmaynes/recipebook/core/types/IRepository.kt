package com.tjmaynes.recipebook.core.types

import arrow.core.Either

data class RepositoryException(
        val status: StatusCode,
        val messages: List<String>
) {
    enum class StatusCode {
        NotFound,
        Unknown
    }
}

typealias Result<T> = Either<RepositoryException, T>

interface IRepository<T> {
    fun getAll(request: PaginatedRequest): Result<List<T>>
    fun getById(id: String): Result<T>
    fun addItem(item: T): Result<T>
    fun updateItem(item: T): Result<T>
    fun removeItem(id: String): Result<String>
}
