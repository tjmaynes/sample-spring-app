package com.tjmaynes.recipebook.persistence.repository

import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import com.mongodb.client.result.DeleteResult
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.persistence.domain.Ingredient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class MongoDbAdapterTest {
    private lateinit var template: ReactiveMongoTemplate
    private val classType = Ingredient::class.java
    private val item = Ingredient.identity()

    @BeforeEach
    fun setup() {
        template = mock<ReactiveMongoTemplate>()
    }

    @Test
    fun `#find - should return items when items exist`() {
        runBlocking {
            val request = PaginatedRequest(0, 10)
            val query = Query().with(PageRequest.of(request.pageNumber, request.pageSize))
            val expected = listOf(item)

            whenever(template.find(query, classType)).thenReturn(Flux.fromIterable(expected))

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(expected), sut.find(request))

            verify(template).find(query, classType)
        }
    }

    @Test
    fun `#find - should return an empty when items do not exist`() {
        runBlocking {
            val request = PaginatedRequest(0, 10)
            val query = Query().with(PageRequest.of(request.pageNumber, request.pageSize))

            whenever(template.find(query, classType)).thenReturn(Flux.empty())

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(emptyList<Ingredient>()), sut.find(request))

            verify(template).find(query, classType)
        }
    }

    @Test
    fun `#findById - should return an item when item exists`() {
        runBlocking {
            val template = mock<ReactiveMongoTemplate>()

            whenever(template.findById(item.id.toString(), classType)).thenReturn(Mono.just(item))

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(Some(item)), sut.findById(item.id.toString()))

            verify(template).findById(item.id.toString(), classType)
        }
    }

    @Test
    fun `#findById - should return nothing when item does not exist`() {
        runBlocking {
            whenever(template.findById("some-id", classType)).thenReturn(Mono.empty())

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(None), sut.findById("some-id"))

            verify(template).findById("some-id", classType)
        }
    }

    @Test
    fun `#insert - should return item when item does not not and is able to be inserted`() {
        runBlocking {
            whenever(template.insert(item)).thenReturn(Mono.just(item))

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(item), sut.insert(item))

            verify(template).insert(item)
        }
    }

    @Test
    fun `#insert - should return empty when item is unable to be inserted`() {
        runBlocking {
            whenever(template.insert(item)).thenReturn(Mono.empty<Ingredient>())

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(null), sut.insert(item))

            verify(template).insert(item)
        }
    }

    @Test
    fun `#update - should return item when item has been updated`() {
        runBlocking {
            whenever(template.save(item)).thenReturn(Mono.just(item))

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(item), sut.update(item))

            verify(template).save(item)
        }
    }

    @Test
    fun `#remove - should return item id when item has been removed from database`() {
        runBlocking {
            val query = Query().addCriteria(Criteria(item.id.toString()))
            whenever(template.remove(query, classType)).thenReturn(Mono.just(DeleteResult.acknowledged(1)))

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(Some(item.id.toString())), sut.remove(item.id.toString()))

            verify(template).remove(query, classType)
        }
    }

    @Test
    fun `#remove - should return None when item was not removed from database`() {
        runBlocking {
            val query = Query().addCriteria(Criteria(item.id.toString()))
            whenever(template.remove(query, classType)).thenReturn(Mono.just(DeleteResult.acknowledged(0)))

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(None), sut.remove(item.id.toString()))

            verify(template).remove(query, classType)
        }
    }

    @Test
    fun `#getTotalCount - should return Long when items exist in database`() {
        runBlocking {
            val query = Query()
            val expected = 10.toLong()
            whenever(template.count(query, classType)).thenReturn(Mono.just(expected))

            val sut = MongoDbAdapter(template, classType)
            assertEquals(Right(expected), sut.getTotalCount())

            verify(template).count(query, classType)
        }
    }
}
