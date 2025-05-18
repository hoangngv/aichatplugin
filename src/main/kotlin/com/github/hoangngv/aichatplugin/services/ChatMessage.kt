package com.github.hoangngv.aichatplugin.services

import java.time.LocalDateTime

data class ChatMessage(
    val sender: Sender,
    val text: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    enum class Sender { USER, ZCA }
}