/*
COMP90015 Assignment 2
Marco Marasco 834482

Interface declares methods required for RMI messaging with whiteboard server.

 */

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface IWhiteboardManager extends Remote {

    /**
     * Called to register a user to the server.
     *
     * @param user User to register.
     * @return Message controller for user to communicate.
     * @throws RemoteException
     */
    public IMessageController register(IWhiteboardUser user) throws RemoteException;

    /**
     * Method is used by user to request to join the whiteboard.
     *
     * @param user User requesting to join.
     * @return Result of request.
     * @throws IllegalStateException
     * @throws RemoteException
     */
    public Result requestRegister(IWhiteboardUser user) throws IllegalStateException, RemoteException;

    /**
     * Method called by user to get full list of current users.
     *
     * @return List of usernames.
     * @throws RemoteException
     */
    public ArrayList<String> getUserNameList() throws RemoteException;

    /**
     * Method called by user to add a shape to the whiteboard.
     *
     * @param user   User requesting to add shape.
     * @param shape  Shape to add.
     * @param colour Colour of shape.
     * @param filled Boolean if shape is filled.
     * @throws RemoteException
     */
    public void addShape(IWhiteboardUser user, Shape shape, Color colour, Boolean filled) throws RemoteException;

    /**
     * Method called by user to receive shapes in board.
     *
     * @return Shapes in board.
     * @throws RemoteException
     */
    public ArrayList<IWhiteboardShape> getShapes() throws RemoteException;

    /**
     * Method used to request all users update their current user list.
     *
     * @throws RemoteException
     */
    public void userRefreshGlobal() throws RemoteException;

    /**
     * Method used to to clear whiteboard.
     * @throws RemoteException
     */
    public void clearAllShapes() throws RemoteException;
}
