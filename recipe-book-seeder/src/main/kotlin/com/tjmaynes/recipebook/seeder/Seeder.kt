package com.tjmaynes.recipebook.seeder

import arrow.core.Either
import arrow.core.Right
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.service.IngredientService
import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.core.types.ServiceException
import com.tjmaynes.recipebook.persistence.DocumentDatabase
import com.tjmaynes.recipebook.persistence.DocumentRepository
import kotlinx.coroutines.runBlocking
import java.io.FileReader

suspend fun seed(ingredientService: IService<Ingredient>): Either<ServiceException, List<Ingredient>> {
    val ingredients: List<Ingredient> = getJsonDataFromFile("./src/")
    return Right(listOf(Ingredient.identity()))
}

private fun <T> getJsonDataFromFile(fileLocation: String) =
        Gson().fromJson<T>(
                FileReader(fileLocation),
                object : TypeToken<T>() {}.type
        )

private fun createIngredientService(connectionString: String): IService<Ingredient> {
    val ingredientDatabase = DocumentDatabase.build(connectionString, Ingredient::class.java)
    val ingredientRepository = DocumentRepository(ingredientDatabase)
    return IngredientService(ingredientRepository)
}

fun main(args: Array<String>) {
    val connectionString = System.getenv("RECIPE_BOOK_DB_CONNECTION_STRING")
    val ingredientService = createIngredientService(connectionString)

    runBlocking {
        seed(ingredientService).fold(
                {},
                {}
        )
    }
}
