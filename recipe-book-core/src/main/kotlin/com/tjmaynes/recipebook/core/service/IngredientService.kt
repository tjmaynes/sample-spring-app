package com.tjmaynes.recipebook.core.service

import arrow.core.Left
import com.tjmaynes.recipebook.core.domain.Ingredient
import com.tjmaynes.recipebook.core.domain.validate
import com.tjmaynes.recipebook.core.types.*

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
}
