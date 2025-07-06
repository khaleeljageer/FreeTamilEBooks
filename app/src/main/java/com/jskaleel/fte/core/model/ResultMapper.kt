package com.jskaleel.fte.core.model

interface IMapper<I, O> {
    fun map(input: I): O
}

abstract class ResultMapper<I, O> : IMapper<ResultState<I>, ResultState<O>> {
    abstract fun onSuccess(input: I): O
    abstract fun onError(error: String): String
    override fun map(input: ResultState<I>): ResultState<O> {
        return when (input) {
            is ResultState.Success -> {
                try {
                    ResultState.Success(onSuccess(input.data))
                } catch (e: Error) {
                    ResultState.Error(e.message.toString())
                }
            }

            is ResultState.Error -> {
                ResultState.Error(onError(input.message))
            }
        }
    }
}
