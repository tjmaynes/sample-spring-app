package com.tjmaynes.recipebook.persistence.domain

import arrow.core.Either
import arrow.core.fix
import com.tjmaynes.recipebook.core.validation.ValidationCheck
import com.tjmaynes.recipebook.core.validation.ValidationErrors
import com.tjmaynes.recipebook.core.validation.getErrors
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant
import java.util.*

@Document
data class Ingredient(
    @Id val id: String?,
    val name: String,
    val calories: Int,
    val price: Double,
    val quality: Quality,
    @Field("serving_size") val servingSize: Int,
    @Field("size_type") val sizeType: String,
    @Field("created_at") @CreatedDate val createdAt: Long,
    @Field("updated_at") @LastModifiedDate val updatedAt: Long
) {
    enum class Quality {
        High,
        Low
    }

    companion object {
        fun identity() = Ingredient(
            id = UUID.randomUUID().toString(),
            name = "some-name",
            calories = 100,
            servingSize = 6,
            sizeType = "teaspoon",
            price = 1.0,
            quality = Quality.High,
            createdAt = Date.from(Instant.now()).time,
            updatedAt = Date.from(Instant.now()).time
        )
    }
}

fun Ingredient.validate(): Either<ValidationErrors, Ingredient> =
        with(ValidationCheck(this)) {
            map(
                fieldIsNotEmpty("name", name),
                fieldIsNotEmpty("sizeType", sizeType),
                fieldIsGreaterThanOrEqualToZero("calories", calories.toDouble()),
                fieldIsGreaterThanOrEqualToZero("servingSize", servingSize.toDouble()),
                fieldIsGreaterThanOrEqualToZero("price", price)
            ) { this@validate }.handleErrorWith { raiseError(it) }.fix().toEither()
                    .mapLeft { getErrors(it) }
        }
