package tv.banko.suggestions.database.collection;

import com.mongodb.client.model.*;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import tv.banko.suggestions.database.Database;
import tv.banko.suggestions.database.subscriber.EmptySubscriber;
import tv.banko.suggestions.suggestion.Suggestion;

public record SuggestionCollection(Database database) {

    public void setSuggestion(Suggestion suggestion) {
        MongoCollection<Suggestion> collection = getCollection();

        collection.insertOne(suggestion, new InsertOneOptions().bypassDocumentValidation(true)).subscribe(new EmptySubscriber<>());
    }

    public void load() {
        getCollection().find().subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Suggestion suggestion) {
                if (suggestion == null) {
                    return;
                }

                try {
                    database.getSuggestions().getManager().addSuggestion(suggestion);
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

    public void deleteSuggestion(Suggestion suggestion) {
        MongoCollection<Suggestion> collection = getCollection();

        collection.deleteOne(Filters.eq(suggestion)).subscribe(new EmptySubscriber<>());
    }

    @NotNull
    public MongoCollection<Suggestion> getCollection() {
        return database.getDatabase().getCollection("channel", Suggestion.class);
    }
}
