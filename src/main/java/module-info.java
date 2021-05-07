module it.polimi.ingsw {
    requires com.google.gson;
    requires javafx.controls;

    exports it.polimi.ingsw.common;
    exports it.polimi.ingsw.server;
    exports it.polimi.ingsw.client;

    exports it.polimi.ingsw.client.gui to javafx.graphics;
}
