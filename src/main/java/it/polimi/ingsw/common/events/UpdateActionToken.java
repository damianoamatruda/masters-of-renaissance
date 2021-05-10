package it.polimi.ingsw.common.events;

import java.util.List;

import it.polimi.ingsw.common.View;

/** Action token state update. */
public class UpdateActionToken implements MVEvent {
    /** The ID of the token being updated. */
    private final int token;
    /** The new stack of tokens. */
    private final List<Integer> stack;

    /**
     * Class constructor.
     * 
     * @param token the ID of the token being updated
     * @param stack the new stack of tokens
     */
    public UpdateActionToken(int token, List<Integer> stack) {
        this.token = token;
        this.stack = stack;
    }
    
    /**
     * @return the new stack of tokens
     */
    public List<Integer> getStack() {
        return stack;
    }
    
    /**
     * @return the ID of the token being updated
     */
    public int getToken() {
        return token;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
