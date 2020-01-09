package com.tjmaynes.recipebook.core.validation

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.nonemptylist.monad.flatten
import arrow.core.extensions.validated.applicativeError.applicativeError
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Semigroup

sealed class ValidationError {
    data class PropertyInvalid(val propertyName: String, val reason: String) : ValidationError()
    data class Multiple(val errors: Nel<ValidationError>) : ValidationError()
}

object ValidationErrorSemigroup : Semigroup<ValidationError> {
    override fun ValidationError.combine(b: ValidationError): ValidationError = when {
        this is ValidationError.Multiple && b is ValidationError.Multiple -> ValidationError.Multiple(errors + b.errors)
        this is ValidationError.Multiple && b !is ValidationError.Multiple -> ValidationError.Multiple(errors + b)
        this !is ValidationError.Multiple && b is ValidationError.Multiple -> ValidationError.Multiple(this.nel() + b.errors)
        else -> ValidationError.Multiple(Nel(this, b))
    }
}

class ValidationCheck<T>(
        private val item: T
) : ApplicativeError<ValidatedPartialOf<ValidationError>, ValidationError> by Validated.applicativeError(ValidationErrorSemigroup) {
    fun fieldIsGreaterThanZero(valueName: String, actualValue: Double): Kind<ValidatedPartialOf<ValidationError>, T> =
            if (actualValue <= 0) raiseError(
                    ValidationError.PropertyInvalid(
                            valueName, "$valueName should be greater than zero."
                    )
            )
            else just(item)

    fun fieldIsNotEmpty(valueName: String, actualValue: String): Kind<ValidatedPartialOf<ValidationError>, T> =
            if (actualValue.isEmpty()) raiseError(
                    ValidationError.PropertyInvalid(
                            valueName, "$valueName must not be empty."
                    )
            )
            else just(item)
}

private fun getValidationErrors(validationError: ValidationError): Nel<String> = when (validationError) {
    is ValidationError.PropertyInvalid -> Nel(validationError.reason)
    is ValidationError.Multiple -> validationError.errors
        .map { getValidationErrors(it) }
        .flatten()
}

fun getErrors(validationError: ValidationError): List<String> = getValidationErrors(validationError).toList()

typealias ValidationErrors = List<String>
typealias IsValidItem<T> = (T) -> Either<ValidationErrors, T>
