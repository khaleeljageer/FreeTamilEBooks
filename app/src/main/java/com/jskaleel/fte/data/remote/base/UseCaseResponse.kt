package com.jskaleel.fte.data.remote.base

import com.jskaleel.fte.data.entities.ErrorModel

interface UseCaseResponse<Type> {

    fun onSuccess(result: Type)

    fun onError(errorModel: ErrorModel?)
}

