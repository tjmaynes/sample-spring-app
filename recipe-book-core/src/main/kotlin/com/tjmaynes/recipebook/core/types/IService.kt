package com.tjmaynes.recipebook.core.types

import arrow.core.Either

data class ServiceException(
        val status: StatusCode,
        val messages: List<String>
) {
    enum class StatusCode {
        NotFound,
        BadRequest,
        Unknown
    }
}

typealias ServiceResult<T> = Either<ServiceException, T>

interface IService<T> {
    suspend fun getAll(request: PaginatedRequest): ServiceResult<List<T>>
    suspend fun getById(id: String): ServiceResult<T>
    suspend fun addItem(item: T): ServiceResult<T>
    suspend fun updateItem(item: T): ServiceResult<T>
    suspend fun removeItem(id: String): ServiceResult<String>
}
