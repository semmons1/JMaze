/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Date: May 12, 2020
 */

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Child class that is derived from JPanel, for now
 * serves the purposes of holding a tile object on our
 * WEST and EAST tile panels.
 * @TILE_DIMENSIONS_ is the immutable size constant for all tile objects.
 * @mouseHandler_ is a variable that allows for mouse listening capabilities, 
 * and is directly derived from the MouseHandler class. As such, all actions of a 
 * mouse event are handled in that class.
 * @content_ is a variable this is meant to represent the label that 
 * each of these tile objects will be holding.
 * It is added to each new tile object, as the constructor is called. 
 * We have done this so that we have a lot of flexibility in terms of 
 * graphics that will be a part of this label,
 * but will not interfere with the underlying panel. 
 * @contentInfo_ is meant to house Content objects at the time of their original generation.
 * This variables is accessed when Content objects need to be moved independent of a
 * mouse handling event.
 * @positionNumber_, an static int variable that is used to keep track of this particular 
 * object ID. It is incremented in a counter-like fashion each time this object is called
 * in a factory method.
 * @generatedTileId_, an int variable that is used by Content to identify it's exact parent
 * container upon save.
 */
public class Tile extends JPanel implements Serializable {

    private static final Dimension TILE_DIMENSIONS_ = new Dimension (100, 100);
    private MouseHandler mouseHandler_ = new MouseHandler();
    
    private Content content_ = new Content();
    
    private static ArrayList<JComponent> contentInfo_ = new ArrayList<JComponent>();
    
    private static int positionNumber_ = 0;
    private int generatedTileId_;
       
    private static final long serialVersionUID = 999L;
    
    
    /**
     * This is the constructor that defines the main attributes that each Tile object will have.
     * Along with size and opacity, each Tile object is given it's own mouse listener.
     */
    public Tile() {
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setOpaque(false);
        setPreferredSize(TILE_DIMENSIONS_);
        setMinimumSize(TILE_DIMENSIONS_);
        setLayout(new BorderLayout());
        add(content_, BorderLayout.CENTER);
        addMouseListener(mouseHandler_);
        
        contentInfo_.add(content_);
        generatedTileId_ = positionNumber_;
        positionNumber_++;
        
    }
    
    
    /**
     * This is a function that is called from the MouseHandler class, when a Label
     * object is shifted to a Tile object.
     * @param copiedLabel is brought in from the outcome of an event handled in MouseHandler.
     * When a new Label object is added to the container that is Cell, a new layout manager is 
     * needed, and BorderLayout fulfills the needs to get a Label object perfectly centered.
     * In this case, the layout manager of a Tile object is defined in it's constructor.
     */
    public void addLabel(JLabel copiedLabel) {

        setLayout(new BorderLayout());
        add(copiedLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
    }
    
    
    /**
     * This is a static getter for an ArrayList that contains Content info
     * for each Tile object.
     * @return contentInfo_, an ArrayList with original Content information.
     */
    public static ArrayList<JComponent> getContentArray() {
        return contentInfo_;
    }
    
    
    /**
     * This a static setter for an ArrayList that contains Content info
     * for each Tile object.
     * @param importedChanges, an ArrayList that contains changes made
     * to the original contentInfo_ variable.
     */
    public static void setContentArray(ArrayList<JComponent> importedChanges) {
        contentInfo_ = importedChanges;
    }
      
     
    /**
     * This is a getter for returning a tile position.
     * Used for saving Content position in parent container.
     * @return generatedTileId_, an int value of the parent container.
     */
    public int getPosition() {
        return generatedTileId_; 
    }
    
    
      
};
