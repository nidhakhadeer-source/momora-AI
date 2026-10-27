package com.example.SIMATS.ui.ai

data class ChatMessage(
    val id: String,
    val text: String,
    val sender: String,
    val timestamp: String,
    val bulletPoints: List<String> = emptyList(),
    val footerNote: String? = null,
    val citationText: String? = null
)

data class AiChatUiState(
    val title: String = "A little guidance, anytime",
    val subtitle: String = "Momora AI · Here to help you understand",
    val dateHeader: String = "TODAY - 26 OCTOBER",
    val currentInputText: String = "",
    val suggestedPrompts: List<String> = listOf("Meal ideas", "When should I call?"),
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "1",
            text = "Is there anything I can try for heartburn after lunch?",
            sender = "You",
            timestamp = "1:55 PM"
        ),
        ChatMessage(
            id = "2",
            text = "That sounds uncomfortable, Maya. You logged mild heartburn after lunch today. A few gentle changes may help:",
            sender = "Momora",
            timestamp = "1:55 PM",
            bulletPoints = listOf(
                "Try smaller meals and eat slowly.",
                "Sit upright after eating.",
                "Notice whether specific foods trigger it."
            ),
            footerNote = "Before trying medicines or supplements for this, check with your pharmacist or maternity team.",
            citationText = "Based on your symptom log · General pregnancy education"
        )
    ),
    val aiDisclaimer: String = "AI can make mistakes. Not a diagnosis or an emergency service.",
    val selectedTab: Int = 2,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
