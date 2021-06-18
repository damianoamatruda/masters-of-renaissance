package it.polimi.ingsw.common.reducedmodel;

import java.util.Optional;

public class ReducedActionToken {
    private final int id;
    private final String kind;
    private final String discardedDevCardColor;
    
    /**
     * @param id                    the ID of the action token
     * @param kind                  the type of token
     * @param discardedDevCardColor the target card color of discard, if existent
     */
    public ReducedActionToken(int id, String kind, String discardedDevCardColor) {
        this.id = id;
        this.kind = kind;
        this.discardedDevCardColor = discardedDevCardColor;
    }

    /**
     * @return the id of the token
     */
    public int getId() {
        return id;
    }

    /**
     * @return the token's type
     */
    public String getKind() {
        return kind;
    }

    /**
     * @return (if applicable, else null) the color
     *         of the development card that will be discarded upon token activation
     */
    public Optional<String> getDiscardedDevCardColor() {
        return Optional.ofNullable(discardedDevCardColor);
    }
}
