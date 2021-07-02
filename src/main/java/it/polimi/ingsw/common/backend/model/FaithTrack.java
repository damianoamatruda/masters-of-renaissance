package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.events.mvevents.UpdateVaticanSection;
import it.polimi.ingsw.common.reducedmodel.ReducedFaithTrack;
import it.polimi.ingsw.common.reducedmodel.ReducedVaticanSection;
import it.polimi.ingsw.common.reducedmodel.ReducedYellowTile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents the Faith Track of a game.
 *
 * @see VaticanSection
 */
public class FaithTrack extends EventDispatcher {
    /** Number of the last reachable faith track tile by a player. */
    private final int maxFaithPointsCount;

    /** Maps the end of each Vatican Section to the Vatican Section. */
    private final Map<Integer, VaticanSection> vaticanSectionsMap;

    /** The set of the Yellow Tiles. */
    private final Set<YellowTile> yellowTiles;

    /**
     * Constructor of the Faith Track.
     *
     * @param vaticanSections the set of the Vatican Sections
     * @param yellowTiles     the set of the Yellow Tiles which will give bonus points at the end
     */
    public FaithTrack(Set<VaticanSection> vaticanSections, Set<YellowTile> yellowTiles, int maxFaithPointsCount) {
        this.vaticanSectionsMap = vaticanSections.stream()
                .collect(Collectors.toUnmodifiableMap(VaticanSection::getFaithPointsEnd, Function.identity()));
        this.vaticanSectionsMap.values().forEach(s -> s.addEventListener(UpdateVaticanSection.class, this::dispatch));
        this.yellowTiles = Set.copyOf(yellowTiles);
        this.maxFaithPointsCount = maxFaithPointsCount;
    }

    /**
     * Returns the optional Vatican Section ending on the faith points.
     *
     * @param faithPoints the faith points
     * @return the Vatican Section ending there
     */
    public Optional<VaticanSection> getVaticanSectionReport(int faithPoints) {
        return vaticanSectionsMap.values()
                .stream().filter(v -> v.getFaithPointsEnd() <= faithPoints)
                .mapToInt(VaticanSection::getFaithPointsEnd)
                .max()
                .stream()
                .mapToObj(vaticanSectionsMap::get)
                .findAny();
    }

    /**
     * Returns the last Yellow Tile that has been reached, based on the faith points.
     *
     * @param faithPoints the faith points
     * @return the last Yellow Tile reached
     */
    public Optional<YellowTile> getLastReachedYellowTile(int faithPoints) {
        return yellowTiles.stream()
                .filter(yellowTile -> yellowTile.getFaithPoints() <= faithPoints)
                .max(Comparator.comparingInt(YellowTile::getFaithPoints));
    }

    /**
     * Getter of the tiles of Vatican reports.
     *
     * @return the set of the Vatican Sections
     */
    public Set<VaticanSection> getVaticanSections() {
        return Set.copyOf(vaticanSectionsMap.values());
    }

    /**
     * Getter of the variable that maps the number of tile to the bonus progressive victory points earned.
     *
     * @return the set of the Yellow Tiles
     */
    public Set<YellowTile> getYellowTiles() {
        return yellowTiles;
    }

    public ReducedFaithTrack reduce() {
        return new ReducedFaithTrack(
                vaticanSectionsMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().reduce())),
                yellowTiles.stream().map(YellowTile::reduce).toList(), maxFaithPointsCount);
    }

    public int getMaxFaithPointsCount() {
        return maxFaithPointsCount;
    }

    /**
     * This class represents a Vatican Section in the Faith Track.
     */
    public static class VaticanSection extends EventDispatcher {
        private final int id;

        /** The first tile of the Vatican Section, which needs to be reached in order to earn bonus points. */
        private final int faithPointsBeginning;

        /** The last tile of the Vatican Section, which needs to be reached in order to activate a Vatican report. */
        private final int faithPointsEnd;

        /** The corresponding quantity of bonus points that will be rewarded to the players after the Report is over. */
        private final int victoryPoints;

        /** <code>true</code> if the Vatican report is already over; <code>false</code> otherwise. */
        private boolean activated;

        /** The list of players that earned a Pope's favor in this section. */
        private final List<Player> bonusGivenPlayers;

        /**
         * Constructor of the Vatican Section.
         *
         * @param faithPointsBeginning the first tile of the Vatican Section, which needs to be reached in order to earn
         *                             bonus points
         * @param faithPointsEnd       the last tile of the Vatican Section, which needs to be reached in order to
         *                             activate a Vatican report
         * @param victoryPoints        the corresponding quantity of bonus points that will be rewarded to the players
         *                             after the Report is over
         * @see VaticanSection
         */
        public VaticanSection(int id, int faithPointsBeginning, int faithPointsEnd, int victoryPoints) {
            this.id = id;
            this.faithPointsBeginning = faithPointsBeginning;
            this.faithPointsEnd = faithPointsEnd;
            this.victoryPoints = victoryPoints;
            this.activated = false;
            this.bonusGivenPlayers = new ArrayList<>();
        }

        /**
         * Returns the first tile of the Vatican Section, which needs to be reached in order to earn bonus points.
         *
         * @return the faith points
         */
        public int getFaithPointsBeginning() {
            return faithPointsBeginning;
        }

        /**
         * Returns the last tile of the Vatican Section, which needs to be reached in order to activate a Vatican
         * report.
         *
         * @return the faith points
         */
        public int getFaithPointsEnd() {
            return faithPointsEnd;
        }

        /**
         * Returns the corresponding quantity of bonus points that will be rewarded to the players after the Report is
         * over.
         *
         * @return the victory points
         */
        public int getVictoryPoints() {
            return victoryPoints;
        }

        /**
         * Returns whether the Vatican Section is activated.
         *
         * @return <code>true</code> if the Vatican Section is activated; <code>false</code> otherwise.
         */
        public boolean isActivated() {
            return activated;
        }

        /**
         * Sets the state of activation of the Vatican Section.
         *
         * @param players   the players of the game that might or might not receive a bonus
         */
        public void activate(List<Player> players) {
            if (activated)
                return;
            activated = true;
            for (Player p : players)
                if (p.getFaithPoints() >= this.getFaithPointsBeginning()) {
                    p.incrementVictoryPoints(this.getVictoryPoints());
                    bonusGivenPlayers.add(p);
                }
            dispatch(new UpdateVaticanSection(id, bonusGivenPlayers.stream().map(Player::getNickname).toList()));
        }

        public ReducedVaticanSection reduce() {
            return new ReducedVaticanSection(id, faithPointsBeginning, faithPointsEnd, victoryPoints,
                    activated, bonusGivenPlayers.stream().map(Player::getNickname).toList());
        }
    }

    /**
     * This class represents a Yellow Tile in the Faith Track.
     */
    public static class YellowTile {
        /** The faith points in the Faith Track where the Yellow Tile is. */
        private final int faithPoints;

        /** The bonus progressive victory points earned. */
        private final int victoryPoints;

        /**
         * Constructor of the Yellow Tile.
         *
         * @param faithPoints   the faith points in the Faith Track where the Yellow Tile is
         * @param victoryPoints the bonus progressive victory points earned
         */
        public YellowTile(int faithPoints, int victoryPoints) {
            this.faithPoints = faithPoints;
            this.victoryPoints = victoryPoints;
        }

        /**
         * Returns the faith points in the Faith Track where the Yellow Tile is.
         *
         * @return the faith points
         */
        public int getFaithPoints() {
            return faithPoints;
        }

        /**
         * Returns the bonus progressive victory points earned.
         *
         * @return the victory points
         */
        public int getVictoryPoints() {
            return victoryPoints;
        }

        public ReducedYellowTile reduce() {
            return new ReducedYellowTile(faithPoints, victoryPoints);
        }
    }
}
