package it.polimi.ingsw.netPart_container;

import it.polimi.ingsw.Client_side_container.ClientRemoteInterface;

import java.rmi.*;

public interface ServerRemoteInterface extends Remote {
     void login(ClientRemoteInterface client);
}
