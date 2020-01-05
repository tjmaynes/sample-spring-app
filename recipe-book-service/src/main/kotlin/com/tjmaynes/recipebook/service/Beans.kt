package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.service.IngredientService
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.persistence.DocumentDatabase
import com.tjmaynes.recipebook.persistence.DocumentRepository
import com.tjmaynes.recipebook.service.types.IHandler
import com.tjmaynes.recipebook.service.ingredient.IngredientHandler
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.server.WebHandler

val beans = beans {
    // Ingredient API
    bean { DocumentDatabase.build(System.getenv("RECIPE_BOOK_DB_CONNECTION_STRING"), Ingredient::class.java) }
    bean<IRepository<Ingredient>> { DocumentRepository(ref()) }
    bean<IService<Ingredient>> { IngredientService(ref()) }
    bean<IHandler<Ingredient>> { IngredientHandler(ref()) }

    // Router
    bean { Routes(ref()) }
    bean<WebHandler>("webHandler") {
        RouterFunctions.toWebHandler(ref<Routes>().getRouter())
    }
}