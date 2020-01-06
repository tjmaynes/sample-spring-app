package com.tjmaynes.recipebook.persistence

import arrow.core.*
import com.tjmaynes.recipebook.core.types.*

class DocumentRepository<T>(private val database: IDatabase<T>) : IRepository<T> {
    override suspend fun getAll(request: PaginatedRequest): RepositoryResult<PaginatedResponse<T>> =
        database.find(request).mapLeft {
            RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Unexpected error has occurred.")
            )
        }.flatMap { Right(PaginatedResponse(it, request.pageNumber, request.pageSize)) }

    override suspend fun getById(id: String): RepositoryResult<T> =
        database.findById(id).mapLeft {
            RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Unexpected error has occurred.")
            )
        }.flatMap {
            when (it) {
                is Some -> Right(it.t)
                is None -> Left(RepositoryException(
                    status = RepositoryException.StatusCode.NotFound,
                    messages = listOf("Item not found!")
                ))
            }
        }

    override suspend fun addItem(item: T): RepositoryResult<T> =
        database.insert(item).mapLeft {
            RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Unexpected error has occurred.")
            )
        }.flatMap { Right(it) }

    override suspend fun updateItem(item: T): RepositoryResult<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun removeItem(id: String): RepositoryResult<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
