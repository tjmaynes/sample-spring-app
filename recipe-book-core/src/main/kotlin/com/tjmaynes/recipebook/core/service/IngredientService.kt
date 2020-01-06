package com.tjmaynes.recipebook.core.service

import arrow.core.Left
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.domain.validate
import com.tjmaynes.recipebook.core.types.*
import com.tjmaynes.recipebook.core.validation.ValidationErrors

class IngredientService(private val repository: IRepository<Ingredient>) : IService<Ingredient> {
    override suspend fun getAll(request: PaginatedRequest): ServiceResult<PaginatedResponse<Ingredient>> =
        repository.getAll(request).mapLeft(::handleRepositoryException)

    override suspend fun getById(id: String): ServiceResult<Ingredient> =
        repository.getById(id).mapLeft(::handleRepositoryException)

    override suspend fun addItem(item: Ingredient): ServiceResult<Ingredient> =
        item.validate().fold(
            { Left(handleBadRequestException(it)) },
            { repository.addItem(it).mapLeft(::handleRepositoryException) }
        )

    override suspend fun updateItem(item: Ingredient): ServiceResult<Ingredient> =
        item.validate().fold(
            { Left(handleBadRequestException(it)) },
            { repository.updateItem(it).mapLeft(::handleRepositoryException) }
        )

    override suspend fun removeItem(id: String): ServiceResult<String> =
        repository.removeItem(id).mapLeft(::handleRepositoryException)

    private fun handleBadRequestException(errors: ValidationErrors) = ServiceException(
        status = ServiceException.StatusCode.BadRequest,
        messages = errors
    )

    private fun handleRepositoryException(exception: RepositoryException) = when (exception.status) {
        RepositoryException.StatusCode.NotFound -> ServiceException(
            status = ServiceException.StatusCode.NotFound,
            messages = exception.messages
        )
        RepositoryException.StatusCode.Unknown -> ServiceException(
            status = ServiceException.StatusCode.Unknown,
            messages = exception.messages
        )
    }
}
