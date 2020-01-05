package com.tjmaynes.recipebook.core.service

import arrow.core.Left
import arrow.core.Right
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IngredientServiceTest {
    private val repository = mock<IRepository<Ingredient>>()
    private val unknowException = Left(ServiceException(
            status = ServiceException.StatusCode.Unknown,
            messages = listOf("Something went wrong")
    ))
    private val unknownRepositoryException = Left(RepositoryException(
            status = RepositoryException.StatusCode.Unknown,
            messages = listOf("Something went wrong")
    ))
    private val notFoundException = Left(ServiceException(
            status = ServiceException.StatusCode.NotFound,
            messages = listOf("Item not found!")
    ))
    private val notFoundRepositoryException = Left(RepositoryException(
            status = RepositoryException.StatusCode.NotFound,
            messages = listOf("Item not found!")
    ))

    @Test
    fun `should be able to get all ingredients from a repository`() {
        runBlocking {
            val pagedRequest = PaginatedRequest(
                    pageNumber = 0,
                    pageSize = 10
            )
            val expected = Right(PaginatedResponse(
                    listOf(Ingredient.identity()), pagedRequest.pageNumber, pagedRequest.pageSize
            ))

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

            whenever(repository.getAll(pagedRequest)).thenReturn(unknownRepositoryException)

            val sut = IngredientService(repository)

            assertEquals(unknowException, sut.getAll(pagedRequest))
            verify(repository).getAll(pagedRequest)
        }
    }

    @Test
    fun `should be able to get ingredient by id from a repository`() {
        runBlocking {
            val ingredient = Ingredient.identity()
            val expected = Right(ingredient)

            whenever(repository.getById(ingredient.id.toString())).thenReturn(expected)

            val sut = IngredientService(repository)

            assertEquals(Right(ingredient), sut.getById(ingredient.id.toString()))
            verify(repository).getById(ingredient.id.toString())
        }
    }

    @Test
    fun `should return a ServiceException when unable to find an ingredient by id`() {
        runBlocking {
            val ingredient = Ingredient.identity()

            whenever(repository.getById(ingredient.id.toString())).thenReturn(notFoundRepositoryException)

            val sut = IngredientService(repository)

            assertEquals(notFoundException, sut.getById(ingredient.id.toString()))
            verify(repository).getById(ingredient.id.toString())
        }
    }

    @Test
    fun `should return a ServiceException when something went wrong fetching an ingredient by id`() {
        runBlocking {
            val ingredient = Ingredient.identity()

            whenever(repository.getById(ingredient.id.toString())).thenReturn(unknownRepositoryException)

            val sut = IngredientService(repository)

            assertEquals(unknowException, sut.getById(ingredient.id.toString()))
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

            whenever(repository.addItem(ingredient)).thenReturn(unknownRepositoryException)

            val sut = IngredientService(repository)

            assertEquals(unknowException, sut.addItem(ingredient))
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

            whenever(repository.updateItem(ingredient)).thenReturn(unknownRepositoryException)

            val sut = IngredientService(repository)

            assertEquals(unknowException, sut.updateItem(ingredient))
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

            whenever(repository.removeItem(ingredient.id.toString())).thenReturn(unknownRepositoryException)

            val sut = IngredientService(repository)

            assertEquals(unknowException, sut.removeItem(ingredient.id.toString()))
            verify(repository).removeItem(ingredient.id.toString())
        }
    }
}