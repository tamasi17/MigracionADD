package main.java.logging;

import log4Mats.LogManager;
import log4Mats.Logger;

import java.io.File;

public class LoggerProvider {
    static final Logger LOGGER = LogManager.getLoggerFromJson(new File("src/main/resources/logConfig.json"));

    static{
        LOGGER.setLogToConsole(true);
    }

    public static Logger getLogger(){
        return LOGGER;
    }
}
