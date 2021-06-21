# Table of Contents
1. [Communication protocol documentation](#communication-protocol-documentation)
    1. [Common data structures](#common-data-structures)
2. [Client-server connection management - Network level](#client-server-connection-management---network-level)
    1. [Connecting to the server](#connecting-to-the-server)
    2. [Closing the connection](#closing-the-connection)
    2. [UpdateServerUnavailable](#updateserverunavailable)
    3. [Reconnection](#reconnection)
    4. [Heartbeat](#heartbeat)
    5. [Errors](#errors)
3. [Client-server connection management - Application level](#client-server-connection-management---application-level)
    1. [Choosing a nickname](#choosing-a-nickname)
    2. [Choosing the number of players](#choosing-the-number-of-players)
    3. [Quitting the game](#quitting-the-game)
    4. [Reconnecting](#reconnecting)
4. [General Errors](#general-errors)
    1. [ErrAction](#erraction)
    2. [ErrNoSuchEntity](#erraction)
    3. [ErrObjectNotOwned](#errobjectnotowned)
    4. [ErrReplacedTransRecipe](#errreplacedtransrecipe)
    5. [ErrResourceReplacement](#errresourcereplacement)
    6. [ErrResourceTransfer](#errresourcetransfer)
5. [Game phase - Player setup](#game-phase---player-setup)
    1. [Choosing leader cards](#choosing-leader-cards)
    2. [Choosing starting resources](#choosing-starting-resources)
6. [Game phase - Turns](#game-phase---turns)
    1. [Main actions](#main-actions)
        1. [Getting resources from the market](#getting-resources-from-the-market)
        2. [Buying a development card](#buying-a-development-card)
        3. [Activating productions](#activating-productions)
        4. [Ending the turn](#ending-the-turn)
    2. [Secondary actions](#secondary-actions)
        1. [Swapping two shelves](#swapping-two-shelves)
        2. [Leader Actions](#leader-actions)
            1. [Activating a leader card](#activating-a-leader-card)
            2. [Discarding a leader card](#discarding-a-leader-card)
    3. [State messages](#state-messages)
        1. [UpdateActionToken](#updateactiontoken)
        2. [UpdateActivateLeader](#updateactivateleader)
        3. [UpdateCurrentPlayer](#updatecurrentplayer)
        4. [UpdateDevCardGrid](#updatedevcardgrid)
        5. [UpdateDevCardSlot](#updatedevcardslot)
        6. [UpdateFaithPoints](#updatefaithpoints)
        7. [UpdateGame](#updategame)
            1. [Caching](#caching)
            2. [Parameters and indices](#parameters-and-indices)
        8. [UpdateGameEnd](#updategameend)
        9. [UpdateLastRound](#updatelastround)
        10. [UpdateLeadersHand](#updateleadershand)
        11. [UpdateLeadersHandCount](#updateleadershandcount)
        12. [UpdateMarket](#updatemarket)
        13. [UpdatePlayer](#updateplayer)
        14. [UpdatePlayerStatus](#updateplayerstatus)
        15. [UpdateResourceContainer](#updateresourcecontainer)
        16. [UpdateSetupDone](#updatesetupdone)
        17. [UpdateVaticanSection](#updatevaticansection)
        18. [UpdateVictoryPoints](#updatevictorypoints)

# Communication protocol documentation
This document describes the client-server communication protocol used by the implementation of the Masters of Renaissance game written by group AM49.

All messages are encoded using the GSON library and follow therefore the JSON specification, language-wise.  
Every value shown in the messages is to be taken as an example, having been written only to show the messages' structure.  

Important details about state update messages in TODO.

`UpdateAction` messages confirm the success of the requested action and mark the end of the state update messages stream.

Error messages are unicast to the client that sent the illegal request.

### Common data structures
Two data structures that are often used inside messages are:
1. ***Resource maps*** - correlate a string, representing a resource type, with an integer, corresponding to its amount
2. ***Resource container maps*** - correlate a container ID (expressed as an integer) with a resource map, corresponding to the resource(s) to add to/remove from it


# Client-server connection management - Network level
To make the protocol more resilient and situation-aware, `NetEvents` have been separated from application-level events.  

The player, when starting the client, can choose whether to connect to the server (singleplayer and multiplayer playmodes) or playing locally (singleplayer only).  
In both cases the messages sent are the same and happen in the exact same way: the network side of the project has been implemented to allow changing the transport layer transparently to both the client and the server.

## Connecting to the server
Upon connection, a handshake takes place:

```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │ ReqWelcome                     │
     ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
     │                                │
     │                     ResWelcome │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
```
**ReqWelcome (client)**
```json
{
  "type": "ReqWelcome"
}
```
**ResWelcome (server)**
```json
{
  "type": "ResWelcome"
}
```

## Closing the connection
The event of the connection closing can happen for three reasons:
1. The player quits the game
2. The client crashes
3. The server crashes

Quitting the client sends one final application-level message to the server (see TODO), allowing the server to distinguish between the situations.
After the server has executed the quit routine, the sockets can now be closed.  
A `ReqGoodbye` event is sent by the client:

```json
{ "type": "ReqGoodbye" }
```

A `ResGoodbye` message is sent by the server as an acknowledgement:

```json
{
  "type": "ResGoodbye"
}
```

Only then the network sockets is closed by both parties.

## UpdateServerUnavailable
This message is used internally by the network layer to notify the application layer of the fact that the connection with the server was closed by the server itself.

**UpdateServerUnavailable**
```json
{ "type": "UpdateServerUnavailable" }
```

## Reconnection
The player is allowed to connect back to the server and join the game they were previously in.  
The reconnection routine at the network level is exactly the same as a normal connection.

## Heartbeat
The server checks whether the clients are still alive using both normal messages and heartbeat messages (the timeout resets with any kind of message).  
Heartbeat messages are sent at regular intervals.

```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │                   ReqHeartbeat │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
     │ ResHeartbeat                   │
     ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
     │                                │
```
**ReqHeartbeat (server)**
```json
{
  "type": "ReqHeartbeat"
}
```
**ResHeartbeat (client)**
```json
{
  "type": "ResHeartbeat"
}
```

## Errors
Broken (unparsable) messages are signaled with a `ErrProtocol` messsage containing information about the error itself:

```json
{
  "type": "ErrProtocol",
  "msg": "Error parsing the message: invalid token 'a'"
}
```



# Client-server connection management - Application level
A summary of the requirements highlighting the relevant parts is reported below:
> - On player connection: the player is automatically added to the game currently being filled. If there is no game in its starting phase, a new one is created.
> - The player starting a new game chooses how many players have to join before the game can start.
> - The game starts as soon as the specified number of players (given by the first player to join) is reached.
> - The server manages the players' turns as per the game's rules. The server must handle a player disconnecting or leaving the game; if there are no players left the game will terminate and all players have to be notified.

The following specification for the additional feature "Multiple Games" is taken into account in the communication protocol's design:
> - Only one waiting room is used to manage the players who join the server.
> - Players who disconnect can successively reconnect to continue the match. While a player isn't connected, the game continues skipping the player's turns.

Given those requirements, the communication at connection time has been modeled the following way.

## Choosing a nickname
After establishing a connection with the server, the client will ask the player to input a nickname of their choice. The entry is sent to the server, and, if acceptable (unique among the registered nicknames, not empty, not already set), is accepted. Else, the server will signal the error, restarting the process.

Information about the player being the first of the match is included in the response sent to the client. This is necessary to [handle the choice of the number of players](#choosing-the-number-of-players).

```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │ ReqJoin                        │
     ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
     │                                │ ╭───────────────────────╮
     │                                ├─┤ check nickname unique │
     │              UpdateBookedSeats │ ╰───────────────────────╯
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
     │                    ErrNickname │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
```
**ReqJoin (client)**
```json
{
  "type": "ReqJoin",
  "nickname": "NicknameA"
}
```
**UpdateBookedSeats (server)**
```json
{
  "type": "UpdateBookedSeats",
  "bookedSeats":1,
  "canPrepareNewGame":"NicknameA",
}
```
**ErrNickname (server)**
```json
{
  "type": "ErrNickname",
  "reason": "TAKEN"
}
```

The `UpdateBookedSeats` message gives the client information about who is the first in the waiting list (and therefore can choose the new game's player count) and the amount of players in the waiting list.
When the match is waiting for players to join before its start, sending notifications allows the players who already joined to know how many empty seats are left, therefore getting a sense for how much waiting time there's left.

## Choosing the number of players
When a player is assigned by the server as the first of a new game, they have to decide the number of players required to start it.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │
╰────────────╯ │ ReqNewGame                     │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭─────────────╮
               │                                ├─┤ try setting │
               │                 UpdateJoinGame │ ╰─────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqNewGame (client)**
```json
{
  "type": "ReqNewGame",
  "playersCount": 3
}
```
**UpdateJoinGame (server)**
```json
{
  "type": "UpdateJoinGame",
  "playersCount": 3
}
```
**ErrNewGame (server)**
```json
{
  "type": "ErrNewGame",
  "isInvalidPlayersCount": false
}
```

The `UpdateJoinGame` message signals to the first `playersCount` waiting clients that a new match is undergoing creation and the number of players included. Any other client that joins the server and is assigned to the starting match will also receive the message.  
If a `ReqNewGame` is received from a client that is not allowed to request a new match, `ErrNewGame.isInvalidPlayersCount` is set to false.

See the [Game start](#game-start) section for information on how the protocol defines the initial data transfer after joining a match.

## Quitting the game
When the player quits the game, the client will send a `ReqQuit` message:

**ReqQuit (client)**
```json
{
  "type":"ReqQuit"
}
```

The server will then execute an internal routine that will allow the player to reconnect at a later time and join back the match they were in, notifying at the same time the other players of the match of the event (see TODO player status).  
A `ResQuit` message is sent to the client to notify it about the closing routine being completed.  

**ResQuit (server)**
```json
{
  "type":"ResQuit"
}
```

The network-level closing routine will then start (see TODO).

## Reconnecting
A player can reconnect to a match they left, if it's still ongoing.  
This is done at connection time by choosing the same nickname as they previously had.

When reconnecting, the server will send all the necessary game data for the client to cache (see TODO game start). The client will therefore be able to judge what phase of the game is currently in place (setup, turns, who the current player is, etc.).



# General Errors
Error messages that are sent in multiple occasions are reported here.  
Any successive mentions of these messages refer to this section for syntax and examples.

## ErrAction
This message signals to the client that the action is being requested at the wrong time.  
The `reason` field offers a more detailed explanation:
1. `LATE_SETUP_ACTION` - a setup request is sent after the setup phase is concluded
2. `EARLY_MANDATORY_ACTION` - an action request (non-setup) is sent during the setup phase
3. `LATE_MANDATORY_ACTION` - a action request (non-setup) is sent for the second time during a player's turn
4. `EARLY_TURN_END` - a request to end the player's turn is sent before a mandatory action request
5. `GAME_ENDED` - an action request is sent after the match's end
6. `NOT_CURRENT_PLAYER` - the player requesting the action is not the current player

**ErrAction (server)**
```json
{
  "type": "ErrAction",
  "reason": "LATE_SETUP_ACTION"
}
```

## ErrNoSuchEntity
This message signals the absence of an entity to match an ID with.

The `originalEntity` field describes the kind of entity the request pertained to, which include:
1. `MARKET_INDEX` - index of a market's row/column does not exist
2. `LEADER` - leader card with referenced ID does not exist
3. `DEVCARD` - development card with referenced ID does not exist
4. `COLOR` - referenced color does not exist
5. `RESOURCE` - referenced resource type does not exist

The message also reports the `id` or the `code` string of the missing object.

**ErrNoSuchEntity (server)**
```json
{
  "type": "ErrNoSuchEntity",
  "originalEntity": "LEADER",
  "id": 50,
  "code": null
}
```

## ErrObjectNotOwned
When a request message from a client references the ID of an object that is not owned by the player, an `ErrObjectNotOwned` message is sent by the server, detailing the erroneus ID and the object's kind.

**ErrObjectNotOwned (server)**
```json
{
  "type": "ErrObjectNotOwned",
  "id": "NicknameA",
  "objectType": "LeaderCard"
}
```

## ErrReplacedTransRecipe
This message signals a discrepancy between the available and specified numbers of resources to be put in a container.

The `replacedCount` field details the number of available resources in the transaction after the replacements have been factored in.  
The `shelvesChoiceResCount` field details the number of resources requested to be put in the container.  
The `isIllegalDiscardedOut` field specifies whether the discrepancy is to be reconduced to illegally discarded resources in output.

**ErrReplacedTransRecipe (server)**
```json
{
  "type": "ErrReplacedTransRecipe",
  "resType": "Coin",
  "replacedCount": 3,
  "shelvesChoiceResCount": 4,
  "isIllegalDiscardedOut": false
}
```

## ErrResourceReplacement
This message signals an error when replacing a production's resources.

The `isNonStorable` field is set to true when a resource in the request is defined as non-storable and takeable/givable to players.  
The `isExcluded` field is set to true if a forbidden resource type is requested as a replacement.  
The `replacedCount` and `blanks` fields are used to indicate a discrepancy between the number of specified replacements and the amount of blanks needing to be filled.

**ErrResourceReplacement (server)**
```json
{
  "type": "ErrResourceReplacement",
  "isInput": true,
  "isNonStorable": false,
  "isExcluded": false,
  "replacedCount": 1,
  "blanks": 0
}
```

## ErrResourceTransfer
This error signals an error with a resource transfer request.

Reasons included in the message are:
1. `BOUNDED_RESTYPE_DIFFER` - a resource is trying to be added/removed to a shelf that's bound to another resource type
2. `NON_STORABLE` - a non-storable resource is trying to be added/removed to a resource container
3. `CAPACITY_REACHED` - the resource transfer requests that the number of resulting resources in the container is either less than zero or greater than the container's capacity
4. `DUPLICATE_BOUNDED_RESOURCE` - a resource is trying to be added to a shelf while there's another shelf bound to the same resource type

**ErrResourceTransfer (server)**
```json
{
  "type": "ErrResourceTransfer",
  "resType": "Coin",
  "isAdded": true,
  "reason": "CAPACITY_REACHED"
}
```



# Game phase - Player setup
When the game starts, the server instantiates its internal model.  
It will then send the game data to the clients (see TODO), and the player setup phase will start.

During this phase the players will have to choose the amount of leader cards and resources specified in the configuration file.

## Choosing leader cards
As per the game's rules, the players have to decide manually what leader cards they want to keep for the match's
duration.

The client is sent the IDs of the leader cards they can choose from, and will send back a subset of them.

Errors related to this action are:
1. `ErrAction` message, with reason `LATE_SETUP_ACTION` - the request message is sent too late (the setup phase is already concluded)
2. `ErrObjectNotOwned` - the request message contains IDs that are not in the player's card list
3. `ErrInitialChoice` - the request message contains too few IDs or the leader cards have alerady been chosen by the player

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
╭────────────╮ │                                │
│ user input ├─┤                                │
╰────────────╯ │ ReqChooseLeaders               │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        *state update messages* │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │              ErrObjectNotOwned │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │               ErrInitialChoice │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqChooseLeaders (client)**
```json
{
  "type": "ReqChooseLeaders",
  "leaders": [ 3, 15 ]
}
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "CHOOSE_LEADERS",
  "player": "NicknameA"
}
```
**ErrInitialChoice (server)**
```json
{
  "type": "ErrInitialChoice",
  "isLeadersChoice": true,
  "missingLeadersCount": 2
}
```

## Choosing starting resources
After choosing the leader cards the players is prompted to choose their starting resources, following the configuration file's settings.

The client is sent the amount of resources and the resource types the player has to choose from (see TODO).  
Every player will have to choose the resources before the match's turns can start, thereby concluding the setup phase.

The `shelves` field is a standard [resource container map](#common-data-structures)

Error messages are fired in these situations:
1. `ErrAction` message, with reason `LATE_SETUP_ACTION` - the request message is sent too late (the setup phase is already concluded)
2. `ErrObjectNotOwned` - the request message contains resource container IDs that are not owned the player
3. `ErrNoSuchEntity` - a resource type that doesn't exist is specified
4. `ErrInitialChoice` - the resurces were already chosen
5. `ErrResourceTransfer` - a number of resources exceeding the shelf's capacity is requested to be added to a shelf

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
╭────────────╮ │                                │
│ user input ├─┤                                │
╰────────────╯ │ ReqChooseResources             │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        *state update messages* │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │              ErrObjectNotOwned │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                ErrNoSuchEntity │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │               ErrInitialChoice │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │            ErrResourceTransfer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqChooseResources (client)**
```json
{
  "type": "ReqChooseResources",
  "shelves": [
    { "1": { "Coin": 1 } },
    { "0": { "Shield": 1 } }
  ]
}
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "CHOOSE_RESOURCES",
  "player": "NicknameA"
}
```
**ErrAction (server)**
```json
{
  "type": "ErrChooseResources",
  "msg": "Cannot choose 2 starting resources, only 1 available."
}
```
**ErrInitialChoice (server)**
```json
{
  "type": "ErrInitialChoice",
  "isLeadersChoice": false,
  "missingLeadersCount": 0
}
```



# Game phase - Turns
After all players have gone through the setup phase, the server will start the turn loop.

The messages in this section can be differentiated into:

1. [Main actions](#main-actions), of which the player has to make only one during the turn
2. [Secondary actions](#secondary-actions), which can be repeated within the player's turn
3. [State messages](#state-messages), which update the local caches' state to match the server's



# Main actions
During their turn, the player has to choose which action to take among these:
1. Getting resources from the market
2. Buying a development card
3. Activating the production

Since the player may want to make a secondary move after the main action, the server waits for a `TurnEnd` message before switching to the next player.

## Getting resources from the market
The following needs to be specified:
1. Which row/column the player wants to take the resources from
2. For each replaceable resource, which resource type to convert it to (if applicable)
3. For each resource (its type considered after the leaders' processing), which shelf to use to store it
4. What resources, among the ones taken from the market, to discard

Discarding is simply handled by specifying a lower quantity of resources to add to a shelf. This also easily matches the rule for which only the resources given by the market can be discarded.

The `replacements` field specifies how the resource conversion should be handled. Since the player knows what type of resource the leader converts to, they can easily select them by specifying, for each type of resource they want as output, how many replaceable resources (of the available ones) to use.

The `replacement` field is a standard [resource map](#common-data-structures)
The `shelves` field is a standard [resource container map](#common-data-structures)

Errors may arise from fitting the resources in the shelves, either by specifying the wrong shelf or by not discarding enough resources.
See TODO general errors for more details on the errors that may be generated by the request.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │ ReqTakeFromMarket              │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        *state update messages* │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │              ErrObjectNotOwned │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                ErrNoSuchEntity │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │         ErrResourceReplacement │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │         ErrReplacedTransRecipe │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │            ErrResourceTransfer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqTakeFromMarket (client)**
```json
{
  "type": "ReqTakeFromMarket",
  "isRow": true,
  "index": 0,
  "replacements": { "Coin": 2 },
  "shelves": [
    { "1": { "Coin": 2 } },
    { "3": { "Shield": 1 } }
  ]
}
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "TAKE_MARKET_RESOURCES",
  "player": "NicknameA"
}
```

## Buying a development card
The following information is needed when buying a development card:
1. The row and column of the card to identify it in the grid
2. The slot to place the card into
3. For each resource that has to be paid, the shelf (or strongbox) to take it from

The `resContainers` field is a standard [resource container map](#common-data-structures)

An `ErrBuyDevCard` message signals:
1. the selected color/level refer to an empty deck of cards (`isStackEmpty` is set to true)
2. the card's level does not allow the card to be placed in the selected slot (`isStackEmpty` is set to false)

An `ErrCardRequirements` message signals which cost requirements the player is missing.

The `missingResources` field is a standard [resource map](#common-data-structures)

Other possible errors include not identifying a valid card/slot, not satisfying placement requirements (the card's level isn't one above the level of the card it is being placed onto) and not specifying correctly the resource transaction's details.  
See TODO general errors for more details on the errors that may be generated by the request.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │ ReqBuyDevCard                  │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        *state update messages* │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │              ErrObjectNotOwned │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                ErrNoSuchEntity │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │         ErrResourceReplacement │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │         ErrReplacedTransRecipe │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │            ErrResourceTransfer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                  ErrBuyDevCard │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │            ErrCardRequirements │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqBuyDevCard (client)**
```json
{
  "type": "ReqBuyDevCard",
  "level": 1,
  "color": "Blue",
  "devSlot": 2,
  "resContainers": [
    { "1": { "Coin": 2 } },
    { "3": { "Shield": 1 } }
  ]
}
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "BUY_DEVELOPMENT_CARD",
  "player": "NicknameA"
}
```
**ErrBuyDevCard (server)**
```json
{
  "type": "ErrBuyDevCard",
  "isStackEmpty": true
}
```
**ErrCardRequirements (server)**
```json
{
  "type": "ErrCardRequirements",
  "missingDevCards": null,
  "missingResources": { "Coin": 1, "Shield": 2 }
}
```

## Activating productions
The following information is needed when activating productions:
1. Which productions to activate
2. For each production, a 
3. Each non-storable resource chosen as a replacement in the input
4. The storable and non-storable output replacements

The `inputContainers` field is a standard [resource container map](#common-data-structures) detailing all (default and replaced) resources in input. The non-storable replacements have to be specified by themselves in the `inputNonStorableRep` field (which is a standard [resource map](#common-data-structures)).
The `outputRep` field specifies the replacements pertaining to the output side of the productions (it's a standard [resource map](#common-data-structures) since all output goes to the player's strongbox).

Errors may originate from issues with referencing inexistent/not owned objects, resource replacements and resource transfers.  
See TODO for more details on a specific error.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │ ReqActivateProd                │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        *state update messages* │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │              ErrObjectNotOwned │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                ErrNoSuchEntity │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │         ErrResourceReplacement │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │         ErrReplacedTransRecipe │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │            ErrResourceTransfer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqActivateProd (client)**
```json
{
  "type": "ReqActivateProd",
  "prodRequests": [
    {
      "id": 0,
      "outputRep": { "Faith": 1 },
      "inputContainers": [
        { "1": { "Coin": 1 } },
        { "2": { "Coin": 2 } },
        { "0": { "Shield": 2 } }
      ]
    }, {
      "id": 3,
      "outputRep": { "Faith": 1 },
      "inputContainers": [
        { "0": { "Stone": 2 } }
      ],
      "inputNonStorableRep": { "Faith": 1 }
    }
  ]
}
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "ACTIVATE_PRODUCTION",
  "player": "NicknameA"
}
```

## Ending the turn
Since the server cannot at any point assume that the player has finished choosing their moves (see [secondary actions](#secondary-actions)), an explicit message has to be sent.

If a player ends their turn early (without having done a mandatory action) an `ErrAction` with `"reason": "EARLY_TURN_END"` is generated.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │ ReqEndTurn                     │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────╮
               │                                ├─┤ exec │
               │        *state update messages* │ ╰──────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqEndTurn (client)**
```json
{ "type": "ReqEndTurn" }
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "END_TURN",
  "player": "NicknameA"
}
```



# Secondary actions
Secondary moves can be performed as often as the player wants and at any point of the turn. They are:

1. Swapping the content of the player's shelves
2. Activating/discarding a leader card

## Swapping two shelves
During their turn, the player can decide to reorder the content of their warehouse's shelves and leader depots.

This is technically only useful when taking resources from the market, as no other action refills the shelves, but it was left as an always-possible operation to improve the gameplay experience.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │ ReqSwapShelves                 │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        *state update messages* │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │              ErrObjectNotOwned │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │            ErrResourceTransfer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqSwapShelves (client)**
```json
{
  "type": "ReqSwapShelves",
  "shelf1": 0,
  "shelf2": 3
}
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "SWAP_SHELVES",
  "player": "NicknameA"
}
```

## Leader actions
During their turn, in addition to one of the main three actions, a player can choose to discard or activate their leader cards.

To activate or discard a leader the server needs to know which card the player wants to act on and which action to perform.

### Activating a leader card

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │ ReqLeaderAction                │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        *state update messages* │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │              ErrObjectNotOwned │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │            ErrCardRequirements │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqLeaderAction (client)**
```json
{
  "type": "ReqLeaderAction",
  "leader": 0,
  "isActivate": true
}
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "ACTIVATE_LEADER",
  "player": "NicknameA"
}
```
**ErrCardRequirements (server)**
```json
{
  "type": "ErrCardRequirements",
  "missingDevCards": [
    {
      "color": "Blue",
      "level": 1,
      "amount": 2
    }
  ],
  "missingResources": null
}
```

If a leader is activated while already active no error is raised, since it's not a critical event.

### Discarding a leader card

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
╭────────────╮ │                                │
│ user input ├─┤                                │ 
╰────────────╯ │ ReqLeaderAction                │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        *state update messages* │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │              ErrObjectNotOwned │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │       ErrActiveLeaderDiscarded │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqLeaderAction (client)**
```json
{
  "type": "ReqLeaderAction",
  "leader": 0,
  "isActivate": false
}
```
**UpdateAction (server)**
```json
{
  "type": "UpdateAction",
  "action": "DISCARD_LEADER",
  "player": "NicknameA"
}
```
**ErrActiveLeaderDiscarded (server)**
```json
{
  "type": "ErrActiveLeaderDiscarded"
}
```

The `ErrActiveLeaderDiscarded` error message is sent by the server when a player tries to discard an active leader card.



# State messages
These messages are used to update the clients' caches so that the data is synchronized with the server's.

State update messages are sent autonomously by the updated entities. Clients cannot assume that a state update message will be sent or the timing it will be sent with (ordering with respect to other messages, etc.).  
State update messages are broadcast unless specified.

IDs reference the data given in [game start](#game-start).

## UpdateActionToken
Notifies clients of the activation of an action token, sending its ID.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │              UpdateActionToken │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateActionToken (server)**
```json
{
  "type": "UpdateActionToken",
  "actionToken": 6
}
```

## UpdateActivateLeader
Notifies clients when a leader card is activated.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │           UpdateActivateLeader │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateActivateLeader (server)**
```json
{
  "type": "UpdateActivateLeader",
  "leader": 1
}
```

## UpdateCurrentPlayer
Notifies clients of who the current player is from the moment the message is sent.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │            UpdateCurrentPlayer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateCurrentPlayer (server)**
```json
{
  "type": "UpdateCurrentPlayer",
  "nickname": "NicknameA"
}
```

## UpdateDevCardGrid
Notifies the clients of an update in the development card grid's state.

The list of IDs are the respective top cards of each deck, the level of which is the card's position in the list and the color of which is specified in the enclosing map.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │              UpdateDevCardGrid │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateDevCardGrid (server)**
```json
{
  "type": "UpdateDevCardGrid",
  "topCards": {
    "levelsCount": 3,
    "colorsCount": 4,
    "grid": {
      "Purple": [
        [ 2, 0, 1, 3 ]
      ],
      "Green": [
        [ 8, 7, 6, 4 ]
      ]
    }
  }
}
```

## UpdateDevCardSlot
Notifies the clients of a card being added to a player's board's development card slot.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │              UpdateDevCardSlot │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateDevCardSlot (server)**
```json
{
  "type": "UpdateDevCardSlot",
  "slot": 0,
  "card": 7
}
```

## UpdateFaithPoints
Notifies clients of a player progressing on the faith track.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │               UpdateFaithTrack │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateFaithTrack (server)**
```json
{
  "type": "UpdateFaithTrack",
  "isBlackCross": false,
  "faithPoints": 14,
  "player": "NicknameA"
}
```

## UpdateGame
As the game starts, the server notifies all players of the event.

### Caching
The server sends the game's state to be cached by the clients. Caching parts of the game's state allows the clients to answer requests without the server's intervention.  
Caching allows partial checks to be preemptively (but not exclusively) done client side: if the player specifies an index that's out of bounds, the client is able to catch the error before sending the request to the server, reducing network and server loads and improving the game's responsiveness.

### Parameters and indices
The game's model has been parameterized to allow for flexibility. The parameters are set via
a [configuration file](../src/main/resources/config/config.json), which also contains serialized game data (e.g. cards,
resources, etc...).  

Clients have a default configuration file embedded to allow for local single player matches. Both clients and server also support loading custom configuration files.

Since the file on a server may be different from the one embedded in a client, all game elements need to be sent at the start of a match, ensuring proper synchronization between the clients and the server, both in terms of IDs and actual game data.

```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │                     UpdateGame │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
```
**UpdateGame (server)**
```json
{
  "type": "UpdateGame",
  "colors": [
    {
      "name": "Yellow",
      "colorValue": "\u001B[33m"
    }, {
      "name": "Purple",
      "colorValue": "\u001B[35m"
    }
  ],
  "resourceTypes": [
    {
      "name": "Servant",
      "storable": true,
      "colorValue": "\u001B[95m",
      "isGiveableToPlayer": true,
      "isTakeableFromPlayer": true
    }, {
      "name": "Zero",
      "storable": false,
      "colorValue": "\u001B[37m",
      "isGiveableToPlayer": false,
      "isTakeableFromPlayer": false
    }
  ],
  "players": [ "NicknameA", "NicknameB", "NicknameC" ],
  "leaderCards": [
    {
      "resourceType": "Servant",
      "leaderType": "DiscountLeader",
      "devCardRequirement": {
        "entries": [
          { "color": "Yellow", "amount": 1, "level": 2 }
        ]
      },
      "isActive": false,
      "containerId": -1,
      "discount": 1,
      "id": 0,
      "victoryPoints": 2,
      "production": -1
    }
  ],
  "developmentCards": [
    {
      "color": "Green",
      "cost": {
        "requirements": { "Shield": 1 }
      },
      "level": 1,
      "id": 0,
      "victoryPoints": 1,
      "production": 5
    }
  ],
  "resContainers": [
    {
      "id": 4,
      "content": {},
      "boundedResType": "Stone",
      "size": 2
    }, {
      "id": 3,
      "content": {},
      "size": -1
    }
  ],
  "productions": [
    {
      "id": 0,
      "input": {},
      "inputBlanks": 2,
      "inputBlanksExclusions": [],
      "output": {},
      "outputBlanks": 1,
      "outputBlanksExclusions": [ "Faith" ],
      "discardableOutput": false
    }
  ],
  "actionTokens": [
    {
      "id": 0,
      "kind": "ActionTokenBlackMoveOneShuffle"
    }, {
      "id": 3,
      "kind": "ActionTokenDiscardTwo",
      "discardedDevCardColor": "Blue"
    }
  ],
  "faithTrack": {
    "vaticanSections": {
      "16": {
        "id": 2,
        "faithPointsBeginning": 12,
        "faithPointsEnd": 16,
        "victoryPoints": 3,
        "activated": false,
        "bonusGivenPlayers": []
      }
    },
    "yellowTiles": [
      {
        "faithPoints": 12,
        "victoryPoints": 6
      }
    ]
  },
  "isSetupDone": false,
  "slotsCount": 3
}
```

## UpdateGameEnd
Notifies the clients of the end of the match, detailing the winner player.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │                  UpdateGameEnd │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateGameEnd (server)**
```json
{
  "type": "UpdateGameEnd",
  "winner": "NicknameA"
}
```

## UpdateLastRound
Notifies the clients of the current round being the last.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │                UpdateLastRound │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateLastRound (server)**
```json
{
  "type": "UpdateLastRound"
}
```

## UpdateLeadersHand
A player's leader cards are hidden from the other players until activated. This message is therefore sent only to the owner of the cards, whom can see their IDs.  
The other players will receive a TODO, which only contains the number of cards owned by the other player.  
Upon receiving a TODO, the non-owner players will know that the currently-playing player has activated a card and its ID. They can therefore add the ID to the current player's leader cards hand.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │              UpdateLeadersHand │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateLeadersHand (server)**
```json
{
  "type": "UpdateLeadersHand",
  "player": "NicknameA",
  "leaders": [ 3, 5 ]
}
```

## UpdateLeadersHandCount
Given the explanation of [UpdateLeadersHand](#updateleadershand), this message contains the number of leader card a player is holding.  
This allows for the cards' IDs to remain hidden while informing clients of events such as a card being discarded.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │         UpdateLeadersHandCount │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateLeadersHandCount (server)**
```json
{
  "type": "UpdateLeadersHandCount",
  "player": "NicknameA",
  "leadersCount": 1
}
```

## UpdateMarket
This message holds the current state of the match's market.

It specifies what resource type is to be accounted for as replaceable, since the configuration file allows for it to be changed.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │                   UpdateMarket │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateMarket (server)**
```json
{
  "type": "UpdateMarket",
  "market": {
    "grid": [
      [ "Coin", "Servant", "Stone", "Shield" ],
      [ "Shield", "Faith", "Zero", "Zero" ]
    ],
    "replaceableResType": "Zero",
    "slide": "Stone"
  }
}
```

## UpdatePlayer
This message holds the state of a player object.  
It is usually only sent at match start time, since other state messages take care of incrementally updating the player's data.

This message also contains the player's setup details, which are needed for the setup phase.  
The `chosenLeadersCount` field specifies how many leader cards must be retained during setup, among the ones sent in the first [UpdateLeadersHand](#updateleadershand) message the client receives.
In the same way, the `initialResources` field specifies the number of resources the player needs to choose during the setup phase.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │                   UpdatePlayer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdatePlayer (server)**
```json
{
  "type": "UpdatePlayer",
  "player": "NicknameA",
  "baseProduction": 0,
  "warehouseShelves": [ 0, 1, 2 ],
  "strongbox": 3,
  "playerSetup": {
    "chosenLeadersCount": 2,
    "initialResources": 1,
    "initialExcludedResources": [ "Faith" ],
    "hasChosenLeaders": false,
    "hasChosenResources":false
  }
}
```

## UpdatePlayerStatus
Notifications about players connecting/disconnecting from a match are sent via this message.

```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │   UpdatePlayerStatus │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
```
**UpdatePlayerStatus (server)**
```json
{
  "type": "UpdatePlayerStatus",
  "nickname": "NicknameA",
  "isActive": true
}
```

## UpdateResourceContainer
This message details the current state of the resource container with the specified ID.

Strongboxes have size set to -1.  
If there's no binding resource the `boundedResType` field is not specified.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │        UpdateResourceContainer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateResourceContainer (server)**
```json
{
  "type": "UpdateResourceContainer",
  "resContainer": {
    "id": 3,
    "content": {
      "Coin": 3
    },
    "boundedResType": "Coin",
    "size": 1
  }
}
```

## UpdateSetupDone
When every player has chosen their leader cards and starting resources the conclusion of the setup phase is notified to the clients.

```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │                UpdateSetupDone │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
```
**UpdateSetupDone (server)**
```json
{
  "type": "UpdateSetupDone"
}
```

## UpdateVaticanSection
This message contains the list of players who benefit from the vatican section's bonus points.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │           UpdateVaticanSection │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateVaticanSection (server)**
```json
{
  "type": "UpdateVaticanSection",
  "id": 60,
  "bonusGivenPlayers": "NicknameA"
}
```

## UpdateVictoryPoints
This message contains the current amount of victory points for the specified player.  
The victory points' amount is updated in real time.

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │            UpdateVictoryPoints │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateVictoryPoints (server)**
```json
{
  "type": "UpdateVictoryPoints",
  "player": "NicknameA",
  "victoryPoints": 20
}
```