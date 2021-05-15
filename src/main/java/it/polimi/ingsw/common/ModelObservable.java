package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.mvevents.*;

import java.util.ArrayList;
import java.util.List;

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
    
    public void notifyBroadcast(ErrAction msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(ErrProtocol msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(ErrRuntime msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(ResGoodbye msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(ResWelcome msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateActionToken msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateBookedSeats msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateCurrentPlayer msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateDevCardGrid msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateDevCardSlot msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateFaithPoints msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateGameEnd msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateGameResume msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateGameStart msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateJoinGame msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateLastRound msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateLeader msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateLeadersHand msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateMarket msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdatePlayerStatus msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateResourceContainer msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateSetupDone msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateVaticanSection msg) {
        for (View o : observers)
            notify(o, msg);
    }

    public void notifyBroadcast(UpdateVictoryPoints msg) {
        for (View o : observers)
            notify(o, msg);
    }


    public void notify(View observer, ErrAction msg) {
        observer.update(msg);
    }

    public void notify(View observer, ErrProtocol msg) {
        observer.update(msg);
    }

    public void notify(View observer, ErrRuntime msg) {
        observer.update(msg);
    }

    public void notify(View observer, ResGoodbye msg) {
        observer.update(msg);
    }

    public void notify(View observer, ResWelcome msg) {
        observer.update(msg);
    }

    public void notify(View observer, UpdateActionToken msg) {
        observer.update(msg);
    }

    public void notify(View observer, UpdateBookedSeats msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateCurrentPlayer msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateDevCardGrid msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateDevCardSlot msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateFaithPoints msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateGameEnd msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateGameResume msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateGameStart msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateJoinGame msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateLastRound msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateLeader msg) {
        observer.update(msg);
    }
    
    public void notify(View observer, UpdateLeadersHand msg) {
        observer.update(msg);
    }

    public void notify(View observer, UpdateMarket msg) {
        observer.update(msg);
    }

    public void notify(View observer, UpdatePlayerStatus msg) {
        observer.update(msg);
    }

    public void notify(View observer, UpdateResourceContainer msg) {
        observer.update(msg);
    }

    public void notify(View observer, UpdateSetupDone msg) {
        observer.update(msg);
    }

    public void notify(View observer, UpdateVaticanSection msg) {
        observer.update(msg);
    }

    public void notify(View observer, UpdateVictoryPoints msg) {
        observer.update(msg);
    }

}
