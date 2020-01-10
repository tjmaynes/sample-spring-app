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

interface IService<T> {
    suspend fun getAll(request: PaginatedRequest): Either<ServiceException, PaginatedResponse<T>>
    suspend fun getById(id: String): Either<ServiceException, T>
    suspend fun addItem(item: T): Either<ServiceException, T>
    suspend fun updateItem(item: T): Either<ServiceException, T>
    suspend fun removeItem(id: String?): Either<ServiceException, String>
    suspend fun getCount(): Either<ServiceException, Long>
}
