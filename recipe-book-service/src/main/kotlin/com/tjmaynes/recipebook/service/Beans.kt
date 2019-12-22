package com.tjmaynes.recipebook.service

import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.RouterFunctions

val beans = beans {
    bean<Routes>()
    bean("webHandler") {
        RouterFunctions.toWebHandler(ref<Routes>().getRouter())
    }
}