package com.tjmaynes.recipebook.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tjmaynes.recipebook.core.domain.Ingredient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.io.FileReader

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeBookApplicationTests {
    private val application = Application(8181)
    private val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8181")
            .build()

    private lateinit var ingredients: List<Ingredient>

    private val givenAnIngredient = Ingredient.identity()

    @BeforeAll
    internal fun beforeAll() {
        application.start()
    }

    @BeforeEach
    fun setup() {
        ingredients = getJsonDataFromFile<List<Ingredient>>(
                ""
        )
        writeRequest(HttpMethod.POST, "/api/v1/ingredient", ingredients)
                .expectStatus().isCreated
    }

    fun `should be able to return all ingredients when ingredients exist`() {
        readRequest("/api/v1/ingredient")
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$").isEqualTo(ingredients)
    }

    fun `should be able to return an ingredient by id when ingredients exist`() {
        val ingredient = ingredients.first()

        readRequest("/api/v1/ingredient?id=${ingredient.id}")
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$").isEqualTo(ingredient)
    }

    fun `should be able to create an ingredient when given a valid ingredient`() {
		val ingredient = givenAnIngredient

		writeRequest(HttpMethod.POST, "/api/v1/ingredient", ingredient)
				.expectStatus().isCreated
				.expectBody()
				.jsonPath("$").isEqualTo(ingredient)
    }

    fun `should be able to update an ingredient when given a valid ingredient`() {
		val ingredient = givenAnIngredient

		writeRequest(HttpMethod.PUT, "/api/v1/ingredient", ingredient)
				.expectStatus().isCreated
				.expectBody()
				.jsonPath("$").isEqualTo(ingredient)
    }

    fun `should be able to remove an ingredient by id when a specific ingredient exists`() {
        val ingredient = ingredients.first()

        removeRequest("/api/v1/ingredient?id=${ingredient.id}")
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$").isEqualTo(ingredient)
    }

    @AfterAll
    fun shutOffServer() {
        application.stop()
    }

    private fun <T> writeRequest(
            httpMethod: HttpMethod = HttpMethod.POST,
            endpoint: String,
            body: T? = null
    ) = client
            .method(httpMethod)
            .uri(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(body!!))
            .exchange()

    private fun readRequest(endpoint: String) =
            client.get().uri(endpoint).exchange()

    private fun removeRequest(endpoint: String) =
            client.delete().uri(endpoint).exchange()
}

fun <T> getJsonDataFromFile(fileLocation: String) =
        Gson().fromJson<T>(
                FileReader(fileLocation),
                object: TypeToken<T>() {}.type
        )