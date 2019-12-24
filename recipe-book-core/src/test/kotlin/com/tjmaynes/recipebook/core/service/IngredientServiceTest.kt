package com.tjmaynes.recipebook.core.service

import arrow.core.Left
import arrow.core.Right
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.RepositoryException
import com.tjmaynes.recipebook.core.types.ServiceException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IngredientServiceTest {
    private val repository = mockk<IRepository<Ingredient>>()

    @Test
    fun `should be able to get all ingredients from a repository`() {
        val expected = Right(listOf(Ingredient.identity()))

        val pagedRequest = PaginatedRequest(
                pageNumber = 0,
                pageSize = 10
        )
        every { repository.getAll(pagedRequest) }.returns(expected)
        assertEquals(expected, IngredientService(repository).getAll(pagedRequest))
        verify { repository.getAll(pagedRequest) }
    }

    @Test
    fun `should return a ServiceException when something went wrong fetching all ingredients`() {
        val pagedRequest = PaginatedRequest(
                pageNumber = 0,
                pageSize = 10
        )

        every { repository.getAll(pagedRequest) }.returns(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )))

        assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )), IngredientService(repository).getAll(pagedRequest))

        verify { repository.getAll(pagedRequest) }
    }

    @Test
    fun `should be able to get ingredient by id from a repository`() {
        val ingredient = Ingredient.identity()
        val expected = Right(ingredient)

        every { repository.getById(ingredient.id.toString()) }.returns(expected)
        assertEquals(expected, IngredientService(repository).getById(ingredient.id.toString()))
        verify { repository.getById(ingredient.id.toString()) }
    }

    @Test
    fun `should return a ServiceException when something went wrong fetching an ingredient by id`() {
        val ingredient = Ingredient.identity()

        every { repository.getById(ingredient.id.toString()) }.returns(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )))

        assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )), IngredientService(repository).getById(ingredient.id.toString()))
        verify { repository.getById(ingredient.id.toString()) }
    }

    @Test
    fun `should be able to add an ingredient to a repository`() {
        val ingredient = Ingredient.identity()
        val expected = Right(ingredient)

        every { repository.addItem(ingredient) }.returns(expected)
        assertEquals(expected, IngredientService(repository).addItem(ingredient))
        verify { repository.addItem(ingredient) }
    }

    @Test
    fun `should return a ServiceException when something went wrong adding an ingredient`() {
        val ingredient = Ingredient.identity()

        every { repository.addItem(ingredient) }.returns(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )))

        assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )), IngredientService(repository).addItem(ingredient))
        verify { repository.addItem(ingredient) }
    }

    @Test
    fun `should be able to update an ingredient in the repository`() {
        val ingredient = Ingredient.identity()
        val expected = Right(ingredient)

        every { repository.updateItem(ingredient) }.returns(expected)
        assertEquals(expected, IngredientService(repository).updateItem(ingredient))
        verify { repository.updateItem(ingredient) }
    }

    @Test
    fun `should return a ServiceException when something went wrong updating an ingredient`() {
        val ingredient = Ingredient.identity()

        every { repository.updateItem(ingredient) }.returns(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )))

        assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )), IngredientService(repository).updateItem(ingredient))
        verify { repository.updateItem(ingredient) }
    }

    @Test
    fun `should be able to remove an ingredient in the repository`() {
        val ingredient = Ingredient.identity()
        val expected = Right(ingredient.id.toString())

        every { repository.removeItem(ingredient.id.toString()) }.returns(expected)
        assertEquals(expected, IngredientService(repository).removeItem(ingredient.id.toString()))
        verify { repository.removeItem(ingredient.id.toString()) }
    }

    @Test
    fun `should return a ServiceException when something went wrong removing an ingredient`() {
        val ingredient = Ingredient.identity()

        every { repository.removeItem(ingredient.id.toString()) }.returns(Left(RepositoryException(
                status = RepositoryException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )))

        assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("Something went wrong")
        )), IngredientService(repository).removeItem(ingredient.id.toString()))
        verify { repository.removeItem(ingredient.id.toString()) }
    }
}