package it.polimi.ingsw.common;

import java.io.IOException;

public interface Network {
    void start() throws IOException;

    void stop();
}
