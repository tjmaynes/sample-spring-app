package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.persistence.domain.Ingredient
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

class Routes(
    private val ingredientHandler: IHandler<Ingredient>
) {
    fun getRouter(): RouterFunction<ServerResponse> = coRouter {
        "/healthcheck" {
            ServerResponse.ok().bodyValue("Healthy").awaitFirst()
        }
        "/ingredient".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/", ingredientHandler::all)
            }
        }
    }
}
