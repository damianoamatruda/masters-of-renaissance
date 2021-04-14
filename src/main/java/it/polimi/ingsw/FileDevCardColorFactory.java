package it.polimi.ingsw;

import it.polimi.ingsw.model.devcardcolors.DevCardColor;
import it.polimi.ingsw.model.devcardcolors.DevCardColorFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory of development card color instances.
 */
public class FileDevCardColorFactory implements DevCardColorFactory {
    Map<String, DevCardColor> devCardColorMap;
    ModelConfig config;

    /**
     * Constructs the factory class.
     *
     * @param config    the parser object
     */
    public FileDevCardColorFactory(ModelConfig config) {
        this.config = config;
        devCardColorMap = generateDevCardColors().stream()
                .collect(Collectors.toMap(DevCardColor::getName, Function.identity()));
    }

    /**
     * Returns color instance by its name.
     *
     * @param name  the color name
     * @return      the instance corresponding the color name
     */
    public DevCardColor get(String name) {
        return devCardColorMap.get(name);
    }

    /**
     *
     *
     * @return
     */
    public Set<DevCardColor> generateDevCardColors(){
        return config.getCardColors().stream().map(s -> new DevCardColor(s)).collect(Collectors.toSet());
    }

}
