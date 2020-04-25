/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Date: Apr 16, 2020
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
 * @timer_ is used for the delayed background shift on Content objects
 * when an illegal move is made. It is initialized with a delay of 
 * 150 milliseconds, and a null action listener.
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
        	       
        	       MouseHandler.FlashPiece flash = new MouseHandler.FlashPiece();
        	       flash.setLabel(content_);
        	       content_.deselect();
        	       content_ = null;
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
                        GameWindow.setChanged(true);
                	
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
                       GameWindow.setChanged(true);
                       
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
                       GameWindow.setChanged(true);
                       
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
                       GameWindow.setChanged(true);
                       
                  }
              }
          } 
       }
       //Is right button pressed?
       if (SwingUtilities.isRightMouseButton(event)) {
           //Did we click on a label? 
           if (event.getComponent() instanceof Content) {
               //To protect against highlighting multiple game pieces.
               if (content_ != null) {
                   
                   content_.deselect();
                   isSelected_ = false;
                   
               }
               content_ = (Content) event.getComponent();
               content_.incrementTheta();
               GameWindow.setChanged(true);
           }   
       }
    }
   
   
   /**
    * This class is used when an illegal "Tile Swap" is made. 
    * The function begins with the label that was previously selected,
    * and changes it's background back and forth between a "danger zone"
    * color. Normally, this switch would be too quick for a human eye to 
    * register, and so a timer that utilizes a delay is used. 
    * @timer_, a Swing Timer to be used for background switch
    * delay.
    * @colors_, a color array used to switch background colors
    * @counter_ a int value that is used to determine how many
    * times a label will flash
    * @illegalLabel_, the label that was moved illegally.
    */
     public class FlashPiece implements ActionListener {
       
       private Timer timer_ = new Timer(150, this); 
       private Color[] colors_ = new Color[2];
       private int counter_ = 0;
       private JLabel illegalLabel_;
       
       
       @Override
       public void actionPerformed(ActionEvent actionEvent) {
           colors_[0] = new Color(96, 165, 218);
           colors_[1] = Color.RED;
       
           if (counter_ < 8) {
               illegalLabel_.setBackground(colors_[counter_%2]);
               counter_++;
           
           } else {
       
               illegalLabel_.setBackground(colors_[0]);
               timer_.stop();
               timer_.removeActionListener(this);
           
           }
       }
       
       /**
        * This function is called with a label that was moved
        * in an illegal fashion. This also starts
        * a swing timer sequence.
        * @param label, A JLabel that will "flash".
        */
       public void setLabel(JLabel label) {
           
           illegalLabel_ = label;
           timer_.start();
           
       }
   }; 
};
