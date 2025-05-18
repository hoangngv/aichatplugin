package com.github.hoangngv.aichatplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.hoangngv.aichatplugin.MyBundle
import java.util.*
import javax.swing.SwingUtilities

@Service(Service.Level.PROJECT)
class ZcaApiService(private val project: Project) {

    private val messages = mutableListOf<ChatMessage>()

    var onMessagesUpdated: (() -> Unit)? = null

    fun getMessages(): List<ChatMessage> = messages.toList()

    fun sendUserMessage(message: String) {
        messages.add(ChatMessage(ChatMessage.Sender.USER, message))
        simulateAIResponse(message)
        onMessagesUpdated?.invoke()
    }

    private fun simulateAIResponse(userMessage: String) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val response = "Sample response to \"$userMessage\"."
                messages.add(ChatMessage(ChatMessage.Sender.ZCA, response))
                SwingUtilities.invokeLater {
                    onMessagesUpdated?.invoke()
                }
            }
        }, 1000)
    }

    fun clearHistory() {
        messages.clear()
        onMessagesUpdated?.invoke()
    }
}
