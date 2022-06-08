package tv.banko.support.database.subscriber;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EmptySubscriber<T> implements Subscriber<T> {
    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
