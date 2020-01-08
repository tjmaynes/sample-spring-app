package com.tjmaynes.recipebook.persistence

import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.service.IngredientService
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.PaginatedResponse
import com.tjmaynes.recipebook.core.types.ServiceException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IngredientServiceTest {
    @Test
    fun `#getAll - should return a PaginatedResponse when items exist`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val request = PaginatedRequest(0, 10)

            val expected = listOf(Ingredient.identity())

            whenever(repository.find(request)).thenReturn(Right(expected))

            val sut = IngredientService(repository)
            assertEquals(Right(PaginatedResponse(
                expected, request.pageNumber, request.pageSize
            )), sut.getAll(request))

            verify(repository).find(request)
        }
    }

    @Test
    fun `#getAll - should return an empty PaginatedResponse when no items exist`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val request = PaginatedRequest(0, 10)

            whenever(repository.find(request)).thenReturn(Right(emptyList()))

            val sut = IngredientService(repository)
            assertEquals(Right(PaginatedResponse(
                emptyList<Ingredient>(), 0, 10
            )), sut.getAll(request))

            verify(repository).find(request)
        }
    }

    @Test
    fun `#getAll - should return a ServiceException if something goes wrong`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val request = PaginatedRequest(0, 10)

            whenever(repository.find(request)).thenReturn(Left(Exception("something happened")))

            val sut = IngredientService(repository)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.getAll(request))

            verify(repository).find(request)
        }
    }

    @Test
    fun `#getById - should return an item by id when item exists`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val id = "some-id"
            val ingredient = Ingredient.identity()

            whenever(repository.findById(id)).thenReturn(Right(Some(ingredient)))

            val sut = IngredientService(repository)
            assertEquals(Right(ingredient), sut.getById(id))

            verify(repository).findById(id)
        }
    }

    @Test
    fun `#getById - should return a ServiceException when requesting a non-existent item`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val id = "some-id"

            whenever(repository.findById(id)).thenReturn(Right(None))

            val sut = IngredientService(repository)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.NotFound,
                messages = listOf("Item not found!")
            )), sut.getById(id))

            verify(repository).findById(id)
        }
    }

    @Test
    fun `#addItem - should be able to add item if item does not already exist in repository`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val expected = Ingredient.identity()

            whenever(repository.insert(expected)).thenReturn(Right(expected))

            val sut = IngredientService(repository)
            assertEquals(Right(expected), sut.addItem(expected))

            verify(repository).insert(expected)
        }
    }

    @Test
    fun `#addItem - should return a ServiceException if something goes wrong`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val item = Ingredient.identity()

            whenever(repository.insert(item)).thenReturn(Left(Exception("something happened")))

            val sut = IngredientService(repository)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.addItem(item))

            verify(repository).insert(item)
        }
    }

    @Test
    fun `#updateItem - should return an item after updating an existing item`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val item = Ingredient.identity()

            whenever(repository.update(item)).thenReturn(Right(item))

            val sut = IngredientService(repository)
            assertEquals(Right(item), sut.updateItem(item))

            verify(repository).update(item)
        }
    }

    @Test
    fun `#updateItem - should return a ServiceException if something goes wrong`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val item = Ingredient.identity()

            whenever(repository.update(item)).thenReturn(Left(Exception("something happened")))

            val sut = IngredientService(repository)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.updateItem(item))

            verify(repository).update(item)
        }
    }

    @Test
    fun `#removeItem - should return item id if item exists`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val itemId = "some-id"

            whenever(repository.remove(itemId)).thenReturn(Right(Some(itemId)))

            val sut = IngredientService(repository)
            assertEquals(Right(itemId), sut.removeItem(itemId))

            verify(repository).remove(itemId)
        }
    }

    @Test
    fun `#removeItem - should return ServiceException if item is unable to be removed`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val item = Ingredient.identity()

            whenever(repository.remove(item.id.toString())).thenReturn(Right(None))

            val sut = IngredientService(repository)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("Unable to remove item from repository at this time")
            )), sut.removeItem(item.id.toString()))

            verify(repository).remove(item.id.toString())
        }
    }

    @Test
    fun `#removeItem - should return ServiceException if exception is thrown`() {
        runBlocking {
            val repository = mock<IRepository<Ingredient>>()
            val item = Ingredient.identity()

            whenever(repository.remove(item.id.toString())).thenReturn(Left(Exception("something happened")))

            val sut = IngredientService(repository)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.removeItem(item.id.toString()))

            verify(repository).remove(item.id.toString())
        }
    }
}
