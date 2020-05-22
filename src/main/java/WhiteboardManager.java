/*
COMP90015 Assignment 2
Marco Marasco 834482

Class for manager of a whiteboard server.

 */

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class WhiteboardManager extends UnicastRemoteObject implements IWhiteboardManager, Serializable {

    // Helper object to interact with users.
    private WhiteboardUserHelper userHelper;
    private ArrayList<IWhiteboardShape> shapes = new ArrayList<IWhiteboardShape>();
    private ArrayList<String> clients = new ArrayList<>();
    private String currFile = "";
    private JFrame jframe;
    private JList clientList;
    private JPanel panel;
    private JPanel titlePanel;
    private JButton saveButton;
    private JButton loadButton;
    private JButton removeButton;
    private JButton saveAsButton;
    private JToolBar toolBar;
    private JList testList;
    private Boolean saved = true;
    private String name;

    /**
     * Constructor.
     *
     * @param name Username for server manager.
     * @throws RemoteException
     * @throws ServerNotActiveException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    public WhiteboardManager(String name) throws RemoteException, ServerNotActiveException, NotBoundException, MalformedURLException {

        startUp();

        // Set server username/
        this.name = name;
        this.userHelper = new WhiteboardUserHelper(this);

    }

    /**
     * Method requests manager to create a new or load a whiteboard.
     *
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     * @throws ServerNotActiveException
     */
    public void startUp() throws RemoteException, NotBoundException, MalformedURLException, ServerNotActiveException {

        Object[] options = {"New", "Open"};
        int n = JOptionPane.showOptionDialog(this.jframe,
                "Please select how you would like to start the whiteboard",
                "Whiteboard",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (n == 1) {
            load();
        }

    }

    /**
     * Method prompts manager to choose a file to load a whiteboard.
     *
     * @return Filename to open.
     */
    private String chooseFile() {

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        int returnValue = jfc.showOpenDialog(null);


        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }

        return "";
    }

    /**
     * Method used to prompt manager to save their changes.
     */
    public void saveChanges() {

        // Only prompt if changes have been made.
        if (!this.saved) {
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this.jframe,
                    "Would you like to save your changes?",
                    "Whiteboard",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (n == 0) {
                save();
            }
        }
    }

    /**
     * Method used to create a new whiteboard.
     */
    public void newBoard() {

        // Prompt to save changes if any.
        saveChanges();
        this.shapes = new ArrayList<IWhiteboardShape>();
        globalShapeRefresh();
    }

    /**
     * Method used to load a whiteboard.
     */
    public void load() {

        // Prompt to save changes if any.
        saveChanges();

        ArrayList<IWhiteboardShape> arraylist = new ArrayList<IWhiteboardShape>();
        try {
            this.currFile = chooseFile();
            if (currFile.equals("")) {
                JOptionPane.showMessageDialog(null, "Unable to open file.");
                return;
            }
            FileInputStream fis = new FileInputStream(this.currFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            arraylist = (ArrayList) ois.readObject();
            ois.close();
            fis.close();

            this.shapes = arraylist;
            this.saved = true;
            globalShapeRefresh();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Unable to open file.");
            return;
        } catch (ClassNotFoundException c) {
            JOptionPane.showMessageDialog(null, "Unable to open file.");
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to open file.");
            return;
        }
    }

    /**
     * Method used to save a whiteboard under a new file.
     */
    public void saveAs() {
        this.currFile = "";
        save();
    }

    /**
     * Method used to save a whiteboard.
     */
    public void save() {
        try {

            if (this.currFile.equals("")) {
                this.currFile = JOptionPane.showInputDialog("Please choose a filename:");
            }
            if (this.currFile == null || this.currFile.equals("")) {
                JOptionPane.showMessageDialog(null, "No filename given. File not saved.");
                return;
            }

            FileOutputStream fos = new FileOutputStream(this.currFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.shapes);
            oos.close();
            fos.close();

        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Unable to save file.");
            return;
        }
        this.saved = true;

    }

    /**
     * Method removes a client from the server.
     */
    public void removeClient() {

        String name = JOptionPane.showInputDialog("Please type username: ");
        if (name == null) return;

        if (name.equals(this.name)) {
            JOptionPane.showMessageDialog(null, "Unable to kick yourself.");
            return;
        }

        // Find user to kick.
        for (IWhiteboardUser user : userHelper.getUsers()) {
            try {
                if (user.getName().equals(name)) {
                    user.kick("You have been kicked from the whiteboard.");
                    return;
                }

            } catch (Exception e) {
                continue;
            }
        }
        JOptionPane.showMessageDialog(null, "Unable to kick " + name + ". Please confirm they username is valid.");
    }

    /**
     * Method used to notify users of closed server.
     *
     * @throws RemoteException
     */
    public void close() throws RemoteException {

        // Prompt to save changes if any.
        saveChanges();

        for (IWhiteboardUser user : userHelper.getUsers()) {
            try {
                if (user.getName().equals(this.name)) continue;
                user.kick("The server manager has shut down the whiteboard.");
            } catch (Exception e) {
                continue;
            }
        }
        System.exit(0);
    }

    /**
     * Method is used by user to request to join the whiteboard.
     *
     * @param user User requesting to join.
     * @return Result of request.
     * @throws IllegalStateException
     * @throws RemoteException
     */
    public Result requestRegister(IWhiteboardUser user) throws IllegalStateException, RemoteException {

        // No duplicate usernames.
        if (getUserNameList().contains(user.getName())) {
            return Result.DUPLICATE;
        }

        // Accept self.
        if (user.getName().equals(this.name)) {
            return Result.ACCEPT;
        }

        int n = 0;
        Object[] options = {"Accept", "Reject"};
        n = JOptionPane.showOptionDialog(this.jframe,
                "Would you like to accept new user " + user.getName() + "?",
                "New User",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (n != 0) {
            return Result.REJECT;
        }


        return Result.ACCEPT;
    }

    /**
     * Called to register a user to the server.
     *
     * @param user User to register.
     * @return Message controller for user to communicate.
     * @throws RemoteException
     */
    public IMessageController register(IWhiteboardUser user) throws IllegalStateException, RemoteException {

        this.userHelper.addUser(user);

        IMessageController userMessageController = new MessageController(user, this);

        showUsers();

        return userMessageController;
    }


    /**
     * Given an IWhiteboardClient client, and a java.awt.Shape shape, create an IWhiteboardItem, store it, and
     * distribute it to all clients.
     *
     * @param source The source client
     * @param shape The shape to be distributed
     * @throws RemoteException
     */
    public void addShape(IWhiteboardUser source, Shape shape, Color colour, Boolean filled) throws RemoteException {
        this.saved = false;
        IWhiteboardShape shapeItem = new WhiteboardShape(source, shape, colour, filled);
        this.shapes.add(shapeItem);
        for (IWhiteboardUser user : this.userHelper.getUsers()) {
            user.receiveShape(shapeItem);
        }
    }


    /**
     * Method used to to clear whiteboard.
     *
     * @throws RemoteException
     */
    public void clearAllShapes() {
        this.shapes = new ArrayList<IWhiteboardShape>();
        globalShapeRefresh();
    }

    /**
     * Method requests users to refresh their list of shapes.
     */
    private void globalShapeRefresh() {

        // No client manager on startup
        if (userHelper == null) return;

        for (IWhiteboardUser user : userHelper.getUsers()) {
            try {
                user.refreshShapes();
            } catch (RemoteException err) {
            }
        }
    }

    /**
     * Method used to request all users update their current user list.
     *
     * @throws RemoteException
     */
    public void userRefreshGlobal() {

        try {
            showUsers();
        } catch (RemoteException e) {
        }

        for (IWhiteboardUser user : userHelper.getUsers()) {
            try {
                user.refreshUsernames();
            } catch (RemoteException err) {
            }
        }
    }

    /**
     * Returns all shapes associated with the server
     * @return ArrayList of IWhiteboardItems on the server.
     */
    public ArrayList<IWhiteboardShape> getShapes() {
        return this.shapes;
    }

    /**
     * Method called by user to get full list of current users.
     *
     * @return List of usernames.
     * @throws RemoteException
     */
    public ArrayList<String> getUserNameList() throws RemoteException {
        ArrayList<String> clientNames = new ArrayList<String>();
        for (IWhiteboardUser client : this.userHelper.getUsers()) {
            clientNames.add(client.getName());
        }

        return clientNames;
    }

    /**
     * Refresh client list.
     * @throws RemoteException
     */
    public void showUsers() throws RemoteException {

        DefaultComboBoxModel userModel = new DefaultComboBoxModel();

        for (String name : getUserNameList()) {
            userModel.addElement(name);
        }
        if (this.clientList != null)
            this.clientList.setModel(userModel);

    }

}
