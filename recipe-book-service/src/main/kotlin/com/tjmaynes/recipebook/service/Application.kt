package com.tjmaynes.recipebook.service

import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

class Application {
    private val httpHandler: HttpHandler
    private val server: HttpServer
    private val disposableRef = AtomicReference<DisposableServer>()

    constructor(port: Int = 8080) {
        val context = GenericApplicationContext().apply {
            beans.initialize(this)
            refresh()
        }

        server = HttpServer.create().host("127.0.0.1").port(port)
        httpHandler = WebHttpHandlerBuilder
                .applicationContext(context)
                .build()
    }

    fun start() {
        this.disposableRef.set(
                server.handle(ReactorHttpHandlerAdapter(httpHandler)).bindNow()
        )
    }

    fun startAndAwait() {
        server.handle(ReactorHttpHandlerAdapter(httpHandler)).bindUntilJavaShutdown(Duration.ofSeconds(45), null)
    }

    fun stop() {
        disposableRef.get().dispose()
    }
}

fun main(args: Array<String>) = Application().startAndAwait()
