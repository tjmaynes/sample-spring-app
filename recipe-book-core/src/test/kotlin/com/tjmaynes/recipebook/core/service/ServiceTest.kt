package com.tjmaynes.recipebook.core.service

import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.tjmaynes.recipebook.core.types.IRepository
import com.tjmaynes.recipebook.core.types.PaginatedRequest
import com.tjmaynes.recipebook.core.types.PaginatedResponse
import com.tjmaynes.recipebook.core.types.ServiceException
import com.tjmaynes.recipebook.core.validation.IsValidItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ServiceTest {
    private lateinit var repository: IRepository<Map<String, Any>>
    private val id = "some-id"
    private val item = mapOf("id" to id, "firstName" to "TJ", "lastName" to "Maynes")
    private val validate: IsValidItem<Map<String, Any>> = { item -> Right(item) }
    private val invalidate: IsValidItem<Map<String, Any>> = { Left(listOf("something is bad")) }

    @BeforeEach
    fun setup() {
        repository = mock()
    }

    @Test
    fun `#getAll - should return a PaginatedResponse when items exist`() {
        runBlocking {
            val repository = mock<IRepository<Map<String, Any>>>()
            val request = PaginatedRequest(0, 10)
            val expected = listOf(item)

            whenever(repository.find(request)).thenReturn(Right(expected))

            val sut = Service(repository, validate)
            assertEquals(Right(PaginatedResponse(
                expected, request.pageNumber, request.pageSize
            )), sut.getAll(request))

            verify(repository).find(request)
        }
    }

    @Test
    fun `#getAll - should return an empty PaginatedResponse when no items exist`() {
        runBlocking {
            val repository = mock<IRepository<Map<String, Any>>>()
            val request = PaginatedRequest(0, 10)

            whenever(repository.find(request)).thenReturn(Right(emptyList()))

            val sut = Service(repository, validate)
            assertEquals(Right(PaginatedResponse(
                emptyList<Map<String, Any>>(), 0, 10
            )), sut.getAll(request))

            verify(repository).find(request)
        }
    }

    @Test
    fun `#getAll - should return a ServiceException if something goes wrong`() {
        runBlocking {
            val request = PaginatedRequest(0, 10)

            whenever(repository.find(request)).thenReturn(Left(Exception("something happened")))

            val sut = Service(repository, validate)
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
            whenever(repository.findById(id)).thenReturn(Right(Some(item)))

            val sut = Service(repository, validate)
            assertEquals(Right(item), sut.getById(id))

            verify(repository).findById(id)
        }
    }

    @Test
    fun `#getById - should return a ServiceException when requesting a non-existent item`() {
        runBlocking {
            whenever(repository.findById(id)).thenReturn(Right(None))

            val sut = Service(repository, validate)
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
            whenever(repository.insert(item)).thenReturn(Right(item))

            val sut = Service(repository, validate)
            assertEquals(Right(item), sut.addItem(item))

            verify(repository).insert(item)
        }
    }

    @Test
    fun `#addItem - should return a ServiceException if something goes wrong`() {
        runBlocking {
            whenever(repository.insert(item)).thenReturn(Left(Exception("something happened")))

            val sut = Service(repository, validate)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.addItem(item))

            verify(repository).insert(item)
        }
    }

    @Test
    fun `#addItem - should return a ServiceException if given an invalid item`() {
        runBlocking {
            val sut = Service(repository, invalidate)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.BadRequest,
                messages = listOf("something is bad")
            )), sut.addItem(item))

            verifyZeroInteractions(repository)
        }
    }

    @Test
    fun `#updateItem - should return an item after updating an existing item`() {
        runBlocking {
            whenever(repository.update(item)).thenReturn(Right(item))

            val sut = Service(repository, validate)
            assertEquals(Right(item), sut.updateItem(item))

            verify(repository).update(item)
        }
    }

    @Test
    fun `#updateItem - should return a ServiceException if something goes wrong`() {
        runBlocking {
            whenever(repository.update(item)).thenReturn(Left(Exception("something happened")))

            val sut = Service(repository, validate)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.updateItem(item))

            verify(repository).update(item)
        }
    }

    @Test
    fun `#updateItem - should return a ServiceException if given an invalid item`() {
        runBlocking {
            val sut = Service(repository, invalidate)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.BadRequest,
                messages = listOf("something is bad")
            )), sut.updateItem(item))

            verifyZeroInteractions(repository)
        }
    }

    @Test
    fun `#removeItem - should return item id if item exists`() {
        runBlocking {
            whenever(repository.remove(id)).thenReturn(Right(Some(id)))

            val sut = Service(repository, validate)
            assertEquals(Right(id), sut.removeItem(id))

            verify(repository).remove(id)
        }
    }

    @Test
    fun `#removeItem - should return ServiceException if item is unable to be removed`() {
        runBlocking {
            whenever(repository.remove(id)).thenReturn(Right(None))

            val sut = Service(repository, validate)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("Unable to remove item from repository at this time")
            )), sut.removeItem(id))

            verify(repository).remove(id)
        }
    }

    @Test
    fun `#removeItem - should return ServiceException if exception is thrown`() {
        runBlocking {
            whenever(repository.remove(id)).thenReturn(Left(Exception("something happened")))

            val sut = Service(repository, validate)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.removeItem(id))

            verify(repository).remove(id)
        }
    }

    @Test
    fun `#getCount - should return Long when items exist in repository`() {
        runBlocking {
            val expected = Right(10.toLong())
            whenever(repository.getTotalCount()).thenReturn(expected)

            val sut = Service(repository, validate)
            assertEquals(expected, sut.getCount())

            verify(repository).getTotalCount()
        }
    }

    @Test
    fun `#getCount - should return ServiceException if exception is thrown`() {
        runBlocking {
            whenever(repository.getTotalCount()).thenReturn(Left(Exception("something happened")))

            val sut = Service(repository, validate)
            assertEquals(Left(ServiceException(
                status = ServiceException.StatusCode.Unknown,
                messages = listOf("something happened")
            )), sut.getCount())

            verify(repository).getTotalCount()
        }
    }
}
