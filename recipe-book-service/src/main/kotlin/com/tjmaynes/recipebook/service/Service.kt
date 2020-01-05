package com.tjmaynes.recipebook.service

import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

class Service {
    private val disposableRef = AtomicReference<DisposableServer>()
    private val server: HttpServer

    constructor(httpHandler: ReactorHttpHandlerAdapter, port: Int = 8080) {
        server = HttpServer.create()
                .host("127.0.0.1").port(port)
                .handle(httpHandler)
    }

    companion object {
        fun build(beans: BeanDefinitionDsl, port: Int = 8080) = Service(
            getHttpHandler(getApplicationContext(beans)), port
        )
    }

    fun start() {
        this.disposableRef.set(server.bindNow())
    }

    fun startAndAwait() {
        server.bindUntilJavaShutdown(Duration.ofSeconds(45), null)
    }

    fun stop() {
        disposableRef.get().dispose()
    }
}

fun main(args: Array<String>) = Service.build(beans).startAndAwait()