# Standard for Socket communication
A list of strings to define a standard for socket communication in our Sagrada game implementation.

>_**Please Note** : bold words are used to point out parameters, underlined ones indicate formatting oriented keywords._

### Login Handling
* Server requests a username from client
	
	`login`
		
* Client sends chosen username
	
	`username `_`<space>`_**`username`**
* Login succesful

	`logged`
* Login Unsuccessful (ServerFull) 
   
   `notLogged_ServerFull`
    
* Login Unsuccessful (usernameNotAvailable)

    `notLogged_usernameNotAvailable`


### Match Initialization Handling


* Server sends 2 grids (double-sided ---> 4 technically)
 
   `sendGridsToUser`_`<space>`_**`json_Grid`**
   
  >_**`Please Note`**`: json_Grid is a basic representation, in json format, of the grid itself.`_
 
 * Client sends back an integer associated with the chosen grid
 
    `chooseGrid`_`<space>`_**`integer`**
    
* Server sends Favor Tokens, according to the difficulty of the chosen grid

   `sendsTokens`_`<space>`_**`integer`**

* Server sends 3 Public Objective Cards to each client

  `generatePublicObjCards`_`<space>`_**`json_PublObjCards`**
  
 > _**`Please Note`**`: these 3 cards are the same for each client. `_

