package com.softwarelab.dataextractor.mvvm.event_aggregator;

import javafx.application.Platform;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Wilson
 * on Thu, 19/08/2021.
 */
@Component
public class EventHub {

    public final static String EVENT_PROJECT_UPDATE = "projectUpdate";

    private final Map<String, List<Subscriber>> subscribers = new LinkedHashMap<>();

    public void publish(String event) {

        Platform.runLater( () -> {
            List<Subscriber> subscriberList = subscribers.get(event);
            System.out.println("there are "+subscriberList.size()+" subscribers");
            if (subscriberList != null) {
                subscriberList.forEach(subscriberObject -> subscriberObject.getCb().accept(event));
            }
        } );
    }

    public void subscribe(String event, Object subscriber, Consumer<String> cb) {

        if( !subscribers.containsKey(event) ) {
            List<Subscriber> slist = new ArrayList<>();
            subscribers.put( event, slist );
        }

        List<Subscriber> subscriberList = subscribers.get( event );

        subscriberList.add( new Subscriber(subscriber, cb) );
    }

    public void unsubscribe(String event, Object subscriber) {

        List<Subscriber> subscriberList = subscribers.get( event );

        if (subscriberList != null) {
            subscriberList.remove( subscriber );
        }
    }


}
