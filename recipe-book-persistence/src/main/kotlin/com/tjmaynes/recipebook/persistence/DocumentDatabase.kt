package com.tjmaynes.recipebook.persistence

import arrow.core.*
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.mongodb.ConnectionString
import com.tjmaynes.recipebook.core.types.IDatabase
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.query.Query
import java.util.*

class DocumentDatabase<T>(
    private val template: ReactiveMongoTemplate,
    private val classType: Class<T>
) : IDatabase<T> {
    override suspend fun find(request: PaginatedRequest): Either<Exception, List<T>> =
        IO.fx {
            val query = Query().with(PageRequest.of(request.pageNumber, request.pageSize))
            !effect {
                try {
                    val results = template
                        .find(query, classType)
                        .buffer(request.pageSize - 1)
                        .awaitLast()
                    Right(results)
                } catch (e: NoSuchElementException) {
                    Right(emptyList<T>())
                }
            }
        }.suspended()

    override suspend fun findById(id: String): Either<Exception, Option<T>> =
        IO.fx {
            !effect {
                try {
                    val results = template.findById(id, classType).block().toOption()
                    Right(results)
                } catch (e: Exception) {
                    Left(e)
                }
            }
        }.suspended()

    override suspend fun insert(item: T): Either<Exception, T> =
        IO.fx {
            !effect {
                try {
                    val results = template.insert(item).block()
                    Right(results)
                } catch (e: Exception) {
                    Left(e)
                }
            }
        }.suspended()

    companion object {
        fun <T> build(connectionURL: String, classType: Class<T>): IDatabase<T> =
            DocumentDatabase(
                ReactiveMongoTemplate(
                    SimpleReactiveMongoDatabaseFactory(ConnectionString(connectionURL))
                ),
                classType
            )
    }
}
