package com.tjmaynes.recipebook.service

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.service.IngredientService
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.persistence.DocumentRepository
import org.springframework.context.support.beans
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.server.WebHandler

val beans = beans {
    // Database
    bean<MongoClient> { MongoClients.create() }
    bean { ReactiveMongoTemplate(ref(), "some-database") }

    // Ingredient API
    bean<IRepository<Ingredient>> { DocumentRepository(ref(), Ingredient::class.java) }
    bean<IService<Ingredient>> { IngredientService(ref()) }
    bean<IHandler<Ingredient>> { CrudHandler(ref()) }

    bean<Routes>()
    bean<WebHandler>("webHandler") {
        RouterFunctions.toWebHandler(ref<Routes>().getRouter())
    }
}