package com.tjmaynes.recipebook.service

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiceTest {
    private val port = 8181
    private val application = Service.build(beans, port)
    private val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:${port}")
            .build()

    @BeforeAll
    internal fun beforeAll() {
        application.start()
    }

    @Test
    fun `should return a 200 status when calling GET healthcheck`() {
        readRequest("/healthcheck")
                .expectStatus().isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should be able to return all ingredients when ingredients exist`() {
//        readRequest("/ingredient?pageNumber=0&pageSize=10")
//                .expectStatus().isOk
//                .expectBody()
//                .jsonPath("$").isEqualTo("")
    }

    @Test
    fun `should be able to return an ingredient by id when ingredients exist`() {
//        val ingredient = ingredients.first()

//        readRequest("/api/v1/ingredient/${ingredient.id}")
//                .expectStatus().isOk
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredient)
    }

    @Test
    fun `should be able to create an ingredient when given a valid ingredient`() {
//        val ingredient = givenAnIngredient

//        writeRequest(HttpMethod.POST, "/api/v1/ingredient", ingredient)
//                .expectStatus().isCreated
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredient)
    }

    @Test
    fun `should be able to update an ingredient when given a valid ingredient`() {
//        val ingredient = givenAnIngredient

//        writeRequest(HttpMethod.PUT, "/api/v1/ingredient", ingredient)
//                .expectStatus().isCreated
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredient)
    }

    @Test
    fun `should be able to remove an ingredient by id when a specific ingredient exists`() {
//        val ingredient = ingredients.first()

//        removeRequest("/api/v1/ingredient?id=${ingredient.id}")
//                .expectStatus().isOk
//                .expectBody()
//                .jsonPath("$").isEqualTo(ingredient)
    }

    @AfterAll
    fun shutOffServer() {
        application.stop()
    }

    private fun readRequest(endpoint: String) =
            client.get().uri(endpoint).accept(MediaType.APPLICATION_JSON).exchange()
}
