package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrReplacedTransRecipe extends ViewEvent {
    private final String resType;
    private final int replacedCount, shelvesChoiceResCount;

    /**
     * @param view
     * @param resType               the resource type the count of which is wrong in the replaced recipe
     * @param replacedCount         the count of resources in the replaced map
     * @param shelvesChoiceResCount the count of resources in the shelves mapping
     */
    public ErrReplacedTransRecipe(View view, String resType, int replacedCount, int shelvesChoiceResCount) {
        super(view);
        this.resType = resType;
        this.replacedCount = replacedCount;
        this.shelvesChoiceResCount = shelvesChoiceResCount;
    }

    /**
     * @return the resType
     */
    public String getResType() {
        return resType;
    }
    /**
     * @return the shelvesChoiceResCount
     */
    public int getShelvesChoiceResCount() {
        return shelvesChoiceResCount;
    }
    /**
     * @return the replacedCount
     */
    public int getReplacedCount() {
        return replacedCount;
    }
}
