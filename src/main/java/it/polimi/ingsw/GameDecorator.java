package it.polimi.ingsw;

/**
 * The decorator that may add extra functionality to the base game.
 * Added for extra extensibility with possible "feature updates" - not necessarily for required features
 */

public abstract class GameDecorator /*implements IGame*/ extends Game{
    /** The game to be extended in functionality */
    protected Game wrappee;

    /** Constructor to be called by the subclasses in order to assign the wrappee
     * @param game the game (wrappee) to be extended
     * */
    protected GameDecorator(Game game){
        this.wrappee = game;
    }
}
