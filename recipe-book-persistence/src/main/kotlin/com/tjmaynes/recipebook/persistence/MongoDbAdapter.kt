package com.tjmaynes.recipebook.persistence

import arrow.core.*
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.mongodb.ConnectionString
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class MongoDbAdapter<T>(
    private val template: ReactiveMongoTemplate,
    private val classType: Class<T>
) : IDatabaseAdapter<T> {
    override suspend fun find(request: PaginatedRequest): Either<Throwable, List<T>> =
        IO.fx {
            val query = Query().with(PageRequest.of(request.pageNumber, request.pageSize))
            !effect {
                Either.catch {
                    template
                        .find(query, classType)
                        .buffer(request.pageSize - 1)
                        .awaitLast()
                }.fold(
                    { when (it) {
                        is NoSuchElementException -> Right(emptyList<T>())
                        else -> Left(it)
                    } },
                    { Right(it) }
                )
            }
        }.suspended()

    override suspend fun findById(id: String): Either<Throwable, Option<T>> =
        IO.fx {
            !effect { Either.catch { template.findById(id, classType).block().toOption() } }
        }.suspended()

    override suspend fun insert(item: T): Either<Throwable, T> =
        IO.fx {
            !effect { Either.catch { template.insert(item).block() } }
        }.suspended()

    override suspend fun update(item: T): Either<Throwable, T> =
        IO.fx {
            !effect { Either.catch { template.save(item).block() } }
        }.suspended()

    override suspend fun remove(id: String): Either<Throwable, Option<String>> =
        IO.fx {
            val query = Query().addCriteria(Criteria(id))
            !effect {
                Either.catch { template.remove(query, classType).block() }.flatMap { result ->
                    when (val deleteResult = result.toOption()) {
                        is Some -> if (deleteResult.t.wasAcknowledged() && deleteResult.t.deletedCount >= 1) {
                            Right(Some(id))
                        } else {
                            Right(None)
                        }
                        is None -> Left(Exception("Unable to delete item $id at this time."))
                    }
                }
            }
        }.suspended()

    companion object {
        fun <T> build(connectionURL: String, classType: Class<T>): IDatabaseAdapter<T> =
            MongoDbAdapter(
                ReactiveMongoTemplate(
                    SimpleReactiveMongoDatabaseFactory(ConnectionString(connectionURL))
                ),
                classType
            )
    }
}
