package com.tjmaynes.recipebook.service

import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.ServiceException
import org.springframework.context.ApplicationContext
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.core.publisher.Mono

fun getApplicationContext(beans: BeanDefinitionDsl): ApplicationContext =
        GenericApplicationContext().apply {
            beans.initialize(this)
            refresh()
        }

fun getHttpHandler(context: ApplicationContext): ReactorHttpHandlerAdapter =
        ReactorHttpHandlerAdapter(
                WebHttpHandlerBuilder
                        .applicationContext(context)
                        .build()
        )

fun createPaginatedRequest(request: ServerRequest): PaginatedRequest =
        createPaginatedRequest(
                request.queryParam("pageNumber").orElse(""),
                request.queryParam("pageSize").orElse("")
        )

fun createPaginatedRequest(pageNumber: String, pageSize: String): PaginatedRequest =
        PaginatedRequest(
                pageNumber = getIntegerOrElse(pageNumber) { 0 },
                pageSize = getIntegerOrElse(pageSize) { 10 }
        )

fun createNon200Response(exception: ServiceException): Mono<ServerResponse> =
        ServerResponse
                .status(statusCodeToHttpStatus(exception.status))
                .bodyValue(exception.messages)

fun statusCodeToHttpStatus(statusCode: ServiceException.StatusCode): HttpStatus =
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