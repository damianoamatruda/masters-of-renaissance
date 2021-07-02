package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

import java.util.Optional;

/** Unicast events. */
public abstract class ViewEvent implements MVEvent {
    /** The View the event is addressed to. */
    private transient View view;

    /**
     * @param view View the event is addressed to
     */
    public ViewEvent(View view) {
        this.view = view;
    }

    public Optional<View> getView() {
        return Optional.ofNullable(view);
    }

    public void setView(View view) {
        this.view = view;
    }
}
