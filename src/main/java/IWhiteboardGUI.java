/*
COMP90015 Assignment 2
Marco Marasco 834482

Interface declares methods required for updating a whiteboard.

 */

import java.util.ArrayList;


public interface IWhiteboardGUI {
    /**
     * Method updates current user list.
     * @param clientNames List of usernames
     */
    public void updateUserList(ArrayList<String> clientNames);

    /**
     * Method is called to receive a shape to the whiteboard.
      * @param shape Shape submitted.
     */
    public void receiveShape(IWhiteboardShape shape);

    /**
     * Method calls user message controller to request all shapes in the whiteboard.
     */
    public void resyncShapes();
}

