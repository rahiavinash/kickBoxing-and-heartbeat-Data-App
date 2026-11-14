package com.app.kickboxing.Activitys.presentation.state

sealed class UIState {
    object Loading : UIState()
    data class Success<T>(val data: T) : UIState()
    data class Error(val message: String) : UIState()
}

sealed class UIAction {
    object LoadData : UIAction()
    data class SubmitData<T>(val data: T) : UIAction()
}

sealed class Results<out T> {
    data class Success<out T>(val data: T) : Results<T>()

    data class Failure(val message: String, val code: Int): Results<Nothing>()
    object Loading : Results<Nothing>()
}