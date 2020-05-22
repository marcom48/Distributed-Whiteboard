/*
COMP90015 Assignment 2
Marco Marasco 834482

Class for user in whiteboard server.

 */

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class WhiteboardUser extends UnicastRemoteObject implements IWhiteboardUser, Remote {

    private IWhiteboardManager server;
    private IMessageController messageController;
    private String name = "";
    private WhiteboardGUI gui = null;




    public static void main(String args[]) throws RemoteException, MalformedURLException, NotBoundException, ServerNotActiveException {


        if (args.length != 2) {
            JOptionPane.showMessageDialog(null, "Incorrect command line arguments.\n" +
                    "Run as \"java -jar WhiteboardUser.jar <serverIP> <serverPort>");
            System.exit(0);

        }

        String ip = args[0];
        int port = -1;

        // Assert correct port number.
        try{
            port = Integer.parseInt(args[1]);
            Utils.portCheck(port);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Invalid port number '" + port +"'.");
            System.exit(0);
        }


        String name = JOptionPane.showInputDialog("Please choose your username: ");

        if (name == null || name.equals("")) {
            JOptionPane.showMessageDialog(null, "No username given. Application shutting down.");
            System.exit(0);
        }
        try {

            // Connect to server.
            Registry registry = LocateRegistry.getRegistry(ip, port);
            IWhiteboardManager server = (IWhiteboardManager) registry.lookup("Server");
            IWhiteboardUser client = new WhiteboardUser(server, name);

            // Run GUI in separate thread.
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        WhiteboardGUI gui = new WhiteboardGUI(client);
                    } catch (RemoteException e) {
                        JOptionPane.showMessageDialog(null, "Unable to start GUI.");
                        System.exit(0);
                    } catch (Exception e){
                        JOptionPane.showMessageDialog(null, "Unable to start GUI.");
                        System.exit(0);
                    }
                }

            });
            t.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to the server.");

        }

    }

    /**
     * Constructor.
     * @param server Server to connect to.
     * @param name Username.
     * @throws RemoteException
     * @throws ServerNotActiveException
     */
    public WhiteboardUser(IWhiteboardManager server, String name) throws RemoteException, ServerNotActiveException {

        this.name = name;

        if (this.name == null || this.name.equals("")) {
            Random rand = new Random(this.name.hashCode());
            this.name = "user-" + rand.nextInt(1000);

        }


        try {
            Result response = server.requestRegister(this);
            switch (response) {
                case REJECT:

                    JOptionPane.showMessageDialog(null, "The whiteboard manager rejected your join request. Application shutting down.");
                    System.exit(0);
                    break;

                case DUPLICATE:
                    JOptionPane.showMessageDialog(null, "The username " + this.name + " is taken. Application shutting down.");
                    System.exit(0);
                    break;
                default:
                    this.messageController = server.register(this);
                    break;

            }

        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to the server.");
            System.exit(0);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Unable to connect to the server.");
            System.exit(0);
        }

    }


    /**
     * Getter for user's message controller.
     *
     * @return Message contoller instance.
     * @throws RemoteException
     */
    public IMessageController getMessageController() {
        return this.messageController;
    }

    /**
     * Method used to request username.
     *
     * @return Username.
     * @throws RemoteException
     */
    public String getName() {

        return this.name;

    }


    /**
     * Method used to add a shape to the whiteboard.
     *
     * @param shape Whiteboard shape to add.
     * @throws RemoteException
     */
    public void receiveShape(IWhiteboardShape shape) throws RemoteException {

        if (this.gui != null) this.gui.receiveShape(shape);
    }

    /**
     * Method used to refresh shapes in whiteboard.
     *
     * @throws RemoteException
     */
    public void refreshShapes() throws RemoteException {

        if (this.gui != null) this.gui.resyncShapes();
    }

    /**
     * Method used to refresh username list.
     *
     * @throws RemoteException
     */
    public void refreshUsernames() throws RemoteException {
        if (this.gui != null) this.gui.updateUserList(this.getMessageController().getUsernames());
    }

    /**
     * Method used to kick a user from the server.
     *
     * @param message Message from server.
     * @throws RemoteException
     */
    public void kick(String message) throws RemoteException {
        Thread t = new Thread(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
                System.exit(0);
            }

        });
        t.start();

    }

    /**
     * Method used to set the GUI for a user.
     *
     * @param gui GUI to set.
     * @throws RemoteException
     */
    public void setGUI(WhiteboardGUI gui) throws RemoteException {
        this.gui = gui;
    }


    /**
     * Method used to test if client is still connected.
     *
     * @return Dummy value to indicate alive.
     * @throws RemoteException
     */
    public Boolean pingUser() {
        return true;
    }

}
