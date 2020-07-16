/*
COMP90015 Assignment 2
Marco Marasco 834482

Interface declares methods required for RMI messaging with whiteboard user.

 */

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IWhiteboardUser extends Remote {

    /**
     * Method used to request username.
     * @return Username.
     * @throws RemoteException
     */
    public String getName() throws RemoteException;

    /**
     * Method used to test if client is still connected.
     * @return Dummy value to indicate alive.
     * @throws RemoteException
     */
    public Boolean pingUser() throws RemoteException;

    /**
     * Getter for user's message controller.
     * @return Message contoller instance.
     * @throws RemoteException
     */
    public IMessageController getMessageController() throws RemoteException;

    /**
     * Method used to refresh shapes in whiteboard.
     * @throws RemoteException
     */
    public void refreshShapes() throws RemoteException;

    /**
     * Method used to add a shape to the whiteboard.
     * @param shape Whiteboard shape to add.
     * @throws RemoteException
     */
    public void receiveShape(IWhiteboardShape shape) throws RemoteException;

    /**
     * Method used to refresh username list.
     * @throws RemoteException
     */
    public void refreshUsernames() throws RemoteException;

    /**
     * Method used to kick a user from the server.
     * @param message Message from server.
     * @throws RemoteException
     */
    public void kick(String message) throws RemoteException;

    /**
     * Method used to set the GUI for a user.
     * @param gui GUI to set.
     * @throws RemoteException
     */
    public void setGUI(WhiteboardGUI gui) throws RemoteException;
}
