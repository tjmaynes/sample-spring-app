package com.tjmaynes.recipebook.core.service

import arrow.core.*
import com.tjmaynes.recipebook.core.types.*
import com.tjmaynes.recipebook.core.validation.IsValidItem
import com.tjmaynes.recipebook.core.validation.ValidationErrors

class Service<T>(
    private val repository: IRepository<T>,
    private val validateItem: IsValidItem<T>
) : IService<T> {
    override suspend fun getAll(request: PaginatedRequest): Either<ServiceException, PaginatedResponse<T>> =
        repository.find(request)
            .mapLeft(::handleException)
            .flatMap { Right(PaginatedResponse(it, request.pageNumber, request.pageSize)) }

    override suspend fun getById(id: String): Either<ServiceException, T> =
        repository.findById(id)
            .mapLeft(::handleException)
            .flatMap {
                when (it) {
                    is Some -> Right(it.t)
                    is None -> Left(ServiceException(
                        status = ServiceException.StatusCode.NotFound,
                        messages = listOf("Item not found!")
                    ))
                }
            }

    override suspend fun addItem(item: T): Either<ServiceException, T> =
        validateItem(item).fold(
            { Left(handleBadRequestException(it)) },
            { repository.insert(it).mapLeft(::handleException) }
        )

    override suspend fun updateItem(item: T): Either<ServiceException, T> =
        validateItem(item).fold(
            { Left(handleBadRequestException(it)) },
            { repository.update(it).mapLeft(::handleException) }
        )

    override suspend fun removeItem(id: String?): Either<ServiceException, String> =
        Option.just(id).fold(
            {
                Left(ServiceException(
                    status = ServiceException.StatusCode.BadRequest,
                    messages = listOf("Please give a valid id")
                ))
            },
            {
                repository.remove(it!!).mapLeft { handleException(it) }
                    .flatMap {
                        when (it) {
                            is Some -> Right(it.t)
                            is None -> Left(ServiceException(
                                status = ServiceException.StatusCode.Unknown,
                                messages = listOf("Unable to remove item from repository at this time")
                            ))
                        }
                    }
            }
        )

    override suspend fun getCount(): Either<ServiceException, Long> =
        repository.getTotalCount().mapLeft { handleException(it) }

    private fun handleException(exception: Throwable) = ServiceException(
        status = ServiceException.StatusCode.Unknown,
        messages = listOf(exception.localizedMessage)
    )

    private fun handleBadRequestException(errors: ValidationErrors) = ServiceException(
        status = ServiceException.StatusCode.BadRequest,
        messages = errors
    )
}
