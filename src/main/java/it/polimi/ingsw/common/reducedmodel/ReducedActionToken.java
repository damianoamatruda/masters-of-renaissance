package it.polimi.ingsw.common.reducedmodel;

public class ReducedActionToken {
    private final int id;
    private final String type;
    private final String discardedDevCardColor;
    
    /**
     * @param id
     * @param type
     * @param discardedDevCardColor
     */
    public ReducedActionToken(int id, String type, String discardedDevCardColor) {
        this.id = id;
        this.type = type;
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
    public String getType() {
        return type;
    }

    /**
     * @return (if applicable, else null) the color
     *         of the development card that will be discarded upon token activation
     */
    public String getDiscardedDevCardColor() {
        return discardedDevCardColor;
    }
}
