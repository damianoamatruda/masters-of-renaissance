package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ResWelcome implements MVEvent {
    @Override
    public void handle(View view) {
        view.update(this);
    }
}
