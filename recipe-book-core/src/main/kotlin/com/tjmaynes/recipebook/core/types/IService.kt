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
    fun getAll(request: PaginatedRequest): ServiceResult<List<T>>
    fun getById(id: String): ServiceResult<T>
    fun addItem(item: T): ServiceResult<T>
    fun updateItem(item: T): ServiceResult<T>
    fun removeItem(id: String): ServiceResult<String>
}
