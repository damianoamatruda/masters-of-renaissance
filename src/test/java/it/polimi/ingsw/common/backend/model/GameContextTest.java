package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.common.backend.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class GameContextTest {
    private static final Logger LOGGER = Logger.getLogger(GameContextTest.class.getName());

    /** A dummy view. */
    View view;

    /** The multiplayer. */
    private Game multiGame;

    /** The solo game. */
    private Game soloGame;

    /** The game factory. */
    private GameFactory gameFactory;

    private GameContext multiContext;
    private GameContext soloContext;

    @BeforeEach
    void setup() {
        view = new View();
        view.addEventListener(Event.class, e -> LOGGER.info(e.getClass().getSimpleName()));
        gameFactory = new FileGameFactory(getClass().getResourceAsStream("/config/config.json"));
        multiGame = gameFactory.getMultiGame(List.of("A", "B"));
        soloGame = gameFactory.getSoloGame("A");

        multiContext = new GameContext(multiGame, gameFactory);
        soloContext = new GameContext(soloGame, gameFactory);
    }

    @Test
    void chooseLeaders() {
        List<Integer> leaders = new ArrayList<>(multiGame.getInkwellPlayer().getLeaders().stream().map(Card::getId).toList());
        multiContext.chooseLeaders(view, multiGame.getInkwellPlayer().getNickname(), leaders.stream().filter(l -> leaders.indexOf(l) % 2 == 0).toList());
        assertEquals(leaders.stream().filter(l -> leaders.indexOf(l) % 2 == 0).toList(), multiGame.getInkwellPlayer().getLeaders().stream().map(Card::getId).toList());
    }

    @Test
    void chooseResources() {
        List<Integer> leaders = multiGame.getPlayers().get(1).getLeaders().stream().map(Card::getId).toList();
        List<Integer> shelves = multiGame.getPlayers().get(1).getWarehouse().getShelves().stream().map(ResourceContainer::getId).toList();

        Map<Integer, Map<String, Integer>> choice = new HashMap<>();
        choice.put(shelves.get(0), Map.of("Coin", 1));

        multiContext.chooseLeaders(view, multiGame.getPlayers().get(1).getNickname(), leaders.stream().filter(l -> leaders.indexOf(l) % 2 == 0).toList());
        multiContext.chooseResources(view, multiGame.getPlayers().get(1).getNickname(), choice);

        assertAll(() -> assertEquals(multiGame.getPlayers().get(1).getWarehouse().getShelves().get(0).getResourceType().get().getName(), "Coin"),
                () -> assertEquals(multiGame.getPlayers().get(1).getWarehouse().getShelves().get(0).getQuantity(), 1));

    }

    @Nested
    class AfterSetup {

        @BeforeEach
        void setup() {
            List<Integer> leaders = soloGame.getInkwellPlayer().getLeaders().stream().map(Card::getId).toList();
            soloContext.chooseLeaders(view, soloGame.getInkwellPlayer().getNickname(), leaders.subList(0, 2));
            soloGame.getPlayers().forEach(player -> player.getSetup().giveInitialFaithPoints(soloGame, player));

            multiGame.getPlayers().forEach(p -> multiContext.chooseLeaders(view, p.getNickname(), p.getLeaders().subList(0, 2).stream().map(Card::getId).toList()));
            Map<Integer, Map<String, Integer>> choice = new HashMap<>();
            choice.put(multiGame.getPlayers().get(1).getWarehouse().getShelves().get(0).getId(), Map.of("Coin", 1));

            multiContext.chooseLeaders(view, multiGame.getPlayers().get(1).getNickname(), leaders.stream().filter(l -> leaders.indexOf(l) % 2 == 0).toList());
            multiContext.chooseResources(view, multiGame.getPlayers().get(1).getNickname(), choice);
            multiGame.getPlayers().forEach(player -> player.getSetup().giveInitialFaithPoints(multiGame, player));
        }

        @Test
        void isSetupDone() {
            assertTrue(soloGame.getPlayers().stream().filter(Player::isActive).map(Player::getSetup).allMatch(PlayerSetup::isDone));
        }

        @Test
        void swapShelves() throws IllegalResourceTransferException {
            soloGame.getInkwellPlayer().getWarehouse().getShelves().get(1).addResources(Map.of(gameFactory.getResourceType("Coin").get(), 1));
            soloGame.getInkwellPlayer().getWarehouse().getShelves().get(2).addResources(Map.of(gameFactory.getResourceType("Shield").get(), 2));

            int s1 = soloGame.getInkwellPlayer().getWarehouse().getShelves().get(1).getId();
            int s2 = soloGame.getInkwellPlayer().getWarehouse().getShelves().get(2).getId();
            soloContext.swapShelves(view, soloGame.getInkwellPlayer().getNickname(), s2, s1);

            assertAll(() -> assertEquals(soloGame.getInkwellPlayer().getWarehouse().getShelves().get(1).getResourceType().get().getName(), "Shield"),
                    () -> assertEquals(soloGame.getInkwellPlayer().getWarehouse().getShelves().get(2).getResourceType().get().getName(), "Coin"),
                    () -> assertEquals(soloGame.getInkwellPlayer().getWarehouse().getShelves().get(1).getQuantity(), 2),
                    () -> assertEquals(soloGame.getInkwellPlayer().getWarehouse().getShelves().get(2).getQuantity(), 1));
        }

        @RepeatedTest(value = 10)
        void activateLeader() throws IllegalResourceTransferException {
            LeaderCard leader = soloGame.getInkwellPlayer().getLeaders().get(0);

            Optional<Map<ResourceType, Integer>> resReq = Optional.empty();
            Optional<Set<DevCardRequirement.Entry>> cardReq = Optional.empty();
            try {
                leader.activate(soloGame.getInkwellPlayer());
            } catch (CardRequirementsNotMetException e) {
                resReq = e.getMissingResources();
                cardReq = e.getMissingDevCards();
            }
            if(resReq.isPresent()) {
                Map<ResourceType, Integer> req = resReq.get();
                for(ResourceType resource : req.keySet()) {
                    soloGame.getInkwellPlayer().getStrongbox().addResources(Map.of(resource, req.get(resource)));
                }
            } else if (cardReq.isPresent()) {
                Set<DevCardRequirement.Entry> req = cardReq.get();
                int i = 0;
                List<Stack<DevelopmentCard>> slots = soloGame.getInkwellPlayer().getDevSlots();
                for(DevCardRequirement.Entry r : req) {
                    for(int k = 0; k < r.reduce().getAmount(); k++) {
                       slots.get(i).push(new DevelopmentCard(gameFactory.getDevCardColor(r.reduce().getColor()).get(),
                               1, new ResourceRequirement(Map.of()), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, 0));
                       for(int j = 2; j <= r.reduce().getLevel(); j++)
                           slots.get(i).push(new DevelopmentCard(gameFactory.getDevCardColor(r.reduce().getColor()).get(),
                                   j, new ResourceRequirement(Map.of()), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, 0));
                       i++;
                    }
                }
            } else {
                fail();
            }
            soloContext.activateLeader(view, soloGame.getInkwellPlayer().getNickname(), leader.getId());

            assertTrue(leader.isActive());
        }

        @Test
        void discardLeader() {
            List<Integer> leaders = new ArrayList<>(soloGame.getInkwellPlayer().getLeaders().stream().map(Card::getId).toList());
            soloContext.discardLeader(view, soloGame.getInkwellPlayer().getNickname(), leaders.get(0));

            assertAll(() -> assertEquals(soloGame.getInkwellPlayer().getLeaders().size(), leaders.size() - 1),
                    () -> assertEquals(1, soloGame.getInkwellPlayer().getFaithPoints()));
        }

        @Test
        void takeMarketResources() {
            Optional<ResourceType> res1 = soloGame.getMarket().getGrid().get(0).stream().filter(ResourceType::isStorable).findAny();
            Optional<ResourceType> res2 = soloGame.getMarket().getGrid().stream().map(r -> r.get(0)).filter(ResourceType::isStorable).findAny();

            List<Warehouse.WarehouseShelf> whShelves = soloGame.getInkwellPlayer().getWarehouse().getShelves();
            List<Integer> shelves = whShelves.stream().map(ResourceContainer::getId).toList();

            res1.ifPresent(r -> {
                Map<Integer, Map<String, Integer>> choice = new HashMap<>();
                choice.put(shelves.get(0), Map.of(r.getName(), 1));

                soloContext.takeMarketResources(view, soloGame.getInkwellPlayer().getNickname(), true,
                        0, Map.of(), choice);
            });

            assertAll(() -> assertTrue(res1.isEmpty() || whShelves.get(0).getResourceMap().equals(Map.of(res1.get(), 1)))/*,
                    () -> assertTrue(res2.isEmpty() || whShelves.get(1).getResourceMap().equals(Map.of(res2.get(), 1)))*/);
        }

        @Test
        void buyDevCard() throws IllegalResourceTransferException {
            DevelopmentCard developmentCard = soloGame.getDevCardGrid().getDeck(gameFactory.getDevCardColor("Blue").get(), 1).peek();

            Map<ResourceType, Integer> req = new HashMap<>();
            try {
                developmentCard.getCost().checkRequirements(soloGame.getInkwellPlayer());
            } catch (CardRequirementsNotMetException e) {
                req = e.getMissingResources().get();
            }

            for(ResourceType resource : req.keySet()) {
                soloGame.getInkwellPlayer().getStrongbox().addResources(Map.of(resource, req.get(resource)));
            }
            Map<String, Integer> content = new HashMap<>();
            soloGame.getInkwellPlayer().getStrongbox().getResourceMap().keySet().forEach(k -> content.put(k.getName(), soloGame.getInkwellPlayer().getStrongbox().getResourceMap().get(k)));

            soloContext.buyDevCard(view, soloGame.getInkwellPlayer().getNickname(), "Blue", 1, 1, Map.of(soloGame.getInkwellPlayer().getStrongbox().getId(), content));

            assertAll(() -> assertEquals(0, soloGame.getInkwellPlayer().getStrongbox().getQuantity()),
                    () -> assertSame(soloGame.getInkwellPlayer().getDevSlots().get(1).peek(), developmentCard));
        }

        @Test
        void activateProductionRequests() throws IllegalResourceTransferException {
            int id = soloGame.getInkwellPlayer().getBaseProduction().getId();

            for(ResourceType resource : soloGame.getInkwellPlayer().getBaseProduction().getInput().keySet()) {
                soloGame.getInkwellPlayer().getStrongbox().addResources(Map.of(resource, soloGame.getInkwellPlayer().getBaseProduction().getInput().get(resource)));
            }
            soloGame.getInkwellPlayer().getStrongbox().addResources(Map.of(gameFactory.getResourceType("Stone").get(), soloGame.getInkwellPlayer().getBaseProduction().getInputBlanks()));

            Map<String, Integer> content = new HashMap<>();
            soloGame.getInkwellPlayer().getStrongbox().getResourceMap().keySet().forEach(k -> content.put(k.getName(), soloGame.getInkwellPlayer().getStrongbox().getResourceMap().get(k)));
            ReducedProductionRequest p = new ReducedProductionRequest(id, Map.of(soloGame.getInkwellPlayer().getStrongbox().getId(), content), Map.of(), Map.of("Servant", soloGame.getInkwellPlayer().getBaseProduction().getOutputBlanks()));
            soloContext.activateProductionRequests(view, soloGame.getCurrentPlayer().getNickname(), List.of(p));

            assertEquals(soloGame.getInkwellPlayer().getStrongbox().getResourceMap(), Map.of(gameFactory.getResourceType("Servant").get(), soloGame.getInkwellPlayer().getBaseProduction().getOutputBlanks()));
        }

        @Test
        void endTurn() {
            soloContext.takeMarketResources(view, soloGame.getInkwellPlayer().getNickname(), true,
                    0, Map.of(), Map.of(soloGame.getInkwellPlayer().getWarehouse().getShelves().get(0).getId(), Map.of()));

            assertDoesNotThrow(() -> soloContext.endTurn(view, soloGame.getInkwellPlayer().getNickname()));
        }

        @Test
        void setActive() {
            String thenCurrent = multiGame.getCurrentPlayer().getNickname();
            String nowCurrent = multiGame.getPlayers().stream().filter(p -> !p.equals(multiGame.getCurrentPlayer())).findAny().get().getNickname();
            multiContext.setActive(thenCurrent, false);
            assertEquals(multiGame.getCurrentPlayer().getNickname(), nowCurrent);
        }
    }
}