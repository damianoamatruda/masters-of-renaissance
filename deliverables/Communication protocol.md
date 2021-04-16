# Communication protocol documentation

This document describes the client-server communication protocol used by the implementation of the Masters of Reneissance game written by group AM49.

All messages are encoded using the GSON library and follow therefore the JSON specification, language-wise.  
Every message and the data it contains is mapped to a class (thanks to GSON's transparent serialization).  
All messages shown in this page hold example values, to show the messages' structure.

The communication session can be divided into the connection phase and the game phase.  
The game phase will be sectioned into player setup phase and turn phase for clarity.


&nbsp;
## Connection phase
---
The summary of the requirements' document given by the professors follows:

> - On player connection: if there is no game in its starting phase (not enough players to start), a new one is created, else the player is automatically added to the half-full game currently being filled.
> - The player starting a new game chooses how many players have to join before the game can start.
> - The game starts as soon as the specified number of players (given by the first player to join) is reached.

The following specification for the additional feature "Multiple Games" is included in the communication protocol:
> Only one waiting room will be used to manage the players who join the server.

Given those requirements, it has been decided to model the communication the following way:

&nbsp;
### Connecting/choosing a nickname 

The player, when starting the client in multiplayer mode, will be asked to input a nickname of their choice. The entry will be sent to the server, and, if unique among the connected players, will be accepted as a connection attempt. Else, the player will be notified of the need to change it, restarting the process.

```
+---------+                      +---------+ 
| Client  |                      | Server  |
+---------+                      +---------+
     |                                |
     |                 send_nickname  |
     | -----------------------------> |
     |                                | /-----------------------\
     |                                |-| check nickname unique |
     |                                | \-----------------------/
     |  nickname_ack                  |
     | <----------------------------- |
     |                                |
```
**send_nickname (client)**  
```json
{ nickname: "Name" }
```

**nickname_ack (server)**  
```json
{ accepted: true, isFirst: false }
```

&nbsp;
### Choosing the number of players

Follows the event in which a player is the first of the game, and has to choose the number of players the game will accept.

```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |             players_count_res  |
               | -----------------------------> |
               |                                |
```
**players_count_res (client)**  
```json
{ count: 1 }
```

&nbsp;
### Game start

As the game starts, the server braodcasts the event to all players.

```
+---------+                      +---------+ 
| Client  |                      | Server  |
+---------+                      +---------+
     |                                |
     |  game_started                  |
     | <----------------------------- |
     |                                |
```
**game_started (server)**  
```json
{ type: "GameStart" }
```


&nbsp;
## Game phase - Player setup
---
When the game starts, the server instantiates its internal model. To set up the player objects, the clients will be asked for choices, since the players following the first are entitled to receive bonus resources and faith points.

The player setup phase requires the players to choose leadercards and resources.

&nbsp;
### Choosing leader cards

The player is sent a portion of the deck of leader cards, from those they can choose which to keep and which to discard. The number of cards sent and chosen is set with parameters server-side.

```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  leader_offer                  |
               | <----------------------------- |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |                 leader_choice  |
               | -----------------------------> |
               |                                |
```
**leader_offer (server)**  
```json
{ leaders: <list of leader cards> }
```

**leader_choice (client)**  
```json
{ choice: <list of leader cards> }
```

&nbsp;
### Choosing starting resources

The players who haven't been given the inkwell have to choose their bonus starting resources.  
The server will notify the player of the event, signaling the amount of resources the player can choose and which resources they can choose from.  
The client will respond by specifying the resources and the respective amounts.

```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  resources_offer               |
               | <----------------------------- |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |              resources_choice  |
               | -----------------------------> |
               |                                |
```
**resources_offer (server)**  
```json
{ count: 1, available_res: <list of resource types> }
```

**resources_choice (client)**  
```json
{ choice: { coin: 1, shield: 1 } }
```


&nbsp;
## Game phase - Turns
---
After all players have gone through the setup phase, the server will start the turn loop.

The player has to choose among three actions to carry out during their turn:
1. Getting resources from the market
2. Buying a development card
3. Activating the production

That said, there are many other commands the player can issue during their turn, showinf the game's state, for example.

&nbsp;
### Show the market

```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |                   show_market  |
               | -----------------------------> |
               |                                |
               |  market_view                   |
               | <----------------------------- |
```
**show_market (client)**  
```json
{ type: "ShowMarket" }
```

**market_view (server)**  
```json
{ view: {
  resources: [[ "coin", "shield" ], [ "coin", "shield" ]],
  colCount: 2
}}
```

&nbsp;
### Show the player's shelves

```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |                  show_shelves  |
               | -----------------------------> |
               |                                |
               |  shelves_view                  |
               | <----------------------------- |
```
**show_shelves (client)**  
```json
{ choice: [ 0, 2, 3 ] }
```

**shelves_view (server)**  
```json
{ view: <list of shelf> }
```

&nbsp;
### Show the player's leader cards

```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |                  show_leaders  |
               | -----------------------------> |
               |                                |
               |  leaders_view                  |
               | <----------------------------- |
```
**show_leaders (client)**  
```json
{ choice: [ 0, 2 ] }
```

**leaders_view (server)**  
```json
{ view: <list of leader card> }
```

&nbsp;
### Getting resources from the market

In order to decide whether to carry out the action, the player can ask to be shown the market's status and their shelves (warehouse's/depot leaders', so they can see whether the taken resources can be stored).  
Moreover, the resources taken by the player may include a replaceable type, which will be processed by the server depending on the player's active (and chosen, on a per-resource basis) leader cards. Therefore, the player can also ask the server to be shown their leader cards.

To get the resources, the player needs to specify:
1. Which row/column they want to take the resources from
2. For each replaceable resource, which leader to use
3. For each resource (after the leaders' processing), which shelf to use for its storage
4. What resources, among the ones taken from the market, to discard

```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                | 
\------------/ |                                |
               |                get_market_req  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  get_market_resp               |
               | <----------------------------- |
               |                                |
               |  leader_choice_err             |
               | <----------------------------- |
               |                                |
               |  shelves_choice_err            |
               | <----------------------------- |
```
**get_market_req (client)**  
```json
{
  index_choice: 0,
  isRow: true,
  discard_choice: [ 2 ],
  leaders_choice: [ 0, 0, 1 ],
  shelf_choice: {
    shelf_index: 1,
    resources: [{
      type: "coin",
      amount: 2
    }, {
      type: "shield",
      amount: 1
    }]
  }
}
```
**get_market_resp (server)**  
```json
{
  shelves_view: <shelves_view JSON>,
  market_view: <market_view JSON>
}
```
**leader_choice_err (server)**  
```json
{
  type: "IllegalLeaderChoice",
  msg: "The operation could not be completed because..."
}
```
**shelves_choice_err (server)**  
```json
{
  type: "IllegalShelfChoice",
  msg: "The operation could not be completed because..."
}
```



&nbsp;

> The server manages the players' turns as per the game's rules. The server must handle a player disconnecting or leaving the game. If there are no players left the game will terminate and all players have to be notified.