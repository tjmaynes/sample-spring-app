package com.tjmaynes.recipebook.core.service

import arrow.core.*
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.domain.validate
import com.tjmaynes.recipebook.core.types.*
import com.tjmaynes.recipebook.core.validation.ValidationErrors

class IngredientService(private val repository: IRepository<Ingredient>) : IService<Ingredient> {
    override suspend fun getAll(request: PaginatedRequest): Either<ServiceException, PaginatedResponse<Ingredient>> =
        repository.find(request)
            .mapLeft { handleException(it) }
            .flatMap { Right(PaginatedResponse(it, request.pageNumber, request.pageSize)) }

    override suspend fun getById(id: String): Either<ServiceException, Ingredient> =
        repository.findById(id)
            .mapLeft { handleException(it) }
            .flatMap {
                when (it) {
                    is Some -> Right(it.t)
                    is None -> Left(ServiceException(
                        status = ServiceException.StatusCode.NotFound,
                        messages = listOf("Item not found!")
                    ))
                }
            }

    override suspend fun addItem(item: Ingredient): Either<ServiceException, Ingredient> =
        item.validate().fold(
            { Left(handleBadRequestException(it)) },
            { repository.insert(it).mapLeft(::handleException) }
        )

    override suspend fun updateItem(item: Ingredient): Either<ServiceException, Ingredient> =
        item.validate().fold(
            { Left(handleBadRequestException(it)) },
            { repository.update(it).mapLeft(::handleException) }
        )

    override suspend fun removeItem(id: String): Either<ServiceException, String> =
        repository.remove(id).mapLeft { handleException(it) }
            .flatMap {
                when (it) {
                    is Some -> Right(it.t)
                    is None -> Left(ServiceException(
                        status = ServiceException.StatusCode.Unknown,
                        messages = listOf("Unable to remove item from repository at this time")
                    ))
                }
            }

    private fun handleException(exception: Throwable) = ServiceException(
        status = ServiceException.StatusCode.Unknown,
        messages = listOf(exception.localizedMessage)
    )

    private fun handleBadRequestException(errors: ValidationErrors) = ServiceException(
        status = ServiceException.StatusCode.BadRequest,
        messages = errors
    )
}
