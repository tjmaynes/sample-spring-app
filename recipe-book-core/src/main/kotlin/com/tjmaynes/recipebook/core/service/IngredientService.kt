package com.tjmaynes.recipebook.core.service

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.domain.validate
import com.tjmaynes.recipebook.core.types.*

class IngredientService(
        private val repository: IRepository<Ingredient>
) : IService<Ingredient> {
    override fun getAll(request: PaginatedRequest): ServiceResult<List<Ingredient>> =
            repository.getAll(request).mapLeft { handleRepositoryException(it) }

    override fun getById(id: String): ServiceResult<Ingredient> =
            repository.getById(id).mapLeft { handleRepositoryException(it) }

    override fun addItem(item: Ingredient): ServiceResult<Ingredient> =
            validateItem(item).map { repository.addItem(it) }.flatMap {
                when (it) {
                    is Either.Left -> Left(handleRepositoryException(it.a))
                    is Either.Right -> Right(it.b)
                }
            }

    override fun updateItem(item: Ingredient): ServiceResult<Ingredient> =
            validateItem(item).map { repository.updateItem(it) }.flatMap {
                when (it) {
                    is Either.Left -> Left(handleRepositoryException(it.a))
                    is Either.Right -> Right(it.b)
                }
            }

    override fun removeItem(id: String): ServiceResult<String> =
            repository.removeItem(id).mapLeft { handleRepositoryException(it) }

    private fun handleRepositoryException(exception: RepositoryException): ServiceException =
            when (exception.status) {
                RepositoryException.StatusCode.NotFound -> ServiceException(
                        status = ServiceException.StatusCode.NotFound,
                        messages = exception.messages
                )
                RepositoryException.StatusCode.Unknown -> ServiceException(
                        status = ServiceException.StatusCode.Unknown,
                        messages = exception.messages
                )
            }

    private fun validateItem(item: Ingredient): ServiceResult<Ingredient> =
            item.validate().mapLeft {
                ServiceException(
                        status = ServiceException.StatusCode.BadRequest,
                        messages = it
                )
            }
}