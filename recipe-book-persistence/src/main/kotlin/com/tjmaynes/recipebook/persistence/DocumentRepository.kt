package com.tjmaynes.recipebook.persistence

import arrow.core.Either
import arrow.core.Option
import arrow.core.Right
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.PaginatedResponse
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query

class DocumentRepository<T>(
        private val template: ReactiveMongoTemplate,
        private val classType: Class<T>
): IRepository<T> {
    override suspend fun getAll(request: PaginatedRequest): Either<Exception, PaginatedResponse<T>> =
            IO.fx {
                !effect {
                    val query = Query().with(PageRequest.of(request.pageNumber, request.pageSize))
                    val r = template.find(query, classType).awaitLast()
                    Right(PaginatedResponse(listOf(r), request.pageNumber, request.pageSize))
                }
            }.suspended()

    override suspend fun getById(id: String): Either<Exception, Option<T>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun addItem(item: T): Either<Exception, T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateItem(item: T): Either<Exception, T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun removeItem(id: String): Either<Exception, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}