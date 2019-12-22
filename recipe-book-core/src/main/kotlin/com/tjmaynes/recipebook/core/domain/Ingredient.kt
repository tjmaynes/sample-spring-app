package com.tjmaynes.recipebook.core.domain

import arrow.core.Either
import arrow.core.extensions.validated.applicativeError.handleError
import arrow.core.fix
import com.tjmaynes.recipebook.core.common.ValidationCheck
import com.tjmaynes.recipebook.core.common.ValidationError
import com.tjmaynes.recipebook.core.common.ValidationErrors
import com.tjmaynes.recipebook.core.common.getValidationErrors
import java.time.Instant
import java.util.*

data class Ingredient(
        val id: UUID,
        val name: String,
        val calories: Int,
        val servingSize: Int,
        val sizeType: String,
        val price: Double,
        val quality: Quality,
        val createdAt: Date,
        val updatedAt: Date
) {
    enum class Quality {
        High,
        Low
    }

    companion object {
        fun identity() = Ingredient(
                id = UUID.randomUUID(),
                name = "some-name",
                calories = 100,
                servingSize = 6,
                sizeType = "teaspoon",
                price = 1.0,
                quality = Quality.High,
                createdAt = Date.from(Instant.now()),
                updatedAt = Date.from(Instant.now())
        )
    }
}

fun Ingredient.validate(): Either<ValidationErrors, Ingredient> =
        with(ValidationCheck(this)) {
            map(
                    fieldIsNotEmpty("name", name),
                    fieldIsNotEmpty("sizeType", sizeType),
                    fieldIsGreaterThanZero("calories", calories.toDouble()),
                    fieldIsGreaterThanZero("servingSize", servingSize.toDouble()),
                    fieldIsGreaterThanZero("price", price)
            ) { this@validate }.handleErrorWith { raiseError(it) }.fix().toEither()
                    .mapLeft { getValidationErrors(it).toList() }
        }