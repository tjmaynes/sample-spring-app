package com.tjmaynes.recipebook.persistence

import arrow.core.Right
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.PaginatedResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux

class DocumentRepositoryTest {
    @Test
    fun `#getAll - should return a Right when items exist`() {
        runBlocking {
            val template = mock<ReactiveMongoTemplate>()
            val request = PaginatedRequest(0, 10)
            val query = Query().with(PageRequest.of(request.pageNumber, request.pageSize))

            val expected = listOf(Ingredient.identity())
            val classType = Ingredient::class.java

            whenever(template.find(query, classType)).thenReturn(Flux.fromIterable(expected))

            val sut = DocumentRepository(template, classType)
            assertEquals(sut.getAll(request), Right(PaginatedResponse(
                    expected, request.pageNumber, request.pageSize
            )))

            verify(template).find(query, Ingredient::class.java)
        }
    }
}