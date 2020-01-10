package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.types.IService
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.ServiceException
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

interface IHandler<T> {
    suspend fun all(request: ServerRequest): ServerResponse
}

class Handler<T>(private val service: IService<T>) : IHandler<T> {
    override suspend fun all(request: ServerRequest): ServerResponse =
        service.getAll(PaginatedRequest(
            pageNumber = getIntegerOrElse(request.queryParam("pageNumber").orElse("")) { 0 },
            pageSize = getIntegerOrElse(request.queryParam("pageNumber").orElse("")) { 10 }
        )).fold(
            { createNon200Response(it).awaitFirst() },
            { ServerResponse.ok().bodyValue(it).awaitFirst() }
        )

    private fun createNon200Response(exception: ServiceException): Mono<ServerResponse> =
        ServerResponse
            .status(statusCodeToHttpStatus(exception.status))
            .bodyValue(exception.messages)

    private fun statusCodeToHttpStatus(statusCode: ServiceException.StatusCode): HttpStatus =
        when (statusCode) {
            ServiceException.StatusCode.NotFound -> HttpStatus.NOT_FOUND
            ServiceException.StatusCode.BadRequest -> HttpStatus.BAD_REQUEST
            ServiceException.StatusCode.Unknown -> HttpStatus.INTERNAL_SERVER_ERROR
        }

    private fun getIntegerOrElse(s: String, callback: () -> Int) = if (s.matches(Regex("-?[0-9]+"))) {
        s.toInt()
    } else {
        callback()
    }
}
