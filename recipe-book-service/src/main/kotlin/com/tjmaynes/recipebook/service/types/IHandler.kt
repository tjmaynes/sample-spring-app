package com.tjmaynes.recipebook.service.types

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface IHandler<T> {
    suspend fun all(serverRequest: ServerRequest): ServerResponse
}