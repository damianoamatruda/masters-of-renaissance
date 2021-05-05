package it.polimi.ingsw.common;

import java.util.List;

import it.polimi.ingsw.common.events.MVEvent;

/** Object of the model able to notify its observers of status changes. */
public class ModelObservable {
    /** The observers of this object. */
    private final List<ModelObserver> listeners;

    /**
     * Class constructor.
     * 
     * @param listeners a list of observers.
     */
    public ModelObservable(List<ModelObserver> listeners) { this.listeners = listeners; }

    public void addListener(ModelObserver l) { listeners.add(l); }

    public void removeListener(ModelObserver l) { listeners.remove(l); }
    
    public void notifyBC(MVEvent msg) {
        for (ModelObserver l : listeners)
            notify(msg, l);
    }
    

    public void notify(MVEvent msg, ModelObserver listener) { listener.update(msg); }
}
