package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/** Event signaling an error when replacing resources in a resource transaction. */
public class ErrReplacedTransRecipe extends ViewEvent {
    private final boolean isInput;
    private final String resType;
    private final int replacedCount, shelvesChoiceResCount;
    private final boolean isIllegalDiscardedOut;

    /**
     * @param view
     * @param isInput               whether the error refers to the input of a production or the output
     * @param resType               the resource type the count of which is wrong in the replaced recipe
     * @param replacedCount         the count of resources in the replaced map
     * @param shelvesChoiceResCount the count of resources in the shelves mapping
     * @param isIllegalDiscardedOut if <code>true</code>, replacedcount refers to the recipe's out
     */
    public ErrReplacedTransRecipe(View view, boolean isInput, String resType, int replacedCount, int shelvesChoiceResCount, boolean isIllegalDiscardedOut) {
        super(view);
        this.isInput = isInput;
        this.resType = resType;
        this.replacedCount = replacedCount;
        this.shelvesChoiceResCount = shelvesChoiceResCount;
        this.isIllegalDiscardedOut = isIllegalDiscardedOut;
    }

    /**
     * @return the isInput
     */
    public boolean isInput() {
        return isInput;
    }

    /**
     * @return if <code>true</code>, replacedcount refers to the recipe's out
     */
    public boolean isIllegalDiscardedOut() {
        return isIllegalDiscardedOut;
    }

    /**
     * @return the resource type the count of which is wrong in the replaced recipe
     */
    public String getResType() {
        return resType;
    }

    /**
     * @return the count of resources in the shelves mapping
     */
    public int getShelvesChoiceResCount() {
        return shelvesChoiceResCount;
    }

    /**
     * @return the count of resources in the replaced map
     */
    public int getReplacedCount() {
        return replacedCount;
    }
}
