package it.polimi.ingsw.common;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.common.events.MVEvent;

/** Object of the model able to notify its observers of status changes. */
public class ModelObservable {
    /** The observers of this object. */
    transient protected List<View> observers;

    /** Class constructor. */
    public ModelObservable() {
        observers = new ArrayList<>();
    }
    
    /**
     * Class constructor.
     * 
     * @param observers a list of observers.
     */
    public ModelObservable(List<View> observers) {
        this.observers = observers;
    }

    /**
     * Registers an observer to the object, allowing it to receive update events.
     * 
     * @param o the observer to register
     */
    public void addObserver(View o) {
        // leadercards get created through GSON
        // GSON does not call the class constructor to instantiate objs
        // therefore, the list can't be created before adding observers to the cards
        if (observers == null)
            observers = new ArrayList<>();

        observers.add(o);
    }

    /**
     * Deregisters the observer, preventing events from propagating to it.
     * 
     * @param o the observer to deregister
     */
    public void removeObserver(View o) {
        observers.remove(o);
    }
    
    /**
     * Notifies every observer of a change in the object data.
     * 
     * @param msg the message containing the new data
     */
    public void notifyBroadcast(MVEvent msg) {
        for (View o : observers)
            notify(o, msg);
    }
    
    /**
     * Notifies the observer of a change in the object's data.
     * 
     * @param observer the observer to update
     * @param msg the message containing the new data
     */
    public void notify(View observer, MVEvent msg) {
        observer.update(msg);
    }
}
