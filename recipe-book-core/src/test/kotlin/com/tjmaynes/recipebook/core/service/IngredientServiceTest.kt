package com.tjmaynes.recipebook.core.service

import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.ServiceException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IngredientServiceTest {
    private val repository = mock<IRepository<Ingredient>>()
    private val UnknownException = Left(ServiceException(
            status = ServiceException.StatusCode.Unknown,
            messages = listOf("Something went wrong")
    ))
    private val UnknownRuntimeException = Left(RuntimeException("Something went wrong"))

    @Test
    fun `should be able to get all ingredients from a repository`() {
        runBlocking {
            val expected = Right(listOf(Ingredient.identity()))

            val pagedRequest = PaginatedRequest(
                    pageNumber = 0,
                    pageSize = 10
            )

            whenever(repository.getAll(pagedRequest)).thenReturn(expected)

            val sut = IngredientService(repository)

            assertEquals(expected, sut.getAll(pagedRequest))
            verify(repository).getAll(pagedRequest)
        }
    }

    @Test
    fun `should return a ServiceException when something went wrong fetching all ingredients`() {
        runBlocking {
            val pagedRequest = PaginatedRequest(
                    pageNumber = 0,
                    pageSize = 10
            )

            whenever(repository.getAll(pagedRequest)).thenReturn(UnknownRuntimeException)

            val sut = IngredientService(repository)

            assertEquals(UnknownException, sut.getAll(pagedRequest))
            verify(repository).getAll(pagedRequest)
        }
    }

    @Test
    fun `should be able to get ingredient by id from a repository`() {
        runBlocking {
            val ingredient = Ingredient.identity()
            val expected = Right(Some(ingredient))

            whenever(repository.getById(ingredient.id.toString())).thenReturn(expected)

            val sut = IngredientService(repository)

            assertEquals(Right(ingredient), sut.getById(ingredient.id.toString()))
            verify(repository).getById(ingredient.id.toString())
        }
    }

    @Test
    fun `should return a ServiceException when something went wrong fetching an ingredient by id`() {
        runBlocking {
            val ingredient = Ingredient.identity()

            whenever(repository.getById(ingredient.id.toString())).thenReturn(UnknownRuntimeException)

            val sut = IngredientService(repository)

            assertEquals(UnknownException, sut.getById(ingredient.id.toString()))
            verify(repository).getById(ingredient.id.toString())
        }
    }

    @Test
    fun `should return a ServiceException when unable to find a specific ingredient`() {
        runBlocking {
            val ingredient = Ingredient.identity()
            val none = Right(None)

            whenever(repository.getById(ingredient.id.toString())).thenReturn(none)

            val sut = IngredientService(repository)

            assertEquals(Left(ServiceException(
                    status = ServiceException.StatusCode.NotFound,
                    messages = emptyList()
            )), sut.getById(ingredient.id.toString()))
            verify(repository).getById(ingredient.id.toString())
        }
    }

    @Test
    fun `should be able to add an ingredient to a repository`() {
        runBlocking {
            val ingredient = Ingredient.identity()
            val expected = Right(ingredient)

            whenever(repository.addItem(ingredient)).thenReturn(expected)

            val sut = IngredientService(repository)

            assertEquals(expected, sut.addItem(ingredient))
            verify(repository).addItem(ingredient)
        }
    }

    @Test
    fun `should return a ServiceException when something went wrong adding an ingredient`() {
        runBlocking {
            val ingredient = Ingredient.identity()

            whenever(repository.addItem(ingredient)).thenReturn(UnknownRuntimeException)

            val sut = IngredientService(repository)

            assertEquals(UnknownException, sut.addItem(ingredient))
            verify(repository).addItem(ingredient)
        }
    }

    @Test
    fun `should return a ServiceException when adding an invalid ingredient`() {
        runBlocking {
            val ingredient = Ingredient.identity().copy(name = "")

            val sut = IngredientService(repository)
            assertEquals(Left(ServiceException(
                    status = ServiceException.StatusCode.BadRequest,
                    messages = listOf("name must not be empty.")
            )), sut.addItem(ingredient))
        }
    }

    @Test
    fun `should be able to update an ingredient in the repository`() {
        runBlocking {
            val ingredient = Ingredient.identity()
            val expected = Right(ingredient)

            whenever(repository.updateItem(ingredient)).thenReturn(expected)

            val sut = IngredientService(repository)

            assertEquals(expected, sut.updateItem(ingredient))
            verify(repository).updateItem(ingredient)
        }
    }

    @Test
    fun `should return a ServiceException when something went wrong updating an ingredient`() {
        runBlocking {
            val ingredient = Ingredient.identity()

            whenever(repository.updateItem(ingredient)).thenReturn(UnknownRuntimeException)

            val sut = IngredientService(repository)

            assertEquals(UnknownException, sut.updateItem(ingredient))
            verify(repository).updateItem(ingredient)
        }
    }

    @Test
    fun `should return a ServiceException when updating using an invalid ingredient`() {
        runBlocking {
            val ingredient = Ingredient.identity().copy(name = "")

            val sut = IngredientService(repository)
            assertEquals(Left(ServiceException(
                    status = ServiceException.StatusCode.BadRequest,
                    messages = listOf("name must not be empty.")
            )), sut.updateItem(ingredient))
        }
    }

    @Test
    fun `should be able to remove an ingredient in the repository`() {
        runBlocking {
            val ingredient = Ingredient.identity()
            val expected = Right(ingredient.id.toString())

            whenever(repository.removeItem(ingredient.id.toString())).thenReturn(expected)

            val sut = IngredientService(repository)

            assertEquals(expected, sut.removeItem(ingredient.id.toString()))
            verify(repository).removeItem(ingredient.id.toString())
        }
    }

    @Test
    fun `should return a ServiceException when something went wrong removing an ingredient`() {
        runBlocking {
            val ingredient = Ingredient.identity()

            whenever(repository.removeItem(ingredient.id.toString())).thenReturn(UnknownRuntimeException)

            val sut = IngredientService(repository)

            assertEquals(UnknownException, sut.removeItem(ingredient.id.toString()))
            verify(repository).removeItem(ingredient.id.toString())
        }
    }
}