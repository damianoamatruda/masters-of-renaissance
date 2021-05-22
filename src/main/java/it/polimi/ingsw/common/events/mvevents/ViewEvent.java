package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

import java.util.Optional;

/**
 * Event handled as unicast.
 */
public abstract class ViewEvent implements MVEvent {
    private transient View view;

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
