package com.tjmaynes.recipebook.core.service

import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.domain.validate
import com.tjmaynes.recipebook.core.types.*
import com.tjmaynes.recipebook.core.validation.ValidationErrors

class IngredientService(private val repository: IRepository<Ingredient>) : IService<Ingredient> {
    override suspend fun getAll(request: PaginatedRequest): ServiceResult<PaginatedResponse<Ingredient>> =
            repository.getAll(request).mapLeft { handleRepositoryException(it) }

    override suspend fun getById(id: String): ServiceResult<Ingredient> =
            repository.getById(id).fold(
                    { Left(handleRepositoryException((it))) },
                    {
                        when (it) {
                            is Some -> Right(it.t)
                            is None -> Left(ServiceException(
                                    status = ServiceException.StatusCode.NotFound,
                                    messages = listOf()
                            ))
                        }
                    }
            )

    override suspend fun addItem(item: Ingredient): ServiceResult<Ingredient> =
            IO.fx {
                item.validate().fold(
                        { Left(handleNotFoundException(it)) },
                        { !effect { repository.addItem(it).mapLeft { handleRepositoryException(it) } } }
                )
            }.suspended()

    override suspend fun updateItem(item: Ingredient): ServiceResult<Ingredient> =
            IO.fx {
                item.validate().fold(
                        { Left(handleNotFoundException(it)) },
                        { !effect { repository.updateItem(it).mapLeft { handleRepositoryException(it) } } }
                )
            }.suspended()

    override suspend fun removeItem(id: String): ServiceResult<String> =
            repository.removeItem(id).mapLeft { handleRepositoryException(it) }

    private fun handleNotFoundException(errors: ValidationErrors) = ServiceException(
            status = ServiceException.StatusCode.BadRequest,
            messages = errors
    )

    private fun handleRepositoryException(exception: Exception) = ServiceException(
            status = ServiceException.StatusCode.Unknown,
            messages = listOf(exception.localizedMessage)
    )
}