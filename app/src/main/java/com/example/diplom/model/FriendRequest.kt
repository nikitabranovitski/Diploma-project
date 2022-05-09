package com.example.diplom.model

data class FriendRequest(
    val id: String = "",
    val firstEmail: String? = "",
    val secondEmail: String = "",
    val confirmation: Boolean = false
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "firstEmail" to firstEmail,
            "secondEmail" to secondEmail,
            "confirmation" to confirmation
        )
    }
}