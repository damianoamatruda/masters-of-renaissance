package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class UpdateActivatedVatSection implements MVEvent {
    private final int id;

    public UpdateActivatedVatSection(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
