package com.tjmaynes.recipebook.persistence

import arrow.core.*
import com.tjmaynes.recipebook.core.types.*

class Repository<T>(private val databaseAdapter: IDatabaseAdapter<T>) : IRepository<T> {
    override suspend fun getAll(request: PaginatedRequest): RepositoryResult<PaginatedResponse<T>> =
        databaseAdapter.find(request)
            .mapLeft { handleException(it) }
            .flatMap { Right(PaginatedResponse(it, request.pageNumber, request.pageSize)) }

    override suspend fun getById(id: String): RepositoryResult<T> =
        databaseAdapter.findById(id)
            .mapLeft { handleException(it) }
            .flatMap { when (it) {
                is Some -> Right(it.t)
                is None -> Left(RepositoryException(
                    status = RepositoryException.StatusCode.NotFound,
                    messages = listOf("Item not found!")
                ))
            } }

    override suspend fun addItem(item: T): RepositoryResult<T> =
        databaseAdapter.insert(item).mapLeft { handleException(it) }

    override suspend fun updateItem(item: T): RepositoryResult<T> =
        databaseAdapter.update(item).mapLeft { handleException(it) }

    override suspend fun removeItem(id: String): RepositoryResult<String> =
        databaseAdapter.remove(id).mapLeft { handleException(it) }
            .flatMap { when (it) {
                is Some -> Right(it.t)
                is None -> Left(RepositoryException(
                    status = RepositoryException.StatusCode.Unknown,
                    messages = listOf("Unable to remove item from database at this time")
                ))
            } }

    private fun handleException(exception: Throwable) =
        RepositoryException(
            status = RepositoryException.StatusCode.Unknown,
            messages = listOf(exception.localizedMessage)
        )
}
