package com.tjmaynes.recipebook.persistence

import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.IDatabase
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.PaginatedResponse
import com.tjmaynes.recipebook.core.types.RepositoryException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DocumentRepositoryTest {
    @Test
    fun `#getAll - should return a PaginatedResponse when items exist`() {
        runBlocking {
            val database = mock<IDatabase<Ingredient>>()
            val request = PaginatedRequest(0, 10)

            val expected = listOf(Ingredient.identity())

            whenever(database.find(request)).thenReturn(expected)

            val sut = DocumentRepository(database)
            assertEquals(sut.getAll(request), Right(PaginatedResponse(
                expected, request.pageNumber, request.pageSize
            )))

            verify(database).find(request)
        }
    }

    @Test
    fun `#getAll - should return a RepositoryException when no items exist`() {
        runBlocking {
            val database = mock<IDatabase<Ingredient>>()
            val request = PaginatedRequest(0, 10)

            whenever(database.find(request)).thenReturn(emptyList())

            val sut = DocumentRepository(database)
            assertEquals(sut.getAll(request), Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Unexpected error has occurred.")
            )))

            verify(database).find(request)
        }
    }

    @Test
    fun `#getById - should return an item by id when item exists`() {
        runBlocking {
            val database = mock<IDatabase<Ingredient>>()
            val id = "some-id"
            val ingredient = Ingredient.identity()

            whenever(database.findById(id)).thenReturn(Some(ingredient))

            val sut = DocumentRepository(database)
            assertEquals(sut.getById(id), Right(ingredient))

            verify(database).findById(id)
        }
    }

    @Test
    fun `#getById - should return a RepositoryException when requesting a non-existant item`() {
        runBlocking {
            val database = mock<IDatabase<Ingredient>>()
            val id = "some-id"

            whenever(database.findById(id)).thenReturn(None)

            val sut = DocumentRepository(database)
            assertEquals(sut.getById(id), Left(RepositoryException(
                status = RepositoryException.StatusCode.NotFound,
                messages = listOf("Item not found!")
            )))

            verify(database).findById(id)
        }
    }

    @Test
    fun `#addItem - should be able to add item if item does not already exist in repository`() {
        runBlocking {
            val database = mock<IDatabase<Ingredient>>()
            val expected = Ingredient.identity()

            whenever(database.insert(expected)).thenReturn(Some(expected))

            val sut = DocumentRepository(database)
            assertEquals(sut.addItem(expected), Right(expected))

            verify(database).insert(expected)
        }
    }

    fun `#addItem - should not be able to add item if item already exists in repository`() {
        runBlocking {
            val database = mock<IDatabase<Ingredient>>()
            val expected = Ingredient.identity()

//            whenever(database.insert(expected)).thenReturn(Exception())

            val sut = DocumentRepository(database)
            assertEquals(sut.addItem(expected), Right(expected))

            verify(database).insert(expected)
        }
    }
}
