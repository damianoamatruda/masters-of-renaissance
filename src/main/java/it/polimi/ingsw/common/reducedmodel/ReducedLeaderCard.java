package it.polimi.ingsw.common.reducedmodel;

import java.util.Optional;

public class ReducedLeaderCard extends ReducedCard {
    private final String resourceType;
    private final LeaderType leaderType;
    private final ReducedDevCardRequirement devCardRequirement;
    private final ReducedResourceRequirement resourceRequirement;
    private final boolean active;

    private final int containerId;
    private final int discount;

    /**
     * @param id            the ID of the card
     * @param victoryPoints the victory points given by the card
     * @param resourceType  the target resource type
     * @param leaderType    the type of leader card
     * @param active        the activation status
     * @param containerId   the ID of the depots, if present
     * @param discount      the resource discount, if present
     * @param production    the included production recipe, if present
     */
    public ReducedLeaderCard(int id, int victoryPoints, String resourceType, LeaderType leaderType, boolean active,
                             ReducedDevCardRequirement devCardRequirement,
                             ReducedResourceRequirement resourceRequirement,
                             int containerId,
                             int discount,
                             int production) {
        super(id, victoryPoints, production);

        if (resourceType == null)
            throw new IllegalArgumentException("Null resource type constructing reduced leader card.");
        if (leaderType == null)
            throw new IllegalArgumentException("Null leader type constructing reduced leader card.");

        this.resourceType = resourceType;
        this.leaderType = leaderType;
        this.active = active;
        this.devCardRequirement = devCardRequirement;
        this.resourceRequirement = resourceRequirement;

        this.containerId = containerId;
        this.discount = discount;
    }

    /**
     * @return the resourceRequirement
     */
    public Optional<ReducedResourceRequirement> getResourceRequirement() {
        return Optional.ofNullable(resourceRequirement);
    }

    /**
     * @return the devCardRequirement
     */
    public Optional<ReducedDevCardRequirement> getDevCardRequirement() {
        return Optional.ofNullable(devCardRequirement);
    }

    /**
     * @return the resourceType of the card
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * @return the leaderType of the card
     */
    public LeaderType getLeaderType() {
        return leaderType;
    }

    /**
     * @return the card isActive
     */
    public boolean isActive() {
        return active;
    }

    public ReducedLeaderCard setActive(boolean active) {
        return new ReducedLeaderCard(
                id, victoryPoints, resourceType, leaderType, active,
                devCardRequirement,
                resourceRequirement,
                containerId,
                discount,
                production);
    }

    /**
     * @return the containerId of the card
     */
    public int getContainerId() {
        return containerId;
    }

    /**
     * @return the discount of the card
     */
    public int getDiscount() {
        return discount;
    }

    public enum LeaderType {
        DEPOT,
        DISCOUNT,
        PRODUCTION,
        ZERO
    }
}
