# Communication protocol documentation

This document describes the client-server communication protocol used by the implementation of the Masters of Reneissance game written by group AM49.

All messages are encoded using the GSON library and follow therefore the JSON specification, language-wise.  
All values shown in the messages hold example values, only to show the messages' structure.  

The communication session can be divided into connection phase and game phase.

# Errors
Three types of errors have been identified:
1. Broken (unparsable) messages
2. Illegal messages (messages sent at the wrong time)
3. Incorrect messages (messages that when executed by the server return an exception, e.g. an incorrect choice)

Messages in the third category are defined in the section they pertain to, following the message they respond to.

Messages that generate an error of the second kind will receive an answer of type `err_illegal_message`:
```json
{
  "type": "err_illegal_message",
  "msg": "wrong message type"
}
```
Only the server is able to generate this type of message.

Unparsable messages will receive an answer of type `err_unparsable_message`:
```json
{
  "type": "err_unparsable_message",
  "msg": "unparsable message"
}
```
When this type of message is received the last message will be recreated and sent again. More advanced solutions were not requested and will therefore not be taken into account to maintain simplicity and keep the focus on other features.

The error messages' content is purposefully kept synthetic and will be expanded upon by the receiver if so appropriate.

# Client-server connection
A summary of the requirements highlighting the relevant parts is reported below:
> - On player connection: the player is automatically added to the game currently being filled. If there is no game in its starting phase, a new one is created.
> - The player starting a new game chooses how many players have to join before the game can start.
> - The game starts as soon as the specified number of players (given by the first player to join) is reached.

The following specification for the additional feature "Multiple Games" is taken into account in the communication protocol's design:
> Only one waiting room will be used to manage the players who join the server.

Given those requirements, the communication has been modeled in the following way.

## Connecting/choosing a nickname
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
{
  "type": "send_nickname",
  "nickname": "Name"
}
```
**nickname_ack (server)**  
```json
{
  "type": "nickname_ack",
  "accepted": true,
  "isFirst": false
}
```

## Choosing the number of players
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
{
  "type": "players_count_res",
  "count": 1
}
```

## Game start
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
{
  "type": "game_started"
}
```

# Game phase - Player setup
When the game starts, the server instantiates its internal model. To set up the player objects, the clients will be asked for choices, since the players following the first are entitled to receive bonus resources and faith points.

The player setup phase requires the players to choose leadercards and resources.

## Choosing leader cards
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
{
  "type": "leader_offer",
  "leaders": <list of leader cards>
}
```
**leader_choice (client)**  
```json
{
  "type": "leader_choice",
  "choice": <list of leader cards>
}
```

## Choosing starting resources
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
{
  "type": "resources_offer",
  "count": 1,
  "available_res": <list of resource types>
}
```
**resources_choice (client)**  
```json
{
  "type": "resources_choice",
  "choice": {
    "coin": 1,
    "shield": 1
  }
}
```

# Game phase - Turns
After all players have gone through the setup phase, the server will start the turn loop.

The player has to choose among three actions to carry out during their turn:
1. Getting resources from the market
2. Buying a development card
3. Activating the production

That said, there are many other commands the player can issue during their turn, showinf the game's state, for example.

## Show the market
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
{
  "type": "show_market"
}
```
**market_view (server)**  
```json
{
  "type": "market_view",
  view: {
    resources: [
      [ "coin", "shield" ],
      [ "coin", "shield" ]
    ],
    colCount: 2
  }
}
```

## Show the player's shelves
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
{
  "type": "show_shelves",
  "choice": [ 0, 2, 3 ]
}
```
**shelves_view (server)**  
```json
{
  "type": "shelves_view",
  "view": <list of shelves>
}
```

## Show the player's leader cards
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
{
  "type": "show_leaders",
  "choice": [ 0, 2 ]
}
```
**leaders_view (server)**  
```json
{
  "type": "leaders_view",
  "view": <list of leader card>
}
```

## Show the development card grid
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |                 show_dev_grid  |
               | -----------------------------> |
               |                                |
               |  dev_grid_view                 |
               | <----------------------------- |
```
**show_dev_grid (client)**  
```json
{
  "type": "show_dev_grid"
}
```
**dev_grid_view (server)**  
```json
{
  "type": "leaders_view",
  "view": [
    [ ID, ID, ID, ID ],
    [ ID, ID, ID, ID ],
    [ ID, ID, ID, ID ]
  ]
}
```

## Get resources from the market
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
  "type": "get_market_req",
  "index_choice": 0,
  "isRow": true,
  "discard_choice": [ 2 ],
  "leaders_choice": [ 0, 0, 1 ],
  "shelf_choice": {
    "shelf_index": 1,
    "resources": [{
      "type": "coin",
      "amount": 2
    }, {
      "type": "shield",
      "amount": 1
    }]
  }
}
```
**get_market_resp (server)**  
```json
{
  "type": "get_market_resp",
  "shelves_view": <shelves_view JSON>,
  "market_view": <market_view JSON>
}
```
**leader_choice_err (server)**  
```json
{
  "type": "IllegalLeaderChoice",
  "msg": "The operation could not be completed because..."
}
```
**shelves_choice_err (server)**  
```json
{
  "type": "IllegalShelfChoice",
  "msg": "The operation could not be completed because..."
}
```

## Swap two shelves' content
During their turn, the player can decide to reorder the warehouse (the leader cards' depots are thought as part of it).

For this to happen, the message sent by the client has to specify the two shelves the player wants to swap. Sending more than one of this type of message will be allowed by the server during the player's turn.

```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                | 
\------------/ |                                |
               |                  swap_shelves  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  shelves_view                  |
               | <----------------------------- |
               |                                |
               |  shelf_swap_choice_err         |
               | <----------------------------- |
```
**swap_shelves (client)**  
```json
{
  "type": "swap_shelves",
  "choice": [ 0, 3 ]
}
```
**shelf_swap_choice_err (server)**  
```json
{
  "type": "shelf_swap_choice_err",
  "msg": "The operation could not be completed because..."
}
```

## Buy a development card
The following information is needed when buying a development card:
1. The row and column of the card to identify it in the grid
2. For each resource, the shelf (or strongbox) to take it from
3. The slot to place it into
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                | 
\------------/ |                                |
               |                  buy_dev_card  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  dev_card_grid_view            |
               | <----------------------------- |
               |                                |
               |  shelf_swap_choice_err         |
               | <----------------------------- |
```
**swap_shelves (client)**  
```json
{
  "type": "swap_shelves",
  "choice": [ 0, 3 ]
}
```
**shelf_swap_choice_err (server)**  
```json
{
  "type": "shelf_swap_choice_err",
  "msg": "The operation could not be completed because..."
}
```

## Activate productions
The following information is needed when activating a production:
1. What productions to activate
2. For each resource of each production, the shelf (or strongbox) to take it from/put it into
3. For each replaceable resource slot in each production, the resource to replace it with
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                | 
\------------/ |                                |
               |                 activate_prod  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  shelves_view                  |
               | <----------------------------- |
               |                                |
               |  prod_activation_err           |
               | <----------------------------- |
```
**activate_prod (client)**  
```json
{
  "type": "activate_prod",
  "productions": [ 0, 3 ],
  "inputBlanksRepl": [
    { "prodID": 0, "resources": [ "coin", "shield" ] }
  ],
  "outputBlanksRepl": [
    { "prodID": 3, "resources": [ "coin" ] }
  ],
  "inputShelves": [
    {
      "prodID": 0,
      "mappings": [
        { "res": "coin", "shelf": 1 },
        { "res": "shield", "shelf": 0 }
      ]
    }, {
      "prodID": 3,
      "mappings": [
        { "res": "shield", "shelf": 0 }
      ]
    }
  ],
  "outputShelves": [
    {
      "prodID": 0,
      "mappings": [
        { "res": "stone", "shelf": 2 }
      ]
    }
  ],
}
```
**prod_activation_err (server)**  
```json
{
  "type": "prod_activation_err",
  "msg": "The operation could not be completed because..."
}
```

## Leader actions
During their turn, in addition to one of the main three actions, a player can choose to discard or activate their leader cards (acting on one or both, performing both actions or the same action twice in the same turn).

To activate or discard a leader the server needs to know which card(s) the player wants to act on.

Leader activation:
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                | 
\------------/ |                                |
               |               activate_leader  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  leaders_view                  |
               | <----------------------------- |
               |                                |
               |  leader_activation_err         |
               | <----------------------------- |
```
**activate_leader (client)**  
```json
{
  "type": "activate_leader",
  "choice": [ 0 ]
}
```
**leader_activation_err (server)**  
```json
{
  "type": "leader_activation_err",
  "msg": "The operation could not be completed because..."
}
```

Discarding a leader:
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                | 
\------------/ |                                |
               |                discard_leader  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  leaders_view                  |
               | <----------------------------- |
               |                                |
               |  leader_discard_err            |
               | <----------------------------- |
```
**discard_leader (client)**  
```json
{
  "type": "discard_leader",
  "choice": [ 0 ]
}
```
**leader_discard_err (server)**  
```json
{
  "type": "leader_discard_err",
  "msg": "The operation could not be completed because..."
}
```

> The server manages the players' turns as per the game's rules. The server must handle a player disconnecting or leaving the game. If there are no players left the game will terminate and all players have to be notified.
