package com.github.hoangngv.aichatplugin.toolWindow

import com.github.hoangngv.aichatplugin.services.ChatMessage
import com.github.hoangngv.aichatplugin.services.ZcaApiService
import com.intellij.openapi.components.service
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBScrollPane
import java.awt.*
import java.time.format.DateTimeFormatter
import javax.swing.*

class ZcaChatWindow(toolWindow: ToolWindow) {
    private val service = toolWindow.project.service<ZcaApiService>()

    private val messageArea = JTextArea().apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        background = Color(0x23272A)
        foreground = Color(0xFFFFFF)
        font = Font("Monospaced", Font.PLAIN, 12)
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
    }

    private val scrollPane = JBScrollPane(messageArea).apply {
        preferredSize = Dimension(400, 300)
    }

    private val inputField = JTextField().apply {
        preferredSize = Dimension(300, 30)
        background = Color(0x23272A)
        foreground = Color(0xFFFFFF)
        caretColor = Color(0xFFFFFF)
        font = Font("Monospaced", Font.PLAIN, 12)
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color(0x99AAB5)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        )
        addActionListener {
            sendMessage()
        }
    }

    private val sendButton = JButton("Send").apply {
        addActionListener { sendMessage() }
    }

    private val clearButton = JButton("Clear").apply {
        addActionListener {
            service.clearHistory()
            refreshMessages()
        }
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    init {
        service.onMessagesUpdated = {
            SwingUtilities.invokeLater {
                refreshMessages()
            }
        }
    }

    fun getContent(): JPanel {
        val panel = JPanel(BorderLayout(10, 10))

        panel.add(scrollPane, BorderLayout.CENTER)

        val inputPanel = JPanel(BorderLayout(5, 5)).apply {
            add(inputField, BorderLayout.CENTER)

            val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 5, 0))
            buttonPanel.add(sendButton)
            buttonPanel.add(clearButton)

            add(buttonPanel, BorderLayout.EAST)
        }

        panel.add(inputPanel, BorderLayout.SOUTH)

        refreshMessages()

        // In real app, you’d listen to service changes (e.g. MessageBus) to auto-refresh

        return panel
    }

    private fun sendMessage() {
        val userMessage = inputField.text.trim()
        if (userMessage.isNotEmpty()) {
            service.sendUserMessage(userMessage)
            inputField.text = ""
            refreshMessages()
        }
    }

    private fun refreshMessages() {
        val builder = StringBuilder()
        val messages = service.getMessages()

        if (messages.isEmpty()) {
            builder.append("🤖 Welcome to ZCA. How can I help you?\n\n")
        } else {
            for (msg in messages) {
                val time = msg.timestamp.format(timeFormatter)
                val prefix = if (msg.sender == ChatMessage.Sender.USER) "👤 You:" else "🤖 ZCA:"
                builder.append("[$time] $prefix: ${msg.text}\n\n")
            }
        }

        messageArea.text = builder.toString()
        messageArea.caretPosition = messageArea.document.length
    }
}