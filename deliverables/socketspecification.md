# Standard for Socket communication

A list of strings to define a standard for socket communication in our Sagrada game implementation.

>_**Please Note** : bold words are used to point out parameters, underlined ones indicate formatting oriented keywords._

### Login Handling
* Client ask to server to login
	`hello`
		
* Server requests a username from client
	`login`
		
* Client sends chosen username

* *Response #1:* Login success (new player)
	`logged`
	
* *Response #2:* Login success (player reconnected)
	`reconnected`
	>**Note**: A successfull login   automatically implies the insertion in the game queue.
	
* *Response #3:* Login failure (server full) 
   `notLogged_server_full`
    
* *Response #4:* Login failure (usernamen not available)
    `notLogged_username_not_available` 
---

### Match Start Handling
* Logout request
`try_logout`

* Successfully logged out
`logged_out`

* Notify a game will start soon (sent also if you can't log out)
`launching_game`

* Notify a game is started
`game_started`
____

### Match Initialization Handling

* Client asks for his grid selection (4 grids choice standard)
`get_grids`

* Server replies with an ok message when ready to send grid selection.
 `ok`

* If Server is not ready to send the grid selection to the user, it replies
`not_ok`

* Before the stream containing the grid selection is sent, Server sends another ok message if the player did not choose a grid yet.

	* If the player has already chosen a grid, Server replies:
`grid_selected`

* Cient tells Server it has selected a grid
`set_grid`

	* Server sends an ok message if it's ready to receive the index of the chosen grid

      * Client sends the index of the chosen grid.
 
	* Server sends a disconnection message if the time the player has to select a grid is up.
`disconnected`
---

### Match Handling  
* Server prepares clients to receive the username of the player in turn.
`turn_player`
	* Server then sends the username of the player in turn.
* Server notifies all clients about the end of the game
`finished`
	* Server warns each clients about incoming points
	`points`
		* Server sends the winning player and its relative points to all players.

#### Data retrieving
* Server warns client about upcoming players' grid
`all_grid`
* Server warns client about upcoming dicepool
`dice_pool`
* Server warns client about upcoming round track
`round_track`
* Server warns client about upcoming tool cards
`tool`
* Server tells client all data was sent
`end_data`
>**Note**: After each of these notifications, mentioned data is sent.

#### Game Operations
>**Note**: When player's turn is sent by Server, it starts listening for any operation the Server means to   
* Die insertion
`insert_die`
	* Followed by the index of the dicepool's die to insert and the coordinates of the relative box.
* Use a Tool Card
`use_toolcard`
	* Followed by the index of the toolcard to use.
* End Turn
`end_turn`
	* If an operation was done already, server sends
	`already_done`
	* when the chosen operation is sent, if the operation timer is up, server sends to current player a disconnected warn.
	` disconnected`
