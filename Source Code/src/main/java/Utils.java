import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.GlyphVector;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;

public class Utils {

    public static final Integer PORT_MIN = 1024;
    public static final Integer PORT_MAX = 49151;
    /**
     * Method used to generate a image from a char.
     * @param font Font type to use.
     * @param ch Char
     * @param x x position of char.
     * @param y y position of char.
     * @return Shape of char.
     */
    public static Shape generateShapeFromText(Font font, char ch, int x, int y) {
        return generateShapeFromText(font, String.valueOf(ch), x, y);
    }

    /**
     * Method used to generate a image from a string.
     * @param font Font type to use.
     * @param string Char
     * @param x x position of char.
     * @param y y position of char.
     * @return Shape of char.
     */
    public static Shape generateShapeFromText(Font font, String string, int x, int y) {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        try {
            GlyphVector vect = font.createGlyphVector(g2.getFontRenderContext(), string);
            Shape shape = vect.getOutline(x, y);

            return shape;
        } finally {
            g2.dispose();
        }
    }

    /**
     * Method used as a factory of server privileged tools for whiteboard.
     * @param server
     * @return
     */
    public static ArrayList<Component> createTools(WhiteboardManager server) {

        JButton newButton = new JButton("New");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        JButton removeButton = new JButton("Remove Client");
        JButton saveAsButton = new JButton("Save as");


        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                server.newBoard();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                server.save();
            }
        });
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                server.load();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                server.removeClient();
            }
        });
        saveAsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                server.saveAs();
            }
        });

        ArrayList<Component> tools = new ArrayList<>();
        tools.add(newButton);
        tools.add(loadButton);
        tools.add(saveButton);
        tools.add(saveAsButton);
        tools.add(removeButton);


        return tools;

    }

    /**
     * Method asserts a port is valid or RMI registry.
     * @param port Port number.
     */
    public static void portCheck(int port){
        // Check valid port number.
        if (port < PORT_MIN || port > PORT_MAX) {
            JOptionPane.showMessageDialog(null, "Invalid port number. Please choose a port in range [" + Integer.toString(PORT_MIN)
                    + ", " + Integer.toString(PORT_MAX) + "].");
            System.exit(0);
        }
    }

}