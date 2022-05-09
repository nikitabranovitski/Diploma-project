package com.example.diplom.model

data class Message(
    val firstEmail: String = "",
    val secondEmail: String = "",
    val listOfMessage: ArrayList<Chat> = arrayListOf()
)

data class Chat(
    val message: String = ""
)