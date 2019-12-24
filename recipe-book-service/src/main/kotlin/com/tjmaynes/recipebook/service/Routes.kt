package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.domain.Ingredient
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

class Routes(
       private val ingredientHandler: IHandler<Ingredient>
) {
    fun getRouter() = coRouter {
        "/healthcheck" {
            ServerResponse.ok().bodyValue("Healthy").awaitFirst()
        }
        "/api/v1".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/ingredient", ingredientHandler::all)
            }
        }
    }
}