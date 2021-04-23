package it.polimi.ingsw;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sun.xml.bind.v2.runtime.property.Property;
import it.polimi.ingsw.server.controller.FileGameFactory;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.actiontokens.ActionToken;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirement;
import it.polimi.ingsw.server.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.server.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import javax.xml.bind.annotation.XmlElement;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

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
//        FileGameFactory gameFactory = new FileGameFactory(App.class.getResourceAsStream("/config.xml"));
        GsonBuilder builder = new GsonBuilder().enableComplexMapKeySerialization();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        JsonParser parser = new JsonParser();

        //Dev Cards
//        DevelopmentCard[] objectMarshal = gson.fromJson(new JsonReader(new FileReader("src/main/resources/devCards.json")), DevelopmentCard[].class);
//        List<DevelopmentCard> list = Arrays.asList(objectMarshal);
//        System.out.println(list);

        //Faith track
//        Object object = parser.parse(new FileReader("src/main/resources/faithTrack.json"));
//        JsonObject objectMarshal = (JsonObject) object;
//        Set<FaithTrack.YellowTile> tiles = gson.fromJson(objectMarshal.get("yellow-tiles"), new TypeToken<HashSet<FaithTrack.YellowTile>>(){}.getType());
//        Set<FaithTrack.VaticanSection> sections = gson.fromJson(objectMarshal.get("vatican-sections"), new TypeToken<HashSet<FaithTrack.VaticanSection>>(){}.getType());
//        FaithTrack track = new FaithTrack(sections, tiles);

        //Market
//        Market resources = gson.fromJson(new JsonReader(new FileReader("src/main/resources/market.json")), Market.class);

        //Resources and colors
//        Object object = parser.parse(new FileReader("src/main/resources/resources_colors.json"));
//        JsonObject objectMarshal = (JsonObject) object;
//        Set<DevCardColor> colors = gson.fromJson(objectMarshal.get("card-colors"), new TypeToken<HashSet<DevCardColor>>(){}.getType());
//        Set<JsonObject> protoResources = gson.fromJson(objectMarshal.get("resource-types"), new TypeToken<HashSet<JsonObject>>(){}.getType());
//        Set<ResourceType> resources = new HashSet<>();
//        for(JsonObject o : protoResources){
//            JsonElement subtype = o.get("subtype");
//            resources.add(subtype == null ? gson.fromJson(o, ResourceType.class) : gson.fromJson(o, (Class<? extends ResourceType>) Class.forName(subtype.getAsString())));
//        }

        //Tokens
//        List<ActionToken> tokens = new ArrayList<>();
//        List<JsonObject> prototypes = gson.fromJson(new JsonReader(new FileReader("src/main/resources/tokens.json")), new TypeToken<ArrayList<JsonObject>>(){}.getType());
//        for(JsonObject o : prototypes){
//            tokens.add(gson.fromJson(o, (Class<? extends ActionToken>) Class.forName(o.get("type").getAsString())));
//        }

        //Leaders
//        Gson gson2 = new GsonBuilder()
//                .enableComplexMapKeySerialization().setPrettyPrinting()
//                .registerTypeHierarchyAdapter(CardRequirement.class, new RequirementDeserializer())
//                .create();
//
//        List<LeaderCard> leaders = new ArrayList<>();
//        List<JsonObject> prototypes = gson.fromJson(new JsonReader(new FileReader("src/main/resources/leaders.json")), new TypeToken<ArrayList<JsonObject>>(){}.getType());
//        for(JsonObject o : prototypes){
//            leaders.add(gson2.fromJson(o, (Class<? extends LeaderCard>) Class.forName(o.get("type").getAsString())));
//        }


        //Writer to JSON (only for marshalling)
//        FileWriter f = new FileWriter("src/main/resources/leaders.json");
//        System.out.println(gson.toJson(gameFactory.generateLeaderCards().get(0)));
//        gson.toJson(gameFactory.generateLeaderCards(), f);
//        f.flush();

        //Other parameters
//        Object object = parser.parse(new FileReader("src/main/resources/otherParams.json"));
//        JsonObject o = (JsonObject) object;
//
//        int maxPlayers = gson.fromJson(o.get("max-players"), int.class);
//        int numLeaders = gson.fromJson(o.get("num-leader-cards"), int.class);
//        int numDiscardedLeaders = gson.fromJson(o.get("num-discarded-leader-cars"), int.class);
//        int maxFaith = gson.fromJson(o.get("max-faith"), int.class);
//        int maxDevCards = gson.fromJson(o.get("max-development-cards"), int.class);
//        int maxLevel = gson.fromJson(o.get("max-level"), int.class);
//        int numCardColors = gson.fromJson(o.get("num-colors"), int.class);
//        int marketColumns = gson.fromJson(o.get("market-columns"), int.class);
//        int maxShelfSize = gson.fromJson(o.get("max-shelf-size"), int.class);
//        int slotsCount = gson.fromJson(o.get("slots-count"), int.class);
//
//        Production baseProduction = gson.fromJson(o.get("base-production"), Production.class);
//        ResourceType replaceableResource = gson.fromJson(o.get("replaceable-resource"), ResourceType.class);

//        Map<Integer, Boost> boost = gson.fromJson(o.get("initial-resources"), new TypeToken<HashMap<Integer, Boost>>(){}.getType());
    }

    private class Boost {
        /** Number of choosable resources obtained at the beginning. */
        private int numStorable;

        /** Starting faith points. */
        private int faith;
    }

//    private static class LeaderDeserializer implements JsonDeserializer<LeaderCard> {
//
//        @Override
//        public LeaderCard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//            Gson gson = new Gson();
//            LeaderCard card;
//            System.out.println(jsonElement);
//            try {
//                CardRequirement req = gson.fromJson(jsonElement.getAsJsonObject().get("requirement"), (Class<? extends CardRequirement>) Class.forName(jsonElement.getAsJsonObject().get("type").getAsString()));
//                jsonElement.getAsJsonObject().remove("requirement");
//                card = gson.fromJson(jsonElement.getAsJsonObject(), LeaderCard.class);
//                Field f1 = card.getClass().getDeclaredField("requirement");
//                f1.setAccessible(true);
//                f1.set(card, req);
//            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
//                e.printStackTrace();
//                card = null;
//            }
//            return card;
//        }
//    }

    private static class RequirementDeserializer implements JsonDeserializer<CardRequirement> {

        @Override
        public CardRequirement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            System.out.println(jsonElement);
            try {
                CardRequirement req = new Gson().fromJson(jsonElement.getAsJsonObject().get("requirement"), (Class<? extends CardRequirement>) Class.forName(jsonElement.getAsJsonObject().get("type").getAsString()));
                return new Gson().fromJson(jsonElement, (Class<? extends CardRequirement>) Class.forName(jsonElement.getAsJsonObject().get("type").getAsString()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
