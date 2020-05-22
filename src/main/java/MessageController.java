import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class MessageController extends UnicastRemoteObject implements IMessageController, Serializable {


    private IWhiteboardUser user;
    private IWhiteboardManager server;

    /**
     * Constructor method.
     *
     * @param user User for message controller.
     * @param server Whiteboard server.
     * @throws RemoteException
     */
    public MessageController(IWhiteboardUser user, WhiteboardManager server) throws RemoteException {
        this.user = user;
        this.server = server;
    }

    /**
     * Method sends a shape to be added to the collaborative whiteboard server.
     *
     * @param shape  Shape to add.
     * @param colour Colour of shape.
     * @param filled Boolean value if shape is filled.
     * @throws RemoteException
     */
    public void addShape(Shape shape, Color colour, Boolean filled) throws RemoteException {
        server.addShape(user, shape, colour, filled);
    }

    /**
     * Method requests current list of shapes for board from server.
     *
     * @return Array list of shapes from server.
     * @throws RemoteException
     */
    public ArrayList<IWhiteboardShape> getShapes() throws RemoteException {
        return server.getShapes();
    }

    /**
     * Method requests list of usernames of current users connected to whiteboard.
     *
     * @return Array list of usernames from server.
     * @throws RemoteException
     */
    public ArrayList<String> getUsernames() throws RemoteException {
        return server.getUserNameList();
    }

    /**
     * Method requests the server to clear the whiteboard.
     *
     * @throws RemoteException
     */
    public void clearWhiteboard() throws RemoteException {
        this.server.clearAllShapes();
    }

}
