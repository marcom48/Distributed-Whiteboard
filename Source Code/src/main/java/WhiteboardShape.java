/*
COMP90015 Assignment 2
Marco Marasco 834482

Class for shapes in whiteboard server.

 */

import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class WhiteboardShape extends UnicastRemoteObject implements IWhiteboardShape, Serializable {

    private Shape shape;
    private transient IWhiteboardUser client;
    private Color colour;
    private Boolean filled;

    /**
     * Constructor.
     * @param client Client that drew shape.
     * @param shape Shape type.
     * @param colour Shape colour.
     * @param filled True if shape is filled.
     * @throws RemoteException
     */
    public WhiteboardShape(IWhiteboardUser client, Shape shape, Color colour, Boolean filled) throws RemoteException {
        this.client = client;
        this.shape = shape;
        this.colour = colour;
        this.filled = filled;
    }


    /**
     * Method returns shape type of whiteboard shape.
     *
     * @return Shape type.
     * @throws RemoteException
     */
    public Shape getShape() {
        return this.shape;
    }

    /**
     * Method returns colour of whiteboard shape.
     *
     * @return Colour.
     * @throws RemoteException
     */
    public Color getColour() {
        return this.colour;
    }

    /**
     * Method returns if a whiteboard shape is filled.
     *
     * @return True if filled.
     * @throws RemoteException
     */
    public Boolean getFilled() {
        return this.filled;
    }
}
