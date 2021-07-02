package it.polimi.ingsw.common;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerManager {
    private static final Logger LOGGER = Logger.getLogger(LoggerManager.class.getName());

    private static final String LOG_LEVEL_ENV = "LOG_LEVEL";

    /**
     * Private constructor to avoid unnecessary instantiation of the class
     */
    private LoggerManager() {
    }

    /**
     * Sets the log level by parsing the environment variable.
     */
    public static void useLogLevelEnv(Level defaultLogLevel) {
        try {
            setLogLevel(Level.parse(System.getenv(LOG_LEVEL_ENV).toUpperCase()));
            LOGGER.log(Level.INFO, "Log level parsed from environment variable.");
        } catch (NullPointerException e) {
            setLogLevel(defaultLogLevel);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid log level.");
            setLogLevel(defaultLogLevel);
        }
    }

    /**
     * Sets the log level.
     *
     * @param logLevel the log level
     */
    public static void setLogLevel(Level logLevel) {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(logLevel);
        for (Handler h : rootLogger.getHandlers())
            h.setLevel(logLevel);
        LOGGER.log(Level.INFO, String.format("Log level set to %s.", logLevel.getName()));
    }
}
