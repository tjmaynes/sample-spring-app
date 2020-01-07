package com.tjmaynes.recipebook.seeder

import arrow.core.Either
import arrow.core.Option
import arrow.core.extensions.either.applicative.applicative
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.service.IngredientService
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.core.types.ServiceException
import com.tjmaynes.recipebook.core.types.ServiceResult
import com.tjmaynes.recipebook.persistence.MongoDbAdapter
import com.tjmaynes.recipebook.persistence.Repository
import java.io.FileReader

suspend fun seedIngredients(ingredientService: IService<Ingredient>): List<ServiceResult<Ingredient>> =
    Either.applicative<Either<ServiceException, Option<List<Ingredient>>>>().run {
        getJsonDataFromFile<List<Ingredient>>("./src/main/kotlin/")
            .map { ingredientService.addItem(it) }
    }

private fun <T> getJsonDataFromFile(fileLocation: String) =
    Gson().fromJson<T>(
        FileReader(fileLocation),
        object : TypeToken<T>() {}.type
    )

private fun <T> createRepository(connectionString: String, classType: Class<T>): IRepository<T> =
    Repository(MongoDbAdapter.build(connectionString, classType))

fun main(args: Array<String>) {
    val connectionString = System.getenv("RECIPE_BOOK_DB_CONNECTION_STRING")
    val ingredientService = IngredientService(
        createRepository(connectionString, Ingredient::class.java)
    )

//    runBlocking {
//        seedIngredients(ingredientService).flatMap { ingredientResults ->
//            when (ingredientResults) {
//                is Either.Left -> Left()
//            }
//        }
//    }
}
