package com.tjmaynes.recipebook.seeder

import arrow.core.Either
import arrow.core.Right
import arrow.core.flatMap
import arrow.core.getOrElse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tjmaynes.recipebook.core.extensions.append
import com.tjmaynes.recipebook.core.service.Service
import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.ServiceException
import com.tjmaynes.recipebook.persistence.domain.Ingredient
import com.tjmaynes.recipebook.persistence.domain.validate
import com.tjmaynes.recipebook.persistence.repository.MongoDbAdapter
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.lang.reflect.Type
import java.time.Instant
import java.util.*

private suspend inline fun <reified T> parseArray(json: String, typeToken: Type) =
    Either.catch { Gson().fromJson<T>(json, typeToken) }

private suspend fun addIngredients(
    service: IService<Ingredient>,
    items: String
) = parseArray<List<Ingredient>>(items, object : TypeToken<List<Ingredient>>() {}.type)
    .getOrElse { emptyList() }
    .map {
        val now = Date.from(Instant.now()).time
        it.copy(createdAt = now, updatedAt = now)
    }
    .map { service.addItem(it) }

private suspend fun <T> getAllItems(
    service: IService<T>,
    totalCount: Long,
    pageNumber: Int = 0,
    pageSize: Int = 10,
    items: MutableCollection<T> = mutableListOf()
): Either<ServiceException, List<T>> = if (items.size <= totalCount) {
    Right(items.toList())
} else {
    service.getAll(PaginatedRequest(pageNumber, pageSize))
        .flatMap { Right(it.items) }
        .flatMap {
            runBlocking {
                getAllItems(
                    service,
                    totalCount,
                    pageNumber + 1,
                    pageSize,
                    items.append(it)
                )
            }
        }
}

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Seeder")

    val connectionString = System.getenv("RECIPE_BOOK_DB_CONNECTION_STRING")
    val rawIngredients = System.getenv("RECIPE_BOOK_RAW_INGREDIENTS")
    val ingredientRepository = MongoDbAdapter.build(connectionString, Ingredient::class.java)
    val ingredientValidator = { item: Ingredient -> item.validate() }
    val ingredientService = Service(ingredientRepository, ingredientValidator)

    runBlocking {
        ingredientRepository.getTotalCount()
            .mapLeft { logger.error(it.localizedMessage) }
            .flatMap { runBlocking { getAllItems(ingredientService, it) } }
            .map { ingredients ->
                ingredients.map {
                    runBlocking { ingredientService.removeItem(it.id) }
                }
            }
            .map { responses ->
                responses.map {
                    when (it) {
                        is Either.Left -> logger.error("Failed: ${it.a.messages}")
                        is Either.Right -> logger.info("Removed: ${it.b}")
                    }
                }
            }

        addIngredients(ingredientService, rawIngredients)
            .map {
                when (it) {
                    is Either.Left -> logger.error("Failed: ${it.a.messages}")
                    is Either.Right -> logger.info("Received: ${it.b}")
                }
            }
    }
}
