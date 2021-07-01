package it.polimi.ingsw.common;

/** AutoCloseable object used for networking purposes. */
public interface Network extends AutoCloseable {
    void open();

    @Override
    void close();
}
