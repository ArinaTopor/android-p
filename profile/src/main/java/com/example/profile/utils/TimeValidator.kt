package com.example.profile.utils

object TimeValidator {
    fun validate(time: String): ValidationResult {
        if (time.isEmpty()) {
            return ValidationResult.Error("Поле не заполнено")
        }
        val pattern = Regex("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")
        if (!pattern.matches(time)) {
            return ValidationResult.Error("Неверный формат времени. Используйте HH:mm")
        }
        return ValidationResult.Success
    }
    
    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}

