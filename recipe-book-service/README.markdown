# Recipe Book Service
> Service written in Kotlin and Spring WebFlux.

## [JSON Generator](https://www.json-generator.com) Types

### Recipe
```json
[
  '{{repeat(5, 7)}}',
  {
    name: '{{lorem(1, "words")}}',
    description: '{{lorem(1, "paragraphs")}}',
    cooking_time: '{{integer(0, 20)}}',
    serving_size: '{{integer(0, 6)}}',
    rating: '{{integer(1, 5)}}',
    ingredient_ids: [
      '{{repeat(3, 8)}}',
      '{{guid()}}'
    ],
    author_id: '{{guid()}}'
  }
]
```

### Ingredient
```json
[
  '{{repeat(5, 7)}}',
  {
    name: '{{lorem(1, "words")}}',
    calories: '{{integer(0, 20)}}',
    serving_size: '{{integer(0, 6)}}',
    size_type: function (tags) {
      var types = ['tablespoon', 'slices', 'teaspoon'];
      return types[tags.integer(0, types.length - 1)];
    },
    quantity: '{{integer(1, 5)}}',
    price: '{{floating(10, 500, 2, "0.00")}}',
    quality: '{{integer(1, 5)}}'
  }
]
```

### ShoppingCart
```json
[
  '{{repeat(3, 4)}}',
  {
    user_id: '{{guid()}}',
    items: [
      '{{repeat(3)}}',
      {
        ingredient_id: '{{guid()}}',
        quantity: '{{integer(1, 3)}}'
      }
    ],
    is_finished: '{{bool()}}'
  }
]
```