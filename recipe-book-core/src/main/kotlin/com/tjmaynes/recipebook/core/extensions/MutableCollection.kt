package com.tjmaynes.recipebook.core.extensions

fun <T> MutableCollection<T>.append(it: Iterable<T>): MutableCollection<T> {
    this.addAll(it)
    return this
}
