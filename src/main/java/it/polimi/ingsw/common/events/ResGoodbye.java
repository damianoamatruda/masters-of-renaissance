package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ResGoodbye implements MVEvent {
    @Override
    public void handle(View view) {
        view.update(this);
    }
}