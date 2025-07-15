package com.jskaleel.epub.utils

sealed interface IResult {
    data class Success(val id: Long) : IResult
    data class Failure(val message: String) : IResult
}