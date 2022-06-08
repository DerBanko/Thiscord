package tv.banko.suggestions.database.collection;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import tv.banko.support.database.Database;
import tv.banko.support.database.subscriber.EmptySubscriber;
import tv.banko.support.ticket.Ticket;

public record TicketCollection(Database database) {

    public TicketCollection(Database database) {
        this.database = database;

        getCollection().createIndex(Indexes.ascending("id"), new IndexOptions().unique(true))
                .subscribe(new EmptySubscriber<>());
    }

    public void setTicket(Ticket ticket) {
        MongoCollection<Document> collection = getCollection();

        collection.findOneAndReplace(Filters.eq("id", ticket.getId()), ticket.toDocument(),
                new FindOneAndReplaceOptions().upsert(true)).subscribe(new EmptySubscriber<>());
    }

    public void loadTickets() {
        getCollection().find(Filters.eq("closed", false)).subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Document document) {
                if (document == null) {
                    return;
                }

                try {
                    database.getBot().getTicket().addTicket(new Ticket(database.getBot().getTicket(), document));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public MongoCollection<Document> getCollection() {
        return database.getDatabase().getCollection("tickets");
    }
}
