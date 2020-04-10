/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Date: Apr 3, 2020
 */

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

import javax.swing.*;

/**
 * This is the class that handles all mouse event logic. It extends from 
 * MouseAdapter rather than implements, as we don't need to override
 *  all of the functions associated with MouseAdapter. For the time being,
 *  mousePressed is the only function this class overrides.
 * @cell_ is a static variable derived from the Cell class, used for casting 
 *  and instance checking.
 * @content_ is a static variable derived from the Content class, used for
 *  casting, copying, and instance checking.
 * @tile_ is a static variable derived from the tile class, used for casting
 *  and instance checking.
 * @isSelected_ is a static boolean that is used to represent when a 
 * Content object has been selected, presumably to be transferred. 
 * This is a critical variable used in the mousePressed logic.
 * @cellParent_ is a static variable derived from the Container class, 
 * used for casting, instance checking, and parent object modification.
 * @tileParent_ is a static variable derived from the Container class, 
 * used for casting, instance checking, and parent object modification.
 *
 */
public class MouseHandler extends MouseAdapter implements Serializable{
    
   
    private static Cell cell_;
    private static Content content_;
    private static Tile tile_;
    private static boolean isSelected_ = false;
    private static Container cellParent_;
    private static Container tileParent_;
    
    public static final long serialVersionUID = 1;
   
   @Override
   public void mousePressed(MouseEvent event) {
       
       //Is left mouse button pressed?
       if (SwingUtilities.isLeftMouseButton(event)) {
           
           //When first selecting your label	   
	   if (!isSelected_) {
	       
	       //Is this actually a transferable label?
	       if (event.getComponent() instanceof Content) {
		   
		   content_ = (Content) event.getComponent();
                   content_.select();
                   isSelected_ = true;
                   
               }
	       
           } else { //Perform actions with selected label.
               
               //Did you click on a label object?
               if (event.getComponent() instanceof Content) {
                   
                 //Did you click on something non related?
        	   if ( (Content) event.getComponent() != content_) {
        	       
        	       content_.deselect();
        	       content_ = null;
        	       Toolkit.getDefaultToolkit().beep();
        	       isSelected_ = false;
        	       
                   } else { //Catch all for illegal moves.
                       
                       content_.deselect();
                       isSelected_ = false;
                       
                   }  
               }
               
             //Move your label to the game board
               else if (event.getComponent() instanceof Cell) {
                   
                 //For when you are moving your label within the game board.
        	   if (content_.getParent() instanceof Cell) {
        	       
        	        cell_= (Cell) event.getComponent();
                        cellParent_ = content_.getParent();
                        content_.deselect();
                        cell_.addLabel(content_);
                        cell_.setBorder(null);
                        ((Cell) cellParent_).setBorder(BorderFactory.createLineBorder(Color.black));
                        cellParent_.revalidate();
                        cellParent_.repaint();
                        isSelected_ = false;
                	
                   } else {
                       
                       cell_ = (Cell) event.getComponent();
                       tileParent_ = content_.getParent();
                       content_.deselect();
                       cell_.addLabel(content_);
                       cell_.setBorder(null);
                       cellParent_ = cell_.getParent();
                       cellParent_.revalidate();
                       cellParent_.repaint();
                       tileParent_.revalidate();
                       tileParent_.repaint();
                       isSelected_ = false;
                       
                   }
                    
               }
               
             //Move your label back to the column place-holders.
               else if (event.getComponent() instanceof Tile) {
        	   
                 //if you are a 4 year old who loves to put pieces where they don't go.
        	   if(content_.getParent() instanceof Tile) {
        	       
        	       tile_ = (Tile) event.getComponent();
                       tileParent_ = content_.getParent();
                       content_.deselect();
                       tile_.addLabel(content_);
                       isSelected_ = false;
                       tile_.revalidate();
                       tile_.repaint();
                       tileParent_.revalidate();
                       tileParent_.repaint();
                       
                   } else {
                       
                       tile_ = (Tile) event.getComponent();
                       tileParent_ = content_.getParent();
                       content_.deselect();
                       tile_.addLabel(content_);
                       isSelected_ = false;
                       cell_ = (Cell) tileParent_;
                       cell_.setBorder(BorderFactory.createLineBorder(Color.black));
                       tileParent_.revalidate();
                       tileParent_.repaint();
                       
                  }
              }
          } 
       }
       if (SwingUtilities.isRightMouseButton(event)) {
           
           if (event.getComponent() instanceof Content) {
               content_ = (Content) event.getComponent();
               content_.rotate();
           }   
       }
    }
};