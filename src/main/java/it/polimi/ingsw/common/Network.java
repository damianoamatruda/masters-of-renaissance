package it.polimi.ingsw.common;

/**
 * This interface represents an auto-closeable object used for networking purposes.
 */
public interface Network extends AutoCloseable {
    void open();

    @Override
    void close();
}
