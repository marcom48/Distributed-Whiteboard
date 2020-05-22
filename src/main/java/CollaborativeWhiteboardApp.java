/*
COMP90015 Assignment 2
Marco Marasco 834482

Class starts a collaborate distributed whiteboard server.

 */




import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.ServerNotActiveException;
import java.time.Instant;


public class CollaborativeWhiteboardApp {

    public static JLabel timeLabel = new JLabel("Runtime: 00:00:00");
    private static Long startTime;

    public static void main(String args[]) {

        try {

            if (args.length != 2) {
                JOptionPane.showMessageDialog(null, "Incorrect command line arguments.\n" +
                        "Run as \"java -jar CollaborativeWhiteboardApp.jar <serverIP> <serverPort>");
                System.exit(0);

            }
            String ip = args[1];
            int port = Integer.parseInt(args[1]);

            // Check valid port number.
            Utils.portCheck(port);

            // Get server username.
            final String name = JOptionPane.showInputDialog("Please choose your name: ");
            if (name == null || name.equals("")) {
                JOptionPane.showMessageDialog(null, "No filename given. File not saved.");
                System.exit(0);
            }


            // Create server and bind to local registry.
            final WhiteboardManager manager = new WhiteboardManager(name);

            try{
                Registry reg = LocateRegistry.createRegistry(port);
                Naming.rebind("Server", manager);

            } catch (ExportException e){
                JOptionPane.showMessageDialog(null, "Port " + port + " in use.");
                System.exit(0);
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Unable to start the server.");
                System.exit(0);
            }

            startTime = Instant.now().getEpochSecond();

            // Run GUI in separate thread.
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        IWhiteboardUser user = new WhiteboardUser(manager, name);
                        WhiteboardGUI gui = new WhiteboardGUI(user);

                        gui.toolBar.add(Box.createHorizontalGlue());
                        gui.addTools(timeLabel);
                        for (Component tool : Utils.createTools(manager)) {
                            gui.addTools(tool);
                        }

                        gui.jFrame.addWindowListener(new WindowAdapter() {
                            public void windowClosing(WindowEvent e) {
                                try {
                                    manager.close();
                                    System.exit(0);
                                } catch (RemoteException ex) {
                                    System.exit(0);
                                }
                            }
                        });

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Unable to start GUI.");
                        System.exit(0);
                    }
                }

            });
            t.start();

            ActionListener updateClockAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateRuntime();
                }
            };

            Timer t2 = new Timer(1000, updateClockAction);
            t2.start();



        }



        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to start server.");
            System.exit(0);
        }
    }

    /**
     * Method updates the total runtime counter in the GUI.
     */
    private static void updateRuntime() {
        Long dur = Instant.now().getEpochSecond() - startTime;
        timeLabel.setText(String.format("Runtime: %02d:%02d:%02d", dur / 3600, (dur % 3600) / 60, (dur % 60)));
    }
}