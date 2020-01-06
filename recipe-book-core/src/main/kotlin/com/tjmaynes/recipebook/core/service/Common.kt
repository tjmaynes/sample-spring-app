package com.tjmaynes.recipebook.core.service

import com.tjmaynes.recipebook.core.types.RepositoryException
import com.tjmaynes.recipebook.core.types.ServiceException
import com.tjmaynes.recipebook.core.validation.ValidationErrors

fun handleBadRequestException(errors: ValidationErrors) = ServiceException(
    status = ServiceException.StatusCode.BadRequest,
    messages = errors
)

fun handleRepositoryException(exception: RepositoryException) = when (exception.status) {
    RepositoryException.StatusCode.NotFound -> ServiceException(
        status = ServiceException.StatusCode.NotFound,
        messages = exception.messages
    )
    RepositoryException.StatusCode.Unknown -> ServiceException(
        status = ServiceException.StatusCode.Unknown,
        messages = exception.messages
    )
}
