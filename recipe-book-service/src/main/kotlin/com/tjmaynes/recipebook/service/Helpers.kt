package com.tjmaynes.recipebook.service

import org.springframework.context.ApplicationContext
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder

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
