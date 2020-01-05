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

typealias RepositoryResult<T> = Either<RepositoryException, T>

interface IRepository<T> {
    suspend fun getAll(request: PaginatedRequest): RepositoryResult<PaginatedResponse<T>>
    suspend fun getById(id: String): RepositoryResult<T>
    suspend fun addItem(item: T): RepositoryResult<T>
    suspend fun updateItem(item: T): RepositoryResult<T>
    suspend fun removeItem(id: String): RepositoryResult<String>
}
