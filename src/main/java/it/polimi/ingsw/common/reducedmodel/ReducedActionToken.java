package it.polimi.ingsw.common.reducedmodel;

public class ReducedActionToken {
    private final int id;
    private final String kind;
    private final String discardedDevCardColor;
    
    /**
     * @param id
     * @param kind
     * @param discardedDevCardColor
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
    public String getDiscardedDevCardColor() {
        return discardedDevCardColor;
    }
}
