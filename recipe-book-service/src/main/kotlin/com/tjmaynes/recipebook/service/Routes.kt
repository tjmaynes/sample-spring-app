package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.domain.Ingredient
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.router

class Routes() {
    fun getRouter() = router {
        "/api/v1".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/ingredient") { handler ->
                  ok().body(BodyInserters.fromValue(Ingredient.identity()))
                }
                POST("/ingredient") { handler ->
                    created(handler.uri()).body(BodyInserters.fromValue(Ingredient.identity()))
                }
            }
        }
    }
}