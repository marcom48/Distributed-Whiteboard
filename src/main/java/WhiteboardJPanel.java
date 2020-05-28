/*
COMP90015 Assignment 2
Marco Marasco 834482

Class for actual whiteboard panel in JFrame.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.*;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class WhiteboardJPanel extends JPanel {

    public ArrayList<IWhiteboardShape> shapes;
    public Point drawStart, drawEnd;
    private IWhiteboardUser user;
    private Color currColour = Color.BLACK;
    private Color prevColour = Color.BLACK;
    private Boolean erasing = false;
    private Boolean currFilled = false;
    private Boolean prevFilled = false;
    private WhiteboardGUI hostGUI;

    // Current shape for drawing.
    private Shapes currShape = Shapes.RECTANGLE;

    // Current text for drawing.
    private String currText = "";

    /**
     * Constructor.
     *
     * @param user    User that hosts whiteboard.
     * @param shapes  Shapes to initialise whiteboard with.
     * @param hostGUI Class that contains JFrame that hosts panel.
     */
    public WhiteboardJPanel(IWhiteboardUser user, ArrayList<IWhiteboardShape> shapes, WhiteboardGUI hostGUI) {

        this.shapes = shapes;
        this.hostGUI = hostGUI;
        this.user = user;
        this.setBackground(Color.WHITE);


        // Initialise mouse listener for drawing.
        this.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                // Get mouse position when pressed.
                drawStart = new Point(e.getX(), e.getY());
                drawEnd = drawStart;
                repaint();
            }

            public void mouseReleased(MouseEvent e) {

                Shape newShape;

                // Switch for what shape to draw.
                switch (currShape) {
                    case LINE:
                        newShape = new Line2D.Double(drawStart.x, drawStart.y,
                                e.getX(), e.getY());
                        break;
                    case RECTANGLE:
                        newShape = drawRectangle(drawStart.x, drawStart.y,
                                e.getX(), e.getY());
                        break;
                    case ELLIPSE:
                        newShape = makeEllipse(drawStart.x, drawStart.y,
                                e.getX(), e.getY());
                        break;
                    case TEXT:

                        newShape = Utils.generateShapeFromText(new Font("TimesRoman", Font.BOLD, 20), currText, e.getX(), e.getY());
                        break;
                    case ROUNDRECT:
                        newShape = drawRoundRectangle(drawStart.x, drawStart.y,
                                e.getX(), e.getY());

                        break;
                    case STAR:
                        newShape = drawStar(Math.min((drawEnd.x - drawStart.x) / 2, (drawEnd.y - drawStart.y) / 2) / 2,
                                (drawStart.x + drawEnd.x) / 2,
                                (drawStart.y + drawEnd.y) / 2);
                        break;
                    default:
                        return;

                }

                try {

                    // Always fill text
                    if (currShape == Shapes.TEXT) {
                        shapes.add(new WhiteboardShape(user, newShape, currColour, true));

                        // Send shape to server.
                        user.getMessageController().addShape(newShape, currColour, true);
                    } else {

                        shapes.add(new WhiteboardShape(user, newShape, currColour, currFilled));

                        // Send shape to server.
                        user.getMessageController().addShape(newShape, currColour, currFilled);
                    }

                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "An error has occurred sendinf" +
                            "data to the server.");
                }

                drawStart = null;
                drawEnd = null;

                // Repaint board
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                drawEnd = new Point(e.getX(), e.getY());
                repaint();
            }
        });
    }


    /**
     * Method used to paint the whiteboard.
     *
     * @param g Graphics component of board.
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw shapes.
        for (int i = 0; i < this.shapes.size(); i++) {
            try {

                g2.setColor(this.shapes.get(i).getColour());
                g2.draw(this.shapes.get(i).getShape());
                if (this.shapes.get(i).getFilled()) {
                    g2.fill(this.shapes.get(i).getShape());

                }

            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(null, "Unable to draw the board.");
                // If repeated error, will cause significant interruption with messages.
                break;
            }
        }


        // Light gray box whilst drawing shape.
        if (drawStart != null && drawEnd != null) {

            // Transparent box
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 0.40f));

            g2.setPaint(Color.LIGHT_GRAY);

            Shape newShape = drawRectangle(drawStart.x, drawStart.y,
                    drawEnd.x, drawEnd.y);
            g2.draw(newShape);
        }
    }

    /**
     * Add shape to whiteboard.
     *
     * @param shape Shape to add.
     */
    public void addShape(IWhiteboardShape shape) {
        this.shapes.add(shape);
        this.repaint();
    }

    /**
     * Update list of shapes.
     *
     * @param shapes New shapes.
     */
    public void updateShapes(ArrayList<IWhiteboardShape> shapes) {
        this.shapes = shapes;
        this.repaint();
    }


    /**
     * Restores whiteboard draw settings after erase.
     */
    private void restoreSettings() {
        this.currFilled = this.prevFilled;
        this.currColour = this.prevColour;
        this.erasing = false;

    }

    /**
     * Set current shape for drawing.
     *
     * @param shape Shape to use.
     */
    public void setCurrShape(Shapes shape) {

        // Restore colour if previous state was erasing.
        if (this.erasing) {
            this.restoreSettings();
        }

        this.currShape = shape;
    }


    public void setCurrText(String text) {
        this.currText = text;
    }

    public void setFill() {
        this.currFilled = !this.currFilled;
    }

    public void setCurrColour() {
        this.currColour = JColorChooser.showDialog(null, "Pick a colour", Color.BLACK);
    }

    /**
     * Set whiteboard to erase mode.
     */
    public void eraser() {

        this.erasing = true;
        this.prevColour = this.currColour;
        this.currColour = Color.WHITE;
        this.prevFilled = this.currFilled;
        this.currFilled = Boolean.TRUE;
        this.currShape = Shapes.RECTANGLE;
    }

    /**
     * Return an ellipse shape.
     *
     * @param x1 Start x coordinate.
     * @param y1 Start y coordinate.
     * @param x2 End x coordinate.
     * @param y2 End y coordinate.
     * @return Shape to draw.
     */
    private Ellipse2D.Float makeEllipse(
            int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);

        return new Ellipse2D.Float(
                x, y, width, height);
    }

    /**
     * Return a rectangle shape.
     *
     * @param x1 Start x coordinate.
     * @param y1 Start y coordinate.
     * @param x2 End x coordinate.
     * @param y2 End y coordinate.
     * @return Shape to draw.
     */
    private Rectangle2D.Float drawRectangle(
            int x1, int y1, int x2, int y2) {
        // Get the top left hand corner for the shape
        // Math.min returns the points closest to 0

        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);

        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);

        return new Rectangle2D.Float(
                x, y, width, height);
    }

    /**
     * Return a round rectangle shape.
     *
     * @param x1 Start x coordinate.
     * @param y1 Start y coordinate.
     * @param x2 End x coordinate.
     * @param y2 End y coordinate.
     * @return Shape to draw.
     */
    private RoundRectangle2D.Double drawRoundRectangle(
            int x1, int y1, int x2, int y2) {
        // Get the top left hand corner for the shape
        // Math.min returns the points closest to 0

        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);

        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);

        return new RoundRectangle2D.Double(x, y, width, height, 50, 50);
    }

    /**
     * Method creates a star shape.
     *
     * @param innerRadius Inner radius of star.
     * @param centerX     x coordinate of star centre.
     * @param centerY     y coordinate of star centre.
     * @return Star shape to draw.
     */
    private Shape drawStar(double innerRadius, double centerX,
                           double centerY) {

        double outerRadius = innerRadius * 2.63;
        int numRays = 5;
        double startAngleRad = Math.toRadians(-18);
        Path2D path = new Path2D.Double();

        double deltaAngleRad = Math.PI / numRays;

        for (int i = 0; i < numRays * 2; i++) {
            double angleRad = startAngleRad + i * deltaAngleRad;
            double cos_a = Math.cos(angleRad);
            double sin_a = Math.sin(angleRad);
            double relX = cos_a;
            double relY = sin_a;
            if ((i & 1) == 0) {
                relX *= outerRadius;
                relY *= outerRadius;
            } else {
                relX *= innerRadius;
                relY *= innerRadius;
            }
            if (i == 0) {
                path.moveTo(centerX + relX, centerY + relY);
            } else {
                path.lineTo(centerX + relX, centerY + relY);
            }
        }
        path.closePath();
        return path;

    }


}
