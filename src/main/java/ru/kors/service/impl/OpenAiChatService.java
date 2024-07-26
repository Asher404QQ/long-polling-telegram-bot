package ru.kors.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import ru.kors.service.AiChatService;

@Service("open-ai-chat")
public class OpenAiChatService implements AiChatService {
    private final ChatClient chatClient;

    public OpenAiChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String getResponseMessage(String requestMessage) {
        return chatClient.prompt()
                .user(requestMessage)
                .call()
                .content();
    }
}
