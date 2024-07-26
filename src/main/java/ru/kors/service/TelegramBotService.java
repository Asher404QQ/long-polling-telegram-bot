package ru.kors.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TelegramBotService implements LongPollingSingleThreadUpdateConsumer, SpringLongPollingBot {
    private final TelegramClient telegramClient;
    private final AiChatService aiChatService;
    private final String botToken;

    public TelegramBotService(TelegramClient telegramClient,
                              @Qualifier("open-ai-chat") AiChatService aiChatService, String botToken) {
        this.telegramClient = telegramClient;
        this.aiChatService = aiChatService;
        this.botToken = botToken;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            var message = SendMessage.builder()
                    .chatId(chatId)
                    .text(aiChatService.getResponseMessage(userMessage))
                    .build();

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
