package it.polimi.ingsw.model;

import it.polimi.ingsw.model.devcardcolors.DevCardColor;

// TODO: Add Javadoc
class DevCardRequirementEntry {
    public final DevCardColor color;
    public final int level;
    public int amount = 0;

    // TODO: Add Javadoc
    public DevCardRequirementEntry(DevCardColor color, int level) {
        this.color = color; this.level = level;
    }
    // TODO: Add Javadoc
    public DevCardRequirementEntry(DevCardColor color, int level, int amount) {
        this.color = color; this.level = level; this.amount = amount; 
    }

    // TODO: Add Javadoc
    public DevCardRequirementEntry setAmount(int newAmount) { amount = newAmount; return this; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DevCardRequirementEntry)) return false;

        return  ((DevCardRequirementEntry)o).color == this.color &&
                (((DevCardRequirementEntry)o).level == 0 || this.level == 0 || ((DevCardRequirementEntry)o).level == this.level);
    }
}