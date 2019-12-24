package com.tjmaynes.recipebook.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import java.io.FileReader

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiceTest {
    private val application = Service(8181)
    private val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8181")
            .build()

    @BeforeAll
    internal fun beforeAll() {
        application.start()
    }

    @Test
    fun `should return a 200 healthcheck endpoint when calling GET healthcheck`() {
        readRequest("/healthcheck")
                .expectStatus().isEqualTo(HttpStatus.OK)
    }

    @AfterAll
    fun shutOffServer() {
        application.stop()
    }

    private fun readRequest(endpoint: String) =
            client.get().uri(endpoint).exchange()
}

fun <T> getJsonDataFromFile(fileLocation: String) =
        Gson().fromJson<T>(
                FileReader(fileLocation),
                object : TypeToken<T>() {}.type
        )