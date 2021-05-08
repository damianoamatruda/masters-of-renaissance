package it.polimi.ingsw.common.events;

import java.util.List;

import it.polimi.ingsw.common.View;

public class UpdateActionToken implements MVEvent {
    private final int token;
    private final List<Integer> stack;

    public UpdateActionToken(int token, List<Integer> stack) {
        this.token = token;
        this.stack = stack;
    }
    
    public List<Integer> getStack() {
        return stack;
    }
    
    public int getToken() {
        return token;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
