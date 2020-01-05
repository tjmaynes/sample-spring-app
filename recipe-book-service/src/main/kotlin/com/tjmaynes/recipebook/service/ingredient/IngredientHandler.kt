package com.tjmaynes.recipebook.service.ingredient

import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.service.createNon200Response
import com.tjmaynes.recipebook.service.createPaginatedRequest
import com.tjmaynes.recipebook.service.types.IHandler
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class IngredientHandler(
        private val ingredientService: IService<Ingredient>
) : IHandler<Ingredient> {
    override suspend fun all(serverRequest: ServerRequest): ServerResponse =
            ingredientService.getAll(createPaginatedRequest(serverRequest)).fold(
                    { createNon200Response(it).awaitFirst() },
                    { ServerResponse.ok().bodyValue(it).awaitFirst() }
            )
}