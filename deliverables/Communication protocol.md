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
        1. [Updating the market](#updating-the-market)
        2. [Updating the player's shelves](#updating-the-player's-shelves)
        3. [Updating the player's leader cards](#updating-the-player's-leader-cards)
        4. [Updating the development card grid](#updating-the-development-card-grid)
        5. [Updating the player's development card slots](#updating-the-player's-development-card-slots)
        6. [Updating the player's position on the faith track](#updating-the-player's-position-on-the-faith-track)
        7. [Sending the activated solo action token](#sending-the-activated-solo-action-token)
    2. [Secondary actions](#secondary-actions)
        1. [Swapping two shelves' content](#swapping-two-shelves'-content)
        2. [Leader Actions](#leader-actions)
    3. [Main actions](#main-actions)
        1. [Getting resources from the market](#getting-resources-from-the-market)

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
1. State messages, which update the local caches' state to match the server's
2. Secondary moves, which can be repeated within the player's turn
3. Main moves, of which the player has to choose only one during the turn

# State messages
These messages are used to update the clients' caches so that the data is syncronized with the server's.

The server automatically sends incremental updates to the player whenever an object changes.  
With that said, in order to carry out a choice-heavy move, the player may want to see objects that were updated long before. On the GUI side, the player can glance at the entire board, but when playing from the CLI finding the last update of an object would be unoptimal.  
In those cases the player can request a fresh view of the objects via commands, which, instead of reaching the server, will be served by the local cache. This allows for improved responsiveness and cuts back on the amount of data transferred, simplifying somewhat the communication protocol as well.  
This solution allows every player to prepare their moves before their turn comes, speeding up the gameplay and improving the experience.

Indices reference the data given in [game start](#game-start).

## Updating the market
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  update_market                 |
               | <----------------------------- |
```
**update_market (server)**  
```json
{
  "type": "update_market",
  "update": {
    "isRow": true,
    "index": 1,
    "resources": [ "Coin", "Shield", "Stone" ],
    "slide_res": "Shield"
  }
}
```

## Updating the player's shelves
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  update_shelves                |
               | <----------------------------- |
```
**update_shelves (server)**  
```json
{
  "type": "update_shelves",
  "update": [
    {
      "index": 0,
      "bound_res": "Coin",
      "amount": 1,
    }, {
      "index": 1,
      "bound_res": null,
      "amount": 0,
    }
  ]
}
```

## Updating the player's leader cards
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  update_leaders                |
               | <----------------------------- |
```
**update_leaders (server)**  
```json
{
  "type": "update_leaders",
  "update": {
    "index": 1,
    "isActive": true
  }
}
```

## Updating the development card grid
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  update_dev_grid               |
               | <----------------------------- |
```
**update_dev_grid (server)**  
```json
{
  "type": "update_dev_grid",
  "update": {
    "row_index": 1,
    "col_index": 2,
    "card_index": 4
  }
}
```

## Updating the player's development card slots
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  update_dev_card_slot          |
               | <----------------------------- |
```
**update_dev_card_slot (server)**  
```json
{
  "type": "update_dev_card_slot",
  "update": {
    "slot_index": 0,
    "card_index": 7
  }
}
```

## Updating the player's position on the faith track
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  update_faith_track            |
               | <----------------------------- |
```
**update_faith_track (server)**  
```json
{
  "type": "update_faith_track",
  "update": {
    "position": 14
  }
}
```

## Sending the activated solo action token
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
               |  update_solo_token             |
               | <----------------------------- |
```
**update_solo_token (server)**  
```json
{
  "type": "update_solo_token",
  "update": {
    "index": 6
  }
}
```


# Secondary actions
Secondary moves can be performed as often as the player wants and at any point of the turn. They are:
1. Swapping the content of the warehouse's shelves (the leader cards' depots count as such too)
2. Activating/discarding a leader card

## Swapping two shelves' content
During their turn, the player can decide to reorder the warehouse (the leader cards' depots are thought as a part of it).

This is technically only useful when taking resources from the market, as no other action refills the shelves, but it was left as an always-possible operation to improve the gameplay experience.
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                | 
\------------/ |                                |
               |              req_swap_shelves  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  update_shelves                |
               | <----------------------------- |
               |                                |
               |  err_shelf_swap                |
               | <----------------------------- |
```
**req_swap_shelves (client)**  
```json
{
  "type": "req_swap_shelves",
  "choice": [ 0, 3 ]
}
```
**shelf_swap_choice_err (server)**  
```json
{
  "type": "err_shelf_swap",
  "msg": "shelves 0 and 3 cannot be swapped: too many resources in shelf 3"
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
               |           req_activate_leader  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  update_leaders                |
               | <----------------------------- |
               |                                |
               |  err_leader_activation         |
               | <----------------------------- |
```
**req_activate_leader (client)**  
```json
{
  "type": "req_activate_leader",
  "choice": [ 0 ]
}
```
**err_leader_activation (server)**  
```json
{
  "type": "err_leader_activation",
  "msg": "leader 0 is already active"
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
               |            req_discard_leader  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  update_leaders                |
               | <----------------------------- |
               |                                |
               |  err_leader_discard            |
               | <----------------------------- |
```
**req_discard_leader (client)**  
```json
{
  "type": "req_discard_leader",
  "choice": [ 0 ]
}
```
**err_leader_discard (server)**  
```json
{
  "type": "err_leader_discard",
  "msg": "leader 0 cannot be discarded: leader 0 is active"
}
```


# Main actions
During their turn, the player has to choose among three main actions to carry out:
1. Getting resources from the market
2. Buying a development card
3. Activating the production

## Getting resources from the market
The following needs to be specified:
1. Which row/column the player wants to take the resources from
2. For each replaceable resource, which leader to use (if applicable)
3. For each resource (its type considered after the leader's processing), which shelf to use to store it
4. What resources, among the ones taken from the market, to discard

The data in the `leaders_choice` field of the request maps the leaders to be used with the resource in the chosen row/column in the same order they appear ("[0, -1, 2]" uses leader 0 for the first resource, no leader for the second and the leader 3 for the last).  
The `discard_index` field holds the indexes of the resources to be discarded as they appear in the chosen list ("3" discards the 4th resource).

It may look like possible errors may arise from choosing the wrong leader type-wise, but the server is designed to handle such case transparently.  
Therefore, the only mistakes that can be made stem from fitting the resources in the shelves, either by specifying the wrong shelf or by not discarding them appropriately, or by specifying a nonexistent leader.
```
          +---------+                      +---------+ 
          | Client  |                      | Server  |
          +---------+                      +---------+
               |                                |
/------------\ |                                |
| user input |-|                                | 
\------------/ |                                |
               |                req_get_market  |
               | -----------------------------> |
               |                                | /--------------------------\
               |                                |-| try exec / check choices |
               |                                | \--------------------------/
               |  update_market                 |
               | <----------------------------- |
               |                                |
               |  update_shelves                |
               | <----------------------------- |
               |                                |
               |  err_shelves_choice            |
               | <----------------------------- |
               |                                |
               |  err_leaders_choice            |
               | <----------------------------- |

```
**req_get_market (client)**  
```json
{
  "type": "req_get_market",
  "choice": {
    "market_index": 0,
    "isRow": true,
    "discard_index": [ 2 ],
    "leaders_choice": [ 0, 0, 1 ],
    "shelf_choice": [
      {
        "shelf_index": 1,
        "res_amount": 2
      }, {
        "shelf_index": 3,
        "res_amount": 2,
        "res_type": "Coin"
      }
    ]
  }
}
```
**err_shelves_choice (server)**  
```json
{
  "type": "err_shelves_choice",
  "msg": "cannot fit the resources in the shelves as chosen: no space left in shelf 3"
}
```
**err_leaders_choice (server)**  
```json
{
  "type": "err_leaders_choice",
  "msg": "cannot use leader 4 to convert resources: leader does not exist"
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
