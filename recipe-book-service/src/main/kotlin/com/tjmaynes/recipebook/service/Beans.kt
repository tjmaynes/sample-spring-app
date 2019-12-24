package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.service.IngredientService
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.Result
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.server.WebHandler

class IngredientRepository(): IRepository<Ingredient> {
    override fun getAll(request: PaginatedRequest): Result<List<Ingredient>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getById(id: String): Result<Ingredient> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addItem(item: Ingredient): Result<Ingredient> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateItem(item: Ingredient): Result<Ingredient> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeItem(id: String): Result<String> {
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