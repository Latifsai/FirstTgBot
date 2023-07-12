package io.proj3ct.LatifFirstSpringBot.service;

import com.vdurmont.emoji.EmojiParser;
import io.proj3ct.LatifFirstSpringBot.config.BotConfig;
import io.proj3ct.LatifFirstSpringBot.model.Ads;
import io.proj3ct.LatifFirstSpringBot.model.User;
import io.proj3ct.LatifFirstSpringBot.repository.AdsRepository;
import io.proj3ct.LatifFirstSpringBot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository repository;

    @Autowired
    private AdsRepository adsRepository;

    private final BotConfig config;

    private final String YES_BUTTON = "YES_BUTTON";
    private final String NO_BUTTON = "NO_BUTTON";
    private final String HELP_TEXT = "This bot is created to demonstrate my Abilities\n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see welcome message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see how to use this bot\n\n" +
            "Type /deletedata to delete your data\n\n" +
            "Type /setting to set new preferences\n\n" +
            "Type /register to register user in system";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "start the bot"));
        listOfCommands.add(new BotCommand("/mydata", "get your data store"));
        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
        listOfCommands.add(new BotCommand("/help", "help to use this bot"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        listOfCommands.add(new BotCommand("/register", "register in system"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting of bot command list " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatID = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerID() == chatID) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = repository.findAll();
                for (User user : users) {
                    sendMessage(user.getChatId(), textToSend);
                }
            } else {

                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatID, update.getMessage().getChat().getFirstName());
                        break;

                    case "/help":
                        sendMessage(chatID, HELP_TEXT);
                        break;

                    case "/register":
                        register(chatID);
                        break;

                    default:
                        sendMessage(chatID, "Sorry command is disable!");

                }
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageID = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(YES_BUTTON)) {
                String text = "You pressed YES button";
                executeEditMessageText(chatId, text, messageID);
            } else if (callBackData.equals(NO_BUTTON)) {
                String text = "You pressed NO button";
                executeEditMessageText(chatId, text, messageID);
            }
        }

    }

    private void executeEditMessageText(long chadId, String text, long messageID) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chadId));
        editMessageText.setText(text);
        editMessageText.setMessageId((int) messageID);

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void register(long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatID));
        message.setText("Do you really wanna register?");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInline.add(yesButton);
        rowInline.add(noButton);

        rows.add(rowInline);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        executeMessage(message);
    }

    private void registerUser(Message message) {
        if (repository.findById(message.getChatId()).isEmpty()) {
            var chatID = message.getChatId();
            var chat = message.getChat();

            User user = createUser(chatID, chat);
            repository.save(user);
            log.info("user saved " + user);
        }
    }

    private User createUser(Long chatId, Chat chat) {
        User user = new User();
        user.setChatId(chatId);
        user.setForename(chat.getFirstName());
        user.setSurname(chat.getLastName());
        user.setUserName(chat.getUserName());
        user.setRegisteredAT(LocalDateTime.now());
        return user;
    }

    private void startCommandReceived(long chatID, String name) {
        String answer
                = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :moneybag:");
        log.info("Replied to user: " + name);

        sendMessage(chatID, answer);
    }

    private void sendMessage(long id, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(id));
        message.setText(textToSend);

        message.setReplyMarkup(createTable());

        executeMessage(message);
    }

    private ReplyKeyboardMarkup createTable() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = fillListOfKeyboardRow();

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private List<KeyboardRow> fillListOfKeyboardRow() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("weather");
        row.add("get random joke");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("register");
        row2.add("check my data");
        row2.add("delete mt data");

        keyboardRows.add(row);
        keyboardRows.add(row2);
        return keyboardRows;
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    @Scheduled(cron = "${cron.scheduler}")
    public void sendAds(){
        var ads = adsRepository.findAll();
        var users = repository.findAll();

        for (Ads ad : ads) {
            for (User user : users) {
                sendMessage(user.getChatId(), ad.getAd());
            }

        }
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
