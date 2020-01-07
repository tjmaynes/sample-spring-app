package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.types.IService
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface IHandler<T> {
    suspend fun all(serverRequest: ServerRequest): ServerResponse
}

class Handler<T>(private val service: IService<T>) : IHandler<T> {
    override suspend fun all(serverRequest: ServerRequest): ServerResponse =
        service.getAll(createPaginatedRequest(serverRequest)).fold(
            { createNon200Response(it).awaitFirst() },
            { ServerResponse.ok().bodyValue(it).awaitFirst() }
        )
}
