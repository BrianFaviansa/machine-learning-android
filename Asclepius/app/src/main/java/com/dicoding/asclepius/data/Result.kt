package com.dicoding.asclepius.data

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class SuccessMessage(val message: String) : Result<Nothing>()
    data class Error(val error: String) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}