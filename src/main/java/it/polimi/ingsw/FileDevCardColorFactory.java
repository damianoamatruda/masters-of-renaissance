package it.polimi.ingsw;

import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.devcardcolors.DevCardColorFactory;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileDevCardColorFactory implements DevCardColorFactory {
    Map<String, DevCardColor> devCardColorMap;

    public FileDevCardColorFactory(FileGameFactory factory) {
        devCardColorMap = factory.generateDevCardColors().stream()
                .collect(Collectors.toMap(DevCardColor::getName, Function.identity()));
    }

    public DevCardColor get(String name) {
        return devCardColorMap.get(name);
    }

}
