package it.polimi.ingsw.common.events;

import java.util.List;

import it.polimi.ingsw.common.View;

public class UpdateMarket implements MVEvent {
    private final boolean isRow;
    private final int index;
    private final List<String> resources;
    private final String slideRes;

    public UpdateMarket(boolean isRow, int index, List<String> resources, String slideRes) {
        this.isRow = isRow;
        this.index = index;
        this.resources = resources;
        this.slideRes = slideRes;
    }

    public boolean isRow() {
        return isRow;
    }
    
    public int getIndex() {
        return index;
    }
    
    public List<String> getResources() {
        return resources;
    }
    
    public String getSlideRes() {
        return slideRes;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
