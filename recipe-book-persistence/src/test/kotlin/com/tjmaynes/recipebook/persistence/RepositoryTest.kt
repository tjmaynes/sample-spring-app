package com.tjmaynes.recipebook.persistence

import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import com.nhaarman.mockitokotlin2.*
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.PaginatedResponse
import com.tjmaynes.recipebook.core.types.RepositoryException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RepositoryTest {
    @Test
    fun `#getAll - should return a PaginatedResponse when items exist`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val request = PaginatedRequest(0, 10)

            val expected = listOf(Ingredient.identity())

            whenever(database.find(request)).thenReturn(Right(expected))

            val sut = Repository(database)
            assertEquals(Right(PaginatedResponse(
                expected, request.pageNumber, request.pageSize
            )), sut.getAll(request))

            verify(database).find(request)
        }
    }

    @Test
    fun `#getAll - should return an empty PaginatedResponse when no items exist`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val request = PaginatedRequest(0, 10)

            whenever(database.find(request)).thenReturn(Right(emptyList()))

            val sut = Repository(database)
            assertEquals(Right(PaginatedResponse(
                emptyList<Ingredient>(), 0, 10
            )), sut.getAll(request))

            verify(database).find(request)
        }
    }

    @Test
    fun `#getAll - should return a RepositoryException if something goes wrong`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val request = PaginatedRequest(0, 10)

            whenever(database.find(request)).thenReturn(Left(Exception("something happened")))

            val sut = Repository(database)
            assertEquals(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.getAll(request))

            verify(database).find(request)
        }
    }

    @Test
    fun `#getById - should return an item by id when item exists`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val id = "some-id"
            val ingredient = Ingredient.identity()

            whenever(database.findById(id)).thenReturn(Right(Some(ingredient)))

            val sut = Repository(database)
            assertEquals(Right(ingredient), sut.getById(id))

            verify(database).findById(id)
        }
    }

    @Test
    fun `#getById - should return a RepositoryException when requesting a non-existent item`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val id = "some-id"

            whenever(database.findById(id)).thenReturn(Right(None))

            val sut = Repository(database)
            assertEquals(Left(RepositoryException(
                status = RepositoryException.StatusCode.NotFound,
                messages = listOf("Item not found!")
            )), sut.getById(id))

            verify(database).findById(id)
        }
    }

    @Test
    fun `#addItem - should be able to add item if item does not already exist in repository`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val expected = Ingredient.identity()

            whenever(database.insert(expected)).thenReturn(Right(expected))

            val sut = Repository(database)
            assertEquals(Right(expected), sut.addItem(expected))

            verify(database).insert(expected)
        }
    }

    @Test
    fun `#addItem - should return a RepositoryException if something goes wrong`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val item = Ingredient.identity()

            whenever(database.insert(item)).thenReturn(Left(Exception("something happened")))

            val sut = Repository(database)
            assertEquals(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.addItem(item))

            verify(database).insert(item)
        }
    }

    @Test
    fun `#updateItem - should return an item after updating an existing item`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val item = Ingredient.identity()

            whenever(database.update(item)).thenReturn(Right(item))

            val sut = Repository(database)
            assertEquals(Right(item), sut.updateItem(item))

            verify(database).update(item)
        }
    }

    @Test
    fun `#updateItem - should return a RepositoryException if something goes wrong`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val item = Ingredient.identity()

            whenever(database.update(item)).thenReturn(Left(Exception("something happened")))

            val sut = Repository(database)
            assertEquals(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.updateItem(item))

            verify(database).update(item)
        }
    }

    @Test
    fun `#removeItem - should return item id if item exists`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val itemId = "some-id"

            whenever(database.remove(itemId)).thenReturn(Right(Some(itemId)))

            val sut = Repository(database)
            assertEquals(Right(itemId), sut.removeItem(itemId))

            verify(database).remove(itemId)
        }
    }

    @Test
    fun `#removeItem - should return RepositoryException if item is unable to be removed`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val item = Ingredient.identity()

            whenever(database.remove(item.id.toString())).thenReturn(Right(None))

            val sut = Repository(database)
            assertEquals(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Unable to remove item from database at this time")
            )), sut.removeItem(item.id.toString()))

            verify(database).remove(item.id.toString())
        }
    }

    @Test
    fun `#removeItem - should return RepositoryException if exception is thrown`() {
        runBlocking {
            val database = mock<IDatabaseAdapter<Ingredient>>()
            val item = Ingredient.identity()

            whenever(database.remove(item.id.toString())).thenReturn(Left(Exception("something happened")))

            val sut = Repository(database)
            assertEquals(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.removeItem(item.id.toString()))

            verify(database).remove(item.id.toString())
        }
    }
}
