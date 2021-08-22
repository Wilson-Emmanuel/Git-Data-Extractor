package com.softwarelab.dataextractor.mvvm.event_aggregator;

import java.util.function.Consumer;

/**
 * Created by Wilson
 * on Fri, 20/08/2021.
 */
public class Subscriber {

    private final Object subscriber;
    private final Consumer<String> cb;

    public Subscriber(Object subscriber,
                      Consumer<String> cb) {
        this.subscriber = subscriber;
        this.cb = cb;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public Consumer<String> getCb() {
        return cb;
    }

    @Override
    public int hashCode() {
        return subscriber.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return subscriber.equals(obj);
    }
}
