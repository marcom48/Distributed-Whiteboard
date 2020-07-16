/*
COMP90015 Assignment 2
Marco Marasco 834482

Interface declares methods required for RMI messaging.

 */

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface IMessageController extends Remote {
    /**
     * Method sends a shape to be added to the collaborative whiteboard server.
     *
     * @param shape  Shape to add.
     * @param colour Colour of shape.
     * @param filled Boolean value if shape is filled.
     * @throws RemoteException
     */
    public void addShape(Shape shape, Color colour, Boolean filled) throws RemoteException;

    /**
     * Method requests current list of shapes for board from server.
     *
     * @return Array list of shapes from server.
     * @throws RemoteException
     */
    public ArrayList<IWhiteboardShape> getShapes() throws RemoteException;

    /**
     * Method requests list of usernames of current users connected to whiteboard.
     *
     * @return Array list of usernames from server.
     * @throws RemoteException
     */
    public ArrayList<String> getUsernames() throws RemoteException;

    /**
     * Method requests the server to clear the whiteboard.
     *
     * @throws RemoteException
     */
    public void clearWhiteboard() throws RemoteException;


}
