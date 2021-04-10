package it.polimi.ingsw;

import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.devcardcolors.DevCardColorFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JavaDevCardColorFactory implements DevCardColorFactory {
    Map<String, DevCardColor> devCardColorMap;

    public JavaDevCardColorFactory() {
        devCardColorMap = generateDevCardColors().stream()
                .collect(Collectors.toMap(DevCardColor::getName, Function.identity()));
    }

    public DevCardColor get(String name) {
        return devCardColorMap.get(name);
    }

    private Set<DevCardColor> generateDevCardColors() {
        return Set.of(
                new DevCardColor("Blue"),
                new DevCardColor("Green"),
                new DevCardColor("Purple"),
                new DevCardColor("Yellow")
        );
    }
}
