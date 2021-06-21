# Table of Contents
1. [Communication protocol documentation](#communication-protocol-documentation)
2. [Client-server connection management - Network level](#client-server-connection-management---network-level)
    1. [Connecting to the server](#connecting-to-the-server)
    2. [Closing the connection](#closing-the-connection)
    3. [Reconnection](#reconnection)
    4. [Heartbeat](#heartbeat)
    5. [Errors](#errors)
3. [Client-server connection management - Application level](#client-server-connection-management---application-level)
    1. [Choosing a nickname](#choosing-a-nickname)
    2. [Choosing the number of players](#choosing-the-number-of-players)
    3. [Quitting the game](#quitting-the-game)
    4. [Reconnection](#reconnecting)
4. [General Errors](#general-errors)
    1. [ErrAction](#erraction)
    2. [ErrObjectNotOwned](#errobjectnotowned)
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
    3. [State messages](#state-messages)
        2. [Game start](#game-start)
        3. [Updating the players list](#updating-the-players-list)
        4. [Updating the current player](#updating-the-current-player)
        5. [Updating the market](#updating-the-market)
        6. [Updating the shelves](#updating-the-shelves)
        7. [Updating the leader cards](#updating-the-leader-cards)
        8. [Updating the development card grid](#updating-the-development-card-grid)
        9. [Updating the development card slots](#updating-the-development-card-slots)
        10. [Updating the position on the faith track](#updating-the-position-on-the-faith-track)
        11. [Updating the vatican sections](#updating-the-vatican-sections)
        12. [Sending the activated solo action token](#sending-the-activated-solo-action-token)
        12. [Notifying the last round](#notifying-the-last-round)
        13. [Declaring a winner](#declaring-a-winner)

# Communication protocol documentation
This document describes the client-server communication protocol used by the implementation of the Masters of Renaissance game written by group AM49.

All messages are encoded using the GSON library and follow therefore the JSON specification, language-wise.  
Every value shown in the messages is to be taken as an example, having been written only to show the messages' structure.  

State update messages are sent autonomously by the updated entities. Clients cannot assume that a state update message will be sent or the timing it will be sent with (ordering with respect to other messages, etc.).  
State update messages are broadcast unless specified.

`UpdateAction` messages confirm the success of the requested action and mark the end of the state update messages stream.

Error messages are unicast to the client that sent the illegal request.


# Client-server connection management - Network level
To make the protocol more resilient and situation-aware, `NetEvents` have been separated from application-level events.  

The player, when starting the client, can choose whether to connect to the server (singleplayer and multiplayer playmodes) or playing locally (singleplayer only).  
In both cases the messages sent will be the same and happen in the exact same way: the network side of the project has been implemented to allow changing the transport layer transparently to both the client and the server.

## Connecting to the server
Upon connection, a handshake will take place:

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

Quitting the client will send one final application-level message to the server (see TODO), allowing the server to distinguish between the situations.
After the server has executed the quit routine, the sockets can now be closed.  
A `ReqGoodbye` event will be sent by the client:

```json
{ "type": "ReqGoodbye" }
```

A `ResGoodbye` message will be sent by the server as an acknowledgement:

```json
{
  "type": "ResGoodbye"
}
```

Only then the network sockets will be closed by both parties.

## Reconnection
The player is allowed to connect back to the server and join the game they were previously in.  
The reconnection routine at the network level is exactly the same as a normal connection.

## Heartbeat
The server checks whether the clients are still alive using both normal messages and heartbeat messages (the timeout resets with any kind of message).  
Heartbeat messages will be sent at regular intervals.

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
Broken (unparsable) messages will be signaled with a `ErrProtocol` messsage containing information about the error itself:

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
> - Only one waiting room will be used to manage the players who join the server.
> - Players who disconnect can successively reconnect to continue the match. While a player isn't connected, the game continues skipping the player's turns.

Given those requirements, the communication at connection time has been modeled the following way.

## Choosing a nickname
After establishing a connection with the server, the client will ask the player to input a nickname of their choice. The entry will be sent to the server, and, if acceptable (unique among the registered nicknames, not empty, not already set), will be accepted. Else, the server will signal the error, restarting the process.

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
If a `ReqNewGame` is received from a client that is not allowed to request a new match, `ErrNewGame.isInvalidPlayersCount` will be set to false.

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
A `ResQuit` message will be sent to the client to notify it about the closing routine being completed.  

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


```json
{
  "type": "ResGoodbye"
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
When a request message from a client references the ID of an object that is not owned by the player, an `ErrObjectNotOwned` message will be sent by the server, detailing the erroneus ID and the object's kind.

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

The client will be sent the IDs of the leader cards they can choose from, and will send back a subset of them.  

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
After choosing the leader cards the players will be prompted to choose their starting resources, following the configuration file's settings.

The client is sent the amount of resources and the resource types the player has to choose from (see TODO).  
Every player will have to choose the resources before the match's turns can start, thereby concluding the setup phase.

Error messages will be fired in these situations:
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
    [ 1, { "Coin": 1 } ],
    [ 0, { "Shield": 1 } ]
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
During their turn, the player has to choose among three main actions to carry out:
1. Getting resources from the market
2. Buying a development card
3. Activating the production

Since the player may want to make a secondary move after the main action, the server waits for a `TurnEnd` message to switch to the next player (timeouts are used in case the player takes too long to make a move/is AFK).

## Getting resources from the market
The following needs to be specified:
1. Which row/column the player wants to take the resources from
2. For each replaceable resource, which leader to use (if applicable)
3. For each resource (its type considered after the leader's processing), which shelf to use to store it
4. What resources, among the ones taken from the market, to discard

Discarding is simply handled by specifying a lower quantity of resources to add to a shelf. This also easily matches the rule for which only the resources given by the market can be discarded.

The `Replacements` field specifies how the resource conversion should be handled. Since the player knows what type of resource the leader convert to, they can easily select them by specifying, for each type of resource they want as output, how many replaceable resources (of the available ones) to use.

Errors may arise from fitting the resources in the shelves, either by specifying the wrong shelf or by not discarding enough resources.

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
               │                   UpdateMarket │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │        UpdateResourceContainer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                   UpdateLeader │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
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
    [ 1, { "Coin": 2 } ],
    [ 3, { "Shield": 2 } ]
  ]
}
```
**ErrAction (server)**
```json
{
  "type": "ErrAction",
  "msg": "Cannot fit the resources in the shelves: no space left in shelf 3."
}
```

## Buying a development card
The following information is needed when buying a development card:
1. The row and column of the card to identify it in the grid
2. The slot to place the card into
3. For each resource that has to be paid, the shelf (or strongbox) to take it from

Possible errors include:

1. Not identifying a valid card (invalid row/col)
2. Not identifying a valid slot
3. Not satisfying placement requirements (the card's level isn't one above the level of the card it is being placed
   onto)
4. Not specifying correctly where to take the resources from/how many to take

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
               │              UpdateDevCardGrid │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqBuyDevCard (client)**
```json
{
  "type": "ReqBuyDevCard",
  "level": 1,
  "color": "Blue",
  "slot": 2,
  "resContainers": [
    [ 1, { "Coin": 2 } ],
    [ 4, { "Coin": 1 } ]
  ]
}
```
**ErrAction (server)**
```json
{
  "type": "ErrAction",
  "msg": "Cannot buy dev card in row 0 color Yellow: color Yellow does not exist."
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
╰────────────╯ │ ReqActivateProd                │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │        UpdateResourceContainer │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqActivateProd (client)**
```json
{
  "type": "ReqActivateProd",
  "productions": [
    {
      "id": 0,
      "inputBlanksRep": { "Coin": 2, "Shield": 1 },
      "outputBlanksRep": { "Shield": 1 },
      "inputContainers": [
        [ 1, { "Coin": 1 } ],
        [ 2, { "Coin": 2 } ],
        [ 0, { "Shield": 2 } ]
      ],
      "outputContainers": [
        [ 2, { "Stone": 1 } ]
      ]
    }, {
      "id": 3,
      "inputBlanksRep": { "Stone": 1 },
      "outputBlanksRep": [],
      "inputContainers": [
        [ 0, { "Stone": 2 } ]
      ],
      "outputContainers": []
    }
  ]
}
```
**ErrAction (server)**
```json
{
  "type": "ErrAction",
  "msg": "Cannot activate production 17: production 17 does not exist."
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
╰────────────╯ │ ReqTurnEnd                     │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────╮
               │                                ├─┤ exec │
               │            UpdateCurrentPlayer │ ╰──────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqTurnEnd (client)**
```json
{ "type": "ReqTurnEnd" }
```
**ResTurnEnd (server)**
```json
{ "type": "UpdateCurrentPlayer" }
```


# Secondary actions
Secondary moves can be performed as often as the player wants and at any point of the turn. They are:

1. Swapping the content of the player's shelves
2. Activating/discarding a leader card

## Swapping two shelves
During their turn, the player can decide to reorder their warehouse.

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
               │        UpdateResourceContainer │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
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
**ErrAction (server)**
```json
{
  "type": "ErrAction",
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
╰────────────╯ │ ReqActivateLeader              │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │                   UpdateLeader │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqActivateLeader (client)**
```json
{
  "type": "ReqActivateLeader",
  "leader": 0
}
```
**ErrAction (server)**
```json
{
  "type": "ErrAction",
  "msg": "Leader 0 cannot be activated: leader does not exist."
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
╰────────────╯ │ ReqDiscardLeader               │
               ┝━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━►│
               │                                │ ╭──────────────────╮
               │                                ├─┤ try exec / check │
               │                   UpdateLeader │ ╰──────────────────╯
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
               │                      ErrAction │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**ReqDiscardLeader (client)**
```json
{
  "type": "ReqDiscardLeader",
  "leader": 0
}
```
**ErrAction (server)**
```json
{
  "type": "ErrAction",
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



## Game start
As the game starts, the server notifies all players of the event.

### Caching
The server sends the game's state to be cached by the clients. Caching parts of the game's state allows the clients to answer requests without the server's intervention.  
Caching allows partial checks to be preemptively (but not exclusively) done client side: if the player specifies an index that's out of bounds, the client is able to catch the error before sending the request to the server, reducing network and server loads and improving the game's responsiveness.

### Parameters and indices
The game's model has been parameterized to allow for flexibility. The parameters are set via
a [configuration file](../src/main/resources/config/config.json), which also contains serialized game data (e.g. cards,
resources, etc...).  
This file is available to both clients and server, which will use it to instantiate the game objects. It is therefore
extremely important for both parties to have matching ordering in the file's objects, since it will be used by the
identification system.  
The matching and ordering properties of the objects in the configuration file are used to identify game objects and
synchronize the game state at the start of the match, eliminating the need for a more complex ID system.

This implies that every ID/index specified in this document has been synchronized at game start either by being taken
from the configuration file or by being specified in the `UpdateGameStart` message.

The market's resources are placed randomly at creation, therefore needing to be synchronized: the entire market's state
needs to be sent.  
The development cards will be shuffled by the server before being placed in the grid: the field `devCardGrid` maps to
each color a list of lists (levels-deck). Each deck is a list of IDs, where each ID references a card in the
configuration file ([ 0, 2 ] means that in that deck there's the first and third card, taken with the same ordering they
appear in the configuration file). The same principle applies to the solo action tokens.

After reordering the cached objects to match the server's state, all indices sent from the server can be used to reference the correct objects.

```
 ┌────────┒                      ┌────────┒ 
 │ Client ┃                      │ Server ┃
 ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
     │                                │
     │                UpdateGameStart │
     │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
     │                                │
```
**UpdateGameStart (server)**
```json
{
  "type": "UpdateGameStart",
  "nicknames": [ "NicknameA", "NicknameB", "NicknameC" ],
  "market": {
    "grid": [
      [ "Coin", "Shield", "Coin" ],
      [ "Coin", "Shield", "Stone" ]
    ],
    "slide": "Shield"
  },
  "devCardGrid": {
    "levelsCount": 3,
    "colorsCount": 4,
    "grid": {
      "Purple": [
        [ 2, 0, 1 ],
        [ 3, 4, 5 ]
      ],
      "Green": [
        [ 8, 7, 6 ],
        [ 9, 11, 10 ]
      ]
    }
  },
  "soloActionTokens": [ 4, 3, 0, 1, 2 ]
}
```
<!-- TODO update this to reflect actual message -->

## Setup done
When every player has chosen their leader cards and starting resources, the conclusion of the setup phase will be notified to the clients.
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

## Updating the players list
Notifications about players connecting/disconnecting from a match will be sent.

When the match is waiting for players to join before its start, sending notifications allows the players who already joined to know how many empty seats are left, therefore getting a sense for how much waiting time there's left.

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

## Updating the current player

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │                UpdateCurrentPlayer │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateCurrentPlayer (server)**
```json
{
  "type": "UpdateCurrentPlayer",
  "nickname": "NicknameB"
}
```

## Updating the market

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
    "isRow": true,
    "index": 1,
    "resources": [ "Coin", "Shield", "Stone" ],
    "slideRes": "Shield"
  }
}
```

## Updating the shelves

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
  "container": {
    "id": 3,
    "content": [
      { "Coin": 3 },
      { "Shield": 1 }
    ],
    "bindingResource": null
  }
}
```

## Updating the leader cards

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │                   UpdateLeader │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateLeader (server)**
```json
{
  "type": "UpdateLeader",
  "leader": 1,
  "isActive": true,
  "isDiscarded": false
}
```

## Updating the development card grid

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
        [ 2, 0, 1 ],
        [ 3, 4, 5 ]
      ],
      "Green": [
        [ 8, 7, 6 ],
        [ 9, 11, 10 ]
      ]
    }
  }
}
```

## Updating the development card slots

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

## Updating the position on the faith track

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
  "position": 14,
  "nickname": "NicknameA"
}
```

## Updating the vatican sections

```
           ┌────────┒                      ┌────────┒ 
           │ Client ┃                      │ Server ┃
           ┕━━━┯━━━━┛                      ┕━━━━┯━━━┛
               │                                │
               │      UpdateActivatedVatSection │
               │◄━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┥
               │                                │
```
**UpdateActivatedVatSection (server)**
```json
{
  "type": "UpdateActivatedVatSection",
  "id": 66
}
```

## Sending the activated solo action token

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
  "actionToken": 6,
  "stack": [ 3, 5, 1, 2, 4, 6, 0 ]
}
```

## Notifying the last round

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
## Declaring a winner

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
  "winner": "NicknameA",
  "victoryPoints": { "NicknameA": 20, "NicknameB": 16, "NicknameC": 12 }
}
```
