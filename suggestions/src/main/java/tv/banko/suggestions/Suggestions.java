package tv.banko.suggestions;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import tv.banko.suggestions.command.CommandManager;
import tv.banko.suggestions.database.Database;
import tv.banko.suggestions.listener.InteractionListener;
import tv.banko.suggestions.listener.MessageListener;
import tv.banko.suggestions.suggestion.SuggestionManager;
import tv.banko.suggestions.translation.CommonTranslation;
import tv.banko.suggestions.translation.MessageTranslation;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class Suggestions {

    private final JDA bot;
    private final Database database;

    private final SuggestionManager manager;

    private final MessageTranslation message;
    private final CommonTranslation common;

    public Suggestions(String token) throws LoginException, InterruptedException {
        JDA bot;
        JDABuilder builder = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS);

        this.message = new MessageTranslation();
        this.common = new CommonTranslation();
        CommandManager commandManager = new CommandManager();

        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.listening(common.get("activity") + " \uD83D\uDCC3"));
        builder.addEventListeners(commandManager);
        builder.addEventListeners(new InteractionListener(this));
        builder.addEventListeners(new MessageListener(this));

        bot = builder.build();
        bot.awaitReady();

        this.bot = bot;
        this.database = new Database(this);
        this.manager = new SuggestionManager(this);

        commandManager.load(this);
        this.database.getSuggestion().load();
    }

    public Suggestions() throws LoginException, InterruptedException {
        this(System.getenv("TOKEN"));
    }

    public JDA getBot() {
        return bot;
    }

    public SuggestionManager getManager() {
        return manager;
    }

    public Database getDatabase() {
        return database;
    }

    public MessageTranslation getMessage() {
        return message;
    }

    public CommonTranslation getCommon() {
        return common;
    }
}
