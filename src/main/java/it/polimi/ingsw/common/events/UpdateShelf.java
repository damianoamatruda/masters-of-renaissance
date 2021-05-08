package it.polimi.ingsw.common.events;

import java.util.Map;

import it.polimi.ingsw.common.View;

public class UpdateShelf implements MVEvent {
    private final int id;
    private final Map<String, Integer> content;

    public UpdateShelf(int id, Map<String, Integer> content) {
        this.content = content;
        this.id = id;
    }

    /**
     * @return the content
     */
    public Map<String, Integer> getContent() {
        return content;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
