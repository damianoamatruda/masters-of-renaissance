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
    1. [State messages](#state-messages)
        1. [Show the market](#show-the-market)

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
     |                  req_nickname  |
     | -----------------------------> |
     |                                | /-----------------------\
     |                                |-| check nickname unique |
     |                                | \-----------------------/
     |  res_nickname                  |
     | <----------------------------- |
     |                                |
     |  err_nickname                  |
     | <----------------------------- |
     |                                |
```
**req_nickname (client)**  
```json
{
  "type": "req_nickname",
  "nickname": "Name"
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
  "msg": "username already taken"
}
```

## Choosing the number of players
When a player is chosen by the server as the first of a new game, they have to decide the number of players required to start it.
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |             req_players_count  |
               | -----------------------------> |
               |                                | /-------------\
               |                                |-| try setting |
               |                                | \-------------/
               |  res_players_count             |
               | <----------------------------- |
               |                                |
               |  err_players_count             |
               | <----------------------------- |
               |                                |
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
  "msg": "the number of players has been set to 4"
}
```
**err_players_count (server)**
```json
{
  "type": "err_players_count",
  "msg": "illegal number of players: 0"
}
```

## Game start
As the game starts, the server notifies all players of the event.
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
When the game starts, the server instantiates its internal model. To set up the player objects, the clients will be asked for choices. Those include which leader cards to keep and resources, since the players following the first are entitled to receive bonus resources and faith points.

## Choosing leader cards
The players have to decide manually what leader cards they want to own.  
Each player is sent a portion of the deck of leader cards, from which they can choose the cards to keep and discard. The number of cards to be sent and chosen is set by parameters server-side.

The client will be sent the IDs of the leader cards they can choose from, and will send back a subset of them.  
If the choice is wrong, the client will be notified of the issue. Else, the server will echo back the chosen IDs to confirm that the process succeeded.
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  offer_leader                  |
               | <----------------------------- |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |             req_leader_choice  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  res_leader_choice             |
               | <----------------------------- |
               |                                |
               |  err_leader_choice             |
               | <----------------------------- |
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
  "choice": [ 3, 15 ]
}
```
**res_leader_choice (server)**  
```json
{
  "type": "res_leader_choice",
  "choice": [ 3, 15 ]
}
```
**err_leader_choice (server)**  
```json
{
  "type": "err_leader_choice",
  "msg": "invalid leader cards chosen: 3, 25"
}
```

## Choosing starting resources
The players who haven't been given the inkwell have to choose their bonus starting resources.  
The server will notify the player of the event, signaling the amount of resources the player can choose and which resource types they can choose from.  
The client will respond by specifying the resource types and the respective amounts.
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  offer_resources               |
               | <----------------------------- |
/------------\ |                                |
| user input |-|                                |
\------------/ |                                |
               |          req_resources_choice  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  res_resources_choice          |
               | <----------------------------- |
               |                                |
               |  err_resources_choice          |
               | <----------------------------- |

```
**offer_resources (server)**  
```json
{
  "type": "offer_resources",
  "count": 1,
  "res_type": [ "Coin", "Shield" ]
}
```
**req_resources_choice (client)**  
```json
{
  "type": "req_resources_choice",
  "choice": {
    "coin": 1,
    "shield": 1
  }
}
```
**err_resources_choice (client)**  
```json
{
  "type": "err_resources_choice",
  "msg": "wrong starting resource choice: cannot choose 2 resources"
}
```

# Game phase - Turns
After all players have gone through the setup phase, the server will send the initial game state to the clients' local caches (see the [State messages](#state-messages) section) and then start the turn loop.

The messages will be differentiated into three categories:
1. State messages, which get the object's state from the server
2. Secondary moves, which can be repeated within the player's turn
3. Main moves, of which the player has to choose only one during the turn

# State messages
These messages are used to show the model's status to the player.

The server automatically sends incremental updates to the player whenever an object changes.  
With that said, in order to carry out a choice-heavy move, the player may want to see objects that were updated long before. On the GUI side, the player can glance at the entire board, but when playing from the CLI finding the last update of an object would be unoptimal.  
In those cases the player can request a fresh view of the objects via commands, which, instead of reaching the server, will be served by the local cache. This allows for improved responsiveness and cuts back on the amount of data transferred, simplifying somewhat the communication protocol as well.  
This solution allows every player to prepare their moves before their turn comes, speeding up the gameplay and improving the experience.

## Show the market
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  view_market                   |
               | <----------------------------- |
```
**view_market (server)**  
```json
{
  "type": "view_market",
  "view": [
    [ "coin", "shield" ],
    [ "shield", "stone" ]
  ]
}
```
During their turn, the player has to choose among three main actions to carry out:
1. Getting resources from the market
2. Buying a development card
3. Activating the production

In addition to these, they can choose to carry out other secondary actions. These can be performed as often as the player wants and at any point of the turn. They are:
1. Swapping the content of the warehouse's shelves (the leader cards' depots count as such too)
2. Activating/discarding a leader card

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
