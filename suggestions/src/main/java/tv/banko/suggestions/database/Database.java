package tv.banko.support.database;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import tv.banko.support.Bot;
import tv.banko.support.database.collection.TicketCollection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private final Bot bot;

    private final MongoClient client;
    private final MongoDatabase database;

    private final TicketCollection ticket;

    public Database(Bot bot) {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        this.bot = bot;
        this.client = MongoClients.create("mongodb://mongodb:27017");

        this.database = client.getDatabase("support");

        this.ticket = new TicketCollection(this);
    }

    public Bot getBot() {
        return bot;
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public TicketCollection getTicket() {
        return ticket;
    }
}
