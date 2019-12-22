package com.tjmaynes.recipebook.seeder

class Seeder {
    val greeting: String
        get() {
            return "Hello world."
        }
}

fun main(args: Array<String>) {
    println(Seeder().greeting)
}
