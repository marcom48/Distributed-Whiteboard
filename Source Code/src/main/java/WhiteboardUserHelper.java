/*
COMP90015 Assignment 2
Marco Marasco 834482

Class for user helper for manager of whiteboard server.

 */

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class WhiteboardUserHelper {
    private Set<IWhiteboardUser> users;
    private WhiteboardManager server;


    /**
     * Constructor.
     * @param server server that instantiates helper.
     */
    public WhiteboardUserHelper(WhiteboardManager server){


        // Utilise a concurrency tolerant list.
        this.users = Collections.newSetFromMap(new ConcurrentHashMap<IWhiteboardUser, Boolean>());

        this.server = server;

        // Begin sentinel to monitor user connectivity.
        new Thread(new UserSentinel(this)).start();
    }

    /**
     * Method returns the list of connected users.
     * @return List of users.
     */
    public Set<IWhiteboardUser> getUsers(){
        return this.users;
    }

    /***
     * Add a user to list of connected user.
     * @param user User to add.
     */
    public void addUser(IWhiteboardUser user){
        this.users.add(user);

        // Inform other users of new user.
        this.server.userRefreshGlobal();
    }


    /***
     * Get the number of users.
     * @return user count.
     */
    public boolean hasUsers(){
        return !this.users.isEmpty();
    }

    /***
     * Removes a given IWhiteboardClient from the client manager
     * @param client
     */
    public void removeClient(IWhiteboardUser client){
        this.users.remove(client);
        this.server.userRefreshGlobal();
    }

    /**
     * Method returns iterator for users.
     * @return Iterator object.
     */
    public Iterator<IWhiteboardUser> iterator(){
        return users.iterator();
    }



}
