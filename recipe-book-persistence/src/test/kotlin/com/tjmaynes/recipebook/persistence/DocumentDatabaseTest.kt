package com.tjmaynes.recipebook.persistence

import arrow.core.None
import arrow.core.Some
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class DocumentDatabaseTest {
    private val classType = Ingredient::class.java

    @Test
    fun `#find - should return items when items exist`() {
        runBlocking {
            val template = mock<ReactiveMongoTemplate>()
            val request = PaginatedRequest(0, 10)
            val query = Query().with(PageRequest.of(request.pageNumber, request.pageSize))

            val expected = listOf(Ingredient.identity())

            whenever(template.find(query, classType)).thenReturn(Flux.fromIterable(expected))

            val sut = DocumentDatabase(template, classType)
            assertEquals(sut.find(request), expected)

            verify(template).find(query, classType)
        }
    }

    @Test
    fun `#find - should return an empty when items do not exist`() {
        runBlocking {
            val template = mock<ReactiveMongoTemplate>()
            val request = PaginatedRequest(0, 10)
            val query = Query().with(PageRequest.of(request.pageNumber, request.pageSize))

            whenever(template.find(query, classType)).thenReturn(Flux.empty())

            val sut = DocumentDatabase(template, classType)
            assertEquals(sut.find(request), emptyList<Ingredient>())

            verify(template).find(query, classType)
        }
    }

    @Test
    fun `#findById - should return an item when item exists`() {
        runBlocking {
            val template = mock<ReactiveMongoTemplate>()

            val expected = Ingredient.identity()

            whenever(template.findById(expected.id.toString(), classType)).thenReturn(Mono.just(expected))

            val sut = DocumentDatabase(template, classType)
            assertEquals(sut.findById(expected.id.toString()), Some(expected))

            verify(template).findById(expected.id.toString(), classType)
        }
    }

    @Test
    fun `#findById - should return nothing when item does not exist`() {
        runBlocking {
            val template = mock<ReactiveMongoTemplate>()

            whenever(template.findById("some-id", classType)).thenReturn(Mono.empty())

            val sut = DocumentDatabase(template, classType)
            assertEquals(sut.findById("some-id"), None)

            verify(template).findById("some-id", classType)
        }
    }

    @Test
    fun `#insert - should return item when item does not not and is able to be inserted`() {
        runBlocking {
            val template = mock<ReactiveMongoTemplate>()

            val item = Ingredient.identity()

            whenever(template.insert(item)).thenReturn(Mono.just(item))

            val sut = DocumentDatabase(template, classType)
            assertEquals(sut.insert(item), Some(item))

            verify(template).insert(item)
        }
    }

    @Test
    fun `#insert - should return empty when item is unable to be inserted`() {
        runBlocking {
            val template = mock<ReactiveMongoTemplate>()

            val item = Ingredient.identity()

            whenever(template.insert(item)).thenReturn(Mono.empty())

            val sut = DocumentDatabase(template, classType)
            assertEquals(sut.insert(item), None)

            verify(template).insert(item)
        }
    }
}
