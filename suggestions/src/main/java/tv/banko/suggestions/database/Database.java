package tv.banko.suggestions.database;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.jetbrains.annotations.NotNull;
import tv.banko.suggestions.Suggestions;
import tv.banko.suggestions.database.collection.SuggestionCollection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private final Suggestions suggestions;

    private final MongoClient client;
    private final MongoDatabase database;

    private final SuggestionCollection suggestion;

    public Database(Suggestions suggestions) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        this.suggestions = suggestions;
        this.client = MongoClients.create("mongodb://mongodb:27017");

        this.database = client.getDatabase("suggestions");

        this.suggestion = new SuggestionCollection(this);
    }

    @NotNull
    public Suggestions getSuggestions() {
        return suggestions;
    }

    @NotNull
    public MongoClient getClient() {
        return client;
    }

    @NotNull
    public MongoDatabase getDatabase() {
        return database;
    }

    @NotNull
    public SuggestionCollection getSuggestion() {
        return suggestion;
    }
}
