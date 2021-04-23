package it.polimi.ingsw;

import it.polimi.ingsw.server.controller.FileGameFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * The main class of the project.
 */
public class App {
    /**
     * Entry point of the main program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FileGameFactory f = new FileGameFactory(App.class.getResourceAsStream("/config.json"));
        System.out.println("");
    }
}
