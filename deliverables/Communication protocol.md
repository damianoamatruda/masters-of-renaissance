# Table of Contents
1. [Communication protocol documentation](#communication-protocol-documentation)
2. [Errors](#errors)
3. [Client-server connection](#client-server-connection)
    1. [Connecting/choosing a nickname](#connecting/choosing-a-nickname)
    2. [Choosing the number of players](#choosing-the-number-of-players)
    3. [Game start](#game-start)
4. [Game phase - Player setup](#game-phase---player-setup)
    1. [Choosing leader cards](#choosing-leader-cards)
    2. [Choosing starting resources](#choosing-starting-resources)
5. [Game phase - Turns](#game-phase---turns)
    1. [Main actions](#main-actions)
        1. [Getting resources from the market](#getting-resources-from-the-market)
        2. [Buying a development card](#buying-a-development-card)
        3. [Activating productions](#activating-productions)
        4. [Ending the turn](#ending-the-turn)
    2. [Secondary actions](#secondary-actions)
        1. [Swapping two shelves' content](#swapping-two-shelves'-content)
        2. [Leader Actions](#leader-actions)
    3. [State messages](#state-messages)
        1. [Updating the current player](#updating-the-current-player)
        2. [Updating the market](#updating-the-market)
        3. [Updating the player's shelves](#updating-the-player's-shelves)
        4. [Updating the player's leader cards](#updating-the-player's-leader-cards)
        5. [Updating the development card grid](#updating-the-development-card-grid)
        6. [Updating the player's development card slots](#updating-the-player's-development-card-slots)
        7. [Updating the player's position on the faith track](#updating-the-player's-position-on-the-faith-track)
        8. [Sending the activated solo action token](#sending-the-activated-solo-action-token)
        9. [Declaring a winner](#declaring-a-winner)

# Communication protocol documentation
This document describes the client-server communication protocol used by the implementation of the Masters of Renaissance game written by group AM49.

All messages are encoded using the GSON library and follow therefore the JSON specification, language-wise.  
Every value shown in the messages is to be taken as an example, having been written only to show the messages' structure.  

# Errors
The following errors have been identified:
1. Broken (unparsable) messages
2. Illegal messages (messages sent at the wrong time)
3. Incorrect messages (messages that when executed by the server return an exception, e.g. due to an incorrect choice)
4. Server not responding
5. Player disconnecting

Messages in the third category are defined in the section they pertain to, following the message they respond to.  
The error generated by a player disconnecting is transparently handled by the server skipping the player's turn. Upon player reconnection the connection manager will connect the player to the room they were in before disconnecting (this is defined in the added functionality "Resilience to disconnections", reported in the [following section](#client-server-connection)).

Messages that generate an error of the second kind will receive an answer of type `err_illegal_message`:
```json
{
  "type": "err_illegal_message",
  "msg": "Wrong message type."
}
```
Only the server is able to generate this type of message.

Unparsable messages will receive an answer of type `err_unparsable_message`:
```json
{
  "type": "err_unparsable_message",
  "msg": "Received unparsable message."
}
```
This kind of message can originate from clients and server alike.
When this type of message is received the last message will be recreated by the sender and sent again. More advanced solutions were not requested and will therefore not be taken into account to maintain simplicity and keep the focus on other features.

If the server is unresponsive the clients will notify the player of the situation, ending the game.

The error messages' content is purposefully kept synthetic and will be expanded upon by the receiver if so appropriate.

# Client-server connection
A summary of the requirements highlighting the relevant parts is reported below:
> - On player connection: the player is automatically added to the game currently being filled. If there is no game in its starting phase, a new one is created.
> - The player starting a new game chooses how many players have to join before the game can start.
> - The game starts as soon as the specified number of players (given by the first player to join) is reached.
> - The server manages the players' turns as per the game's rules. The server must handle a player disconnecting or leaving the game; if there are no players left the game will terminate and all players have to be notified.

The following specification for the additional feature "Multiple Games" is taken into account in the communication protocol's design:
> - Only one waiting room will be used to manage the players who join the server.
> - Players who disconnect can successively reconnect to continue the match. While a player isn't connected, the game continues skipping the player's turns.

Given those requirements, the communication has been modeled in the following way.

## Connecting/choosing a nickname
The player, when starting the client, will be asked to input a nickname of their choice. The entry will be sent to the server, and, if unique among the connected players, will be accepted. Else, the player will be notified of the need to change it, restarting the process.

The information of whether the player is the first of the match is included in the response given to the client. This is necessary to [handle the choice of the number of players](#choosing-the-number-of-players).
```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │                   req_nickname │
     ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
     │                                │ ╭───────────────────────╮
     │                                ├─┤ check nickname unique │
     │ res_nickname                   │ ╰───────────────────────╯
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
     │ err_nickname                   │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     ┆                                ┆
```
**req_nickname (client)**
```json
{
  "type": "req_nickname",
  "nickname": "X"
}
```
**res_nickname (server)**
```json
{
  "type": "res_nickname",
  "isFirst": false
}
```
**err_nickname (server)**
```json
{
  "type": "err_nickname",
  "msg": "Username already taken."
}
```

## Choosing the number of players
When a player is chosen by the server as the first of a new game, they have to decide the number of players required to start it.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │
╰────────────╯ │              req_players_count │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭─────────────╮
               │                                ├─┤ try setting │
               │ res_players_count              │ ╰─────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_players_count              │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**req_players_count (client)**
```json
{
  "type": "req_players_count",
  "count": 3
}
```
**res_players_count (server)**
```json
{
  "type": "res_players_count",
  "msg": "The number of players has been set to 3."
}
```
**err_players_count (server)**
```json
{
  "type": "err_players_count",
  "msg": "Illegal number of players: 0."
}
```

## Game start
As the game starts, the server notifies all players of the event.

### Caching
At the same time, it sends the game's state to be cached by the clients. Caching parts of the game's state allows the clients to answer requests without the server's intervention.  
For example, when using the CLI, updates sent from the server would be logged to the user's console. If the player wanted to look at an old state update (e.g. something that happened 10 moves prior), they would have to scroll a lot to reach it. To avoid this, the player can query the game to be shown the objects status. Caching allows the clients to handle this kind of request, making the communication protocol and server loads lighter, while improving the game's interactivity and user experience.  
Caching also allows partial checks to be preemptively (but not exclusively) done client side: if the player specifies an index that's out of bounds, the client is able to catch the error before sending the request to the server, again reducing network and server loads.

### Parameters and indices
The game's model has been parameterized to allow for flexibility. The parameters are set via a [configuration file](../src/main/resources/config.json), which also contains serialized game data (e.g. cards, resources, etc...).  
This file is available to both clients and server, which will use it to instantiate the game objects. It is therefore **extremely important** for both parties to have matching files.  
The matching and ordering properties of the objects in the configuration file are used to identify game objects and synchronize the game state at the start of the match, eliminating the need for a more complex ID system (this system was chosen over a more proper solution as a compromise to allow the developers to focus on other features, the lack of time being the main driver of the decision).

This implies that every ID/index specified in this document has been synchronized at game start either by being taken from the configuration file or by being specified in the `game_started` message.

The market's resources are placed randomly at creation, therefore needing to be synchronized: the entire market's state needs to be sent.  
The leader cards will be shuffled before they can be chosen by the players: the `leader_cards` field of the `game_started` message contains the original placements (in the config file) of the leader cards in the order the server shuffled them (`leader_cards[0] = 2` means that the config file's third card is the first in the server's list).  
The same principle applies to the development cards grid's stacks, sent as a list of objects, mapping colors with a list (levels) of lists (the deck of cards matching that level and color), and the solo action tokens.

After reordering the cached objects to match the server's state, all indices sent from the server can be used to reference the correct objects.

```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │ game_started                   │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     ┆                                ┆
```
**game_started (server)**
```json
{
"type": "game_started",
  "nicknames": [ "X", "Y", "Z" ],
  "market": {
    "grid": [
      [ "Coin", "Shield", "Coin" ],
      [ "Coin", "Shield", "Stone" ]
    ],
    "slide": "Shield"
  },
  "leader_cards": [ 4, 2, 0, 1, 3 ],
  "dev_card_grid": [
    {
      "color": "Blue",
      "stacks": [
        [ 2, 0, 1 ],
        [ 3, 4, 5 ]
      ]
    }, {
      "color": "Green",
      "stacks": [
        [ 8, 7, 6 ],
        [ 9, 11, 10 ]
      ]
    }
  ],
  "solo_action_tokens": [ 4, 3, 0, 1, 2 ]
}
```

# Game phase - Player setup
When the game starts, the server instantiates its internal model. To set up the player objects, the clients will be asked for choices. Those include which leader cards to keep and what resources to take, since the players following the first are entitled to receive bonus resources and faith points.

## Choosing leader cards
As per the game's rules, the players have to decide manually what leader cards they want to keep for the match's duration: each player is sent a portion of the deck of leader cards, from which they can choose the cards to keep and discard.

The client will be sent the IDs of the leader cards they can choose from, and will send back a subset of them.  
To confirm the success of the operation, the server will echo back the chosen indices. Else, the player will be notified with an error message.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ offer_leader                   │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
╭────────────╮ │                                │
│ user input ├─┤                                │
╰────────────╯ │              req_leader_choice │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────────────╮
               │                                ├─┤ try exec / check choices │
               │ res_leader_choice              │ ╰──────────────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_leader_choice              │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**offer_leader (server)**
```json
{
  "type": "offer_leader",
  "leaders_id": [ 3, 7, 14, 15 ]
}
```
**req_leader_choice (client)**
```json
{
  "type": "req_leader_choice",
  "leaders_id": [ 3, 15 ]
}
```
**res_leader_choice (server)**
```json
{
  "type": "res_leader_choice"
}
```
**err_leader_choice (server)**
```json
{
  "type": "err_leader_choice",
  "msg": "Invalid leader cards chosen: 3, 25."
}
```

## Choosing starting resources
The players who haven't been given the inkwell have to choose their bonus starting resources.  
The server will notify the player of the event, signaling the amount of resources the player can choose and which resource types they can choose from.  
The client will respond by specifying the resource types and the respective quantities.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ offer_resources                │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
╭────────────╮ │                                │
│ user input ├─┤                                │
╰────────────╯ │           req_resources_choice │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────────────╮
               │                                ├─┤ try exec / check choices │
               │ update_shelves                 │ ╰──────────────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ update_faith_track             │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_resources_choice           │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_shelf_choice               │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**offer_resources (server)**
```json
{
  "type": "offer_resources",
  "storable_count": 1
}
```
**req_resources_choice (client)**
```json
{
  "type": "req_resources_choice",
  "init_res": [
    { "shelf": 1, "res": { "type": "Coin", "quantity": 1 } },
    { "shelf": 0, "res": { "type": "Shield", "quantity": 1 } }
  ]
}
```
**err_resources_choice (client)**
```json
{
  "type": "err_resources_choice",
  "msg": "Cannot choose 2 starting resources, only 1 available."
}
```
**err_shelf_choice (client)**
```json
{
  "type": "err_shelf_choice",
  "msg": "Cannot place 3 resources of type Coin on shelf 0: not enough space."
}
```

# Game phase - Turns
After all players have gone through the setup phase, the server will start the turn loop.

The messages in this section can be differentiated into:
1. [Main actions](#main-actions), of which the player has to choose only one during the turn
2. [Secondary actions](#secondary-actions), which can be repeated within the player's turn
3. [State messages](#state-messages), which update the local caches' state to match the server's


# Main actions
During their turn, the player has to choose among three main actions to carry out:
1. Getting resources from the market
2. Buying a development card
3. Activating the production

Since the player may want to make a secondary move after the main action, the server waits for a `turn_end` message to switch to the next player (timeouts are used in case the player takes too long to make a move/is AFK).

## Getting resources from the market
The following needs to be specified:
1. Which row/column the player wants to take the resources from
2. For each replaceable resource, which leader to use (if applicable)
3. For each resource (its type considered after the leader's processing), which shelf to use to store it
4. What resources, among the ones taken from the market, to discard

Discarding is simply handled by specifying a lower quantity of resources to add to a shelf. This also easily matches the rule for which only the resources given by the market can be discarded.

The `replacements` field specifies how the resource conversion should be handled. Since the player knows what type of resource the leader convert to, they can easily select them by specifying, for each type of resource they want as output, how many replaceable resources (of the available ones) to use.

Errors may arise from fitting the resources in the shelves, either by specifying the wrong shelf or by not discarding enough resources.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │                 req_get_market │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────────────╮
               │                                ├─┤ try exec / check choices │
               │ update_market                  │ ╰──────────────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ update_shelves                 │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ update_leaders                 │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_shelves_choice             │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**req_get_market (client)**
```json
{
  "type": "req_get_market",
  "market_index": 0,
  "isRow": true,
  "replacements": [ { "res": "Coin", "quantity": 2 } ],
  "shelf_choice": [
    {
      "shelf_index": 1,
      "res_quantity": 2,
      "res_type": "Coin"
    }, {
      "shelf_index": 3,
      "res_quantity": 2,
      "res_type": "Shield"
    }
  ]
}
```
**err_shelves_choice (server)**
```json
{
  "type": "err_shelves_choice",
  "msg": "Cannot fit the resources in the shelves as chosen: no space left in shelf 3."
}
```

## Buying a development card
The following information is needed when buying a development card:
1. The row and column of the card to identify it in the grid
2. The slot to place the card into
3. For each resource that has to be paid, the shelf (or strongbox) to take it from

Possible errors include:
1. Not identifying a valid card (invalid row/col choice)
2. Not identifying a valid slot index
3. Not satisfying placement requirements (the card's level isn't one above the level of the card it is being placed onto)
4. Not specifying correctly where to take the resources from/how many to take

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │               req_buy_dev_card │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────────────╮
               │                                ├─┤ try exec / check choices │
               │ update_dev_grid                │ ╰──────────────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_dev_card_choice            │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_payment_shelf_choice       │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_slot_choice                │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**req_buy_dev_card (client)**
```json
{
  "type": "req_buy_dev_card",
  "level": 1,
  "color": "Blue",
  "slot_index": 2,
  "res_choice": [
    {
      "res_type": "Coin",
      "shelf_index": 1,
      "quantity": 2
    }, {
      "res_type": "Coin",
      "shelf_index": 4, // two warehouse shelves cannot have the same resource ─► one of them refers to the strongbox
      "quantity": 1
    }
  ]
}
```
**err_dev_card_choice (server)**
```json
{
  "type": "err_dev_card_choice",
  "msg": "Cannot choose dev card in row 0 column 5: column 5 does not exist."
}
```
**err_payment_shelf_choice (server)**
```json
{
  "type": "err_payment_shelf_choice",
  "msg": "Cannot take 3 resource Coin from shelf 1: not enough resources."
}
```
**err_slot_choice (server)**
```json
{
  "type": "err_slot_choice",
  "msg": "Cannot assign dev card to slot 3: card level constraints not satisfied: required card of level 1 not present."
}
```

## Activating productions
The following information is needed when activating a production:
1. What productions to activate
2. For each resource of each production, the shelf (or strongbox) to take it from/put it into
3. For each replaceable resource slot in each production, the resource to replace it with

Possible errors include:
1. Specifying nonexistent productions
2. Incomplete/wrong specification of resource-shelf mappings
3. Incomplete/wrong specification of replaceable resources

In the example below, the production with ID 3 does not specify its output: this correlates with the presence of only faith points as the output (faith points are not assignable to shelves).
```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │              req_activate_prod │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────────────╮
               │                                ├─┤ try exec / check choices │
               │ update_shelves                 │ ╰──────────────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_prod_choice                │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_shelf_map_choice           │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_replacement_choice         │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**req_activate_prod (client)**
```json
{
  "type": "req_activate_prod",
  "productions": [
    {
      "id": 0,
      "inputBlanksRepl": [
        {
          "res": "Coin",
          "quantity": 2
        }, {
          "res": "Shield",
          "quantity": 1
        }
      ],
      "outputBlanksRepl": [ { "res": "Shield", "quantity": 1 } ],
      "inputShelves": [
          { "shelf": 1, "res": { "type": "Coin", "quantity": 1 } },
          { "shelf": 2, "res": { "type": "Coin", "quantity": 2 } },
          { "shelf": 0, "res": { "type": "Shield", "quantity": 2 } }
        ],
      "outputShelves": [
        { "shelf": 2, "res": { "type": "Stone", "quantity": 1 } }
      ]
    }, {
      "id": 3,
      "inputBlanksRepl": [ { "res": "Stone", "quantity": 1 } ],
      "outputBlanksRepl": [],
      "inputShelves": [
          { "shelf": 0, "res": { "type": "Stone", "quantity": 2 } }
        ],
      "outputShelves": []
    }
  ]
}
```
**err_prod_choice (server)**
```json
{
  "type": "err_prod_choice",
  "msg": "Cannot activate production 17: production 17 does not exist."
}
```
**err_shelf_map_choice (server)**
```json
{
  "type": "err_shelf_map_choice",
  "msg": "Cannot place 3 resource Coin in shelf 2: wrong resource type, shelf 2 is bound to Shield."
}
```
**err_replacement_choice (server)**
```json
{
  "type": "err_replacement_choice",
  "msg": "Replacements incomplete: production 3 requires 3 replacements, only 2 specified."
}
```

## Ending the turn
Since the server cannot at any point assume that the player has finished choosing their moves (see [secondary actions](#secondary-actions)), an explicit message has to be sent.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │                   req_turn_end │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────╮
               │                                ├─┤ exec │
               │ res_turn_end                   │ ╰──────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**req_turn_end (client)**
```json
{ "type": "req_turn_end" }
```
**res_turn_end (server)**
```json
{ "type": "res_turn_end" }
```


# Secondary actions
Secondary moves can be performed as often as the player wants and at any point of the turn. They are:
1. Swapping the content of the warehouse's shelves (the leader cards' depots can be included in the choice)
2. Activating/discarding a leader card

## Swapping two shelves' content
During their turn, the player can decide to reorder the warehouse.

This is technically only useful when taking resources from the market, as no other action refills the shelves, but it was left as an always-possible operation to improve the gameplay experience.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │               req_swap_shelves │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────────────╮
               │                                ├─┤ try exec / check choices │
               │ update_shelves                 │ ╰──────────────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_shelf_swap                 │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**req_swap_shelves (client)**
```json
{
  "type": "req_swap_shelves",
  "shelves": [ 0, 3 ]
}
```
**shelf_swap_choice_err (server)**
```json
{
  "type": "err_shelf_swap",
  "msg": "Shelves 0 and 3 cannot be swapped: too many resources in shelf 3."
}
```

## Leader actions
During their turn, in addition to one of the main three actions, a player can choose to discard or activate their leader cards (acting on one or both, performing both actions or the same action twice in the same turn).

To activate or discard a leader the server needs to know which card(s) the player wants to act on.

Leader activation:

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │            req_activate_leader │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────────────╮
               │                                ├─┤ try exec / check choices │
               │ update_leaders                 │ ╰──────────────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**req_activate_leader (client)**
```json
{
  "type": "req_activate_leader",
  "leader_id": 0
}
```
If a leader is already active no error is raised, since it's not a critical event.

Discarding a leader:

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │             req_discard_leader │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────────────╮
               │                                ├─┤ try exec / check choices │
               │ update_leaders                 │ ╰──────────────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │ err_leader_discard             │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**req_discard_leader (client)**
```json
{
  "type": "req_discard_leader",
  "leader_id": 0
}
```
**err_leader_discard (server)**
```json
{
  "type": "err_leader_discard",
  "msg": "Leader 0 cannot be discarded: leader 0 is active."
}
```


# State messages
These messages are used to update the clients' caches so that the data is synchronized with the server's.

The server automatically sends incremental updates to the player whenever an object changes.  
With that said, in order to carry out a choice-heavy move, the player may want to see objects that were updated long before. On the GUI side, the player can glance at the entire board, but when playing from the CLI finding the last update of an object would be unoptimal.  
In those cases the player can request a fresh view of the objects via commands, which, instead of reaching the server, will be served by the local cache. This allows for improved responsiveness and cuts back on the amount of data transferred, simplifying the communication protocol as well.  
This solution allows every player to prepare their moves before their turn comes, speeding up the gameplay and improving the experience.

Indices reference the data given in [game start](#game-start).

All messages are broadcast to all players, as the game rules don't specify that some objects should remain hidden.


## Updating the current player

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ update_cur_player              │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**update_cur_player (server)**
```json
{
  "type": "update_cur_player",
  "nickname": "Y"
}
```

## Updating the market

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               ┆                                ┆
               │ update_market                  │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**update_market (server)**
```json
{
  "type": "update_market",
  "isRow": true,
  "index": 1,
  "resources": [ "Coin", "Shield", "Stone" ],
  "slide_res": "Shield"
}
```

## Updating the player's shelves

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ update_shelves                 │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**update_shelves (server)**
```json
{
  "type": "update_shelves",
  "shelves": [
    {
      "index": 0,
      "bound_res": "Coin",
      "quantity": 1
    }, {
      "index": 1,
      "bound_res": null,
      "quantity": 0
    }
  ]
}
```

## Updating the player's leader cards

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ update_leaders                 │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**update_leaders (server)**
```json
{
  "type": "update_leaders",
  "index": 1,
  "isActive": true
}
```

## Updating the development card grid

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ update_dev_grid                │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**update_dev_grid (server)**
```json
{
  "type": "update_dev_grid",
  "row_index": 1,
  "col_index": 2,
  "card_index": 4
}
```

## Updating the player's development card slots

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ update_dev_card_slot           │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**update_dev_card_slot (server)**
```json
{
  "type": "update_dev_card_slot",
  "slot_index": 0,
  "card_index": 7
}
```

## Updating the player's position on the faith track

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ update_faith_track             │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**update_faith_track (server)**
```json
{
  "type": "update_faith_track",
  "isLiM": false,
  "position": 14
}
```

## Sending the activated solo action token

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ update_solo_token              │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**update_solo_token (server)**
```json
{
  "type": "update_solo_token",
  "index": 6
}
```

## Declaring a winner

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │ update_winner                  │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               ┆                                ┆
```
**update_winner (server)**
```json
{
  "type": "update_winner",
  "msg": "Player X wins!",
  "victory_points": [
    { "nickname": "X", "quantity": 20 },
    { "nickname": "Y", "quantity": 16 },
    { "nickname": "Z", "quantity": 12 }
  ]
}
```