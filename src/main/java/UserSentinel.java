/*
COMP90015 Assignment 2
Marco Marasco 834482

Class acts as sentinel for user connectivity.

 */

import java.rmi.RemoteException;
import java.util.Iterator;


public class UserSentinel implements Runnable{

    private WhiteboardUserHelper manager;

    public UserSentinel(WhiteboardUserHelper manager){
        this.manager = manager;
    }

    /**
     * Method checks all users each second.
     */
    public void run(){

        while(true) {
            if(manager.hasUsers()) {

                for (Iterator<IWhiteboardUser> iterator = this.manager.iterator(); iterator.hasNext();) {

                    IWhiteboardUser user = iterator.next();

                    try {
                        // Check on user.
                        user.pingUser();
                    } catch (RemoteException e) {

                        // User has disconnected.
                        manager.removeClient(user);
                    }
                }
            }else{
                continue;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }
}