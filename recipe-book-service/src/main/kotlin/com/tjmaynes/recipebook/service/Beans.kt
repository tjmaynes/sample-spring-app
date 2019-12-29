package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.service.IngredientService
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.server.WebHandler
import arrow.core.Either
import arrow.core.Option

class IngredientRepository(): IRepository<Ingredient> {
    override suspend fun getAll(request: PaginatedRequest): Either<Exception, List<Ingredient>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getById(id: String): Either<Exception, Option<Ingredient>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun addItem(item: Ingredient): Either<Exception, Ingredient> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updateItem(item: Ingredient): Either<Exception, Ingredient> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun removeItem(id: String): Either<Exception, String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

val beans = beans {
    bean<IRepository<Ingredient>> {
        IngredientRepository()
    }

    bean<IService<Ingredient>> {
        IngredientService(ref())
    }
    bean<IHandler<Ingredient>> {
        CrudHandler<Ingredient>(ref())
    }
    bean<Routes>()
    bean<WebHandler>("webHandler") {
        RouterFunctions.toWebHandler(ref<Routes>().getRouter())
    }
}