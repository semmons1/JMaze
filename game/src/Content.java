/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Apr 16, 2020
 */

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.*;
import java.io.*;




/**
 * Child class that is derived from JLabel, for now
 * serves the purposes of representing an object that can be
 * shifted around the game board.
 * @originalFile_ is a File argument that is meant to pass the default
 * maze file to the RawFileHandler constructor.
 * @CONTENT_DIMENSIONS_ is the immutable size constant for all tile objects.
 * @rawFileHandler_ is an instance of the RawFileHandler class, used to load
 * the default maze file.
 * @contentIndex_ is an int value that is primarily used to keep track of what lines
 * an object will be generated with.
 * @mouseHandler_ is a variable that allows for mouse listening capabilities,
 * and is directly derived from the MouseHandler class. 
 * As such, all actions of a mouse events are handled in that class.
 * @theta_ is a primitive int variable that is used to represent what degree shift
 * a content object should be at (90, 180, 270, 360).
 * @defaultRotation_ is a primitive int variable that is used to keep track of 
 * the degree shift that a Content object is originally spawned with. 
 * @lineInfo_, an ArrayList that is imported from RawFileHandler, and contains
 * line information for all sixteen pieces. contentIndex_ is used to decide
 * which lines go where.
 * @currentLines_, a 2D line array the allows paintComponent() to draw lines
 * on a given piece.
 * @contentBackground_, the background color of a Content object.
 */
public class Content extends JLabel implements Serializable {
        
    private File originalFile_ = new File("input/default.mze"); //could also be ../../input/default.mze
    private RawFileHandler rawFileHandler_ =  new RawFileHandler(originalFile_); 
    private static int contentIndex_ = 0;
    private ArrayList<Line2D[]> lineInfo_ = rawFileHandler_.getLineInfo();
    private Line2D[] currentLines_;
    
    private static final Dimension CONTENT_DIMENSIONS_ = new Dimension(100, 100);
    private Color contentBackground_ = new Color(96, 165, 218);

    private MouseHandler mouseHandler_ = new MouseHandler(); 
    
    private int theta_ = 0;
    private int defaultRotation_;
    
    private static final long serialVersionUID = 992L;
   
    
    /**
     * This is the constructor that defines the main attributes that each Content object will have.
     * Along with size and opacity and text, each Tile object is given it's own mouse listener.
     */
    public Content() {
        
        setBackground(contentBackground_);
        setOpaque(true);
        setPreferredSize(CONTENT_DIMENSIONS_);
        setMinimumSize(CONTENT_DIMENSIONS_);
        addMouseListener(mouseHandler_);
        /*Each time this constructor is called, tileIndex_ is incremented. 
        tileIndex_ serves as a way of knowing which line coordinates to retrieve,
        so the paintComponent function will assign the correct lines, to the correct tile.
        Since paintComponent is called many times through this program
        (repaint(),etc.), it is not a good idea to do anything more than the absolute minimum
        required to draw the lines on a Content object. The Override below is risky enough
        with all the repaint() functions that are called in this program.
        */
        setLines(lineInfo_.get(contentIndex_));
        contentIndex_++;
    }
    
    //DON'T mess with this function, it affects how everything is rendered in the game.
    //In this instance, in only takes in local line data, and applies (draws) it
    //on a specified Content object.
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.setPaint(Color.BLACK);
        
        //For some reason, you must interact with the rotate function
        //before any lines are drawn.
        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;
        double radians = (theta_ * (Math.PI / 2));
        
        g2d.rotate(radians, halfWidth, halfHeight);
        
        //For a Line in the most recently retrieved Lines
        for(Line2D line : currentLines_) {
            
            float x0 = (float) line.getX1();
            float y0 = (float) line.getY1();
            float x1 = (float) line.getX2();
            float y1 = (float) line.getY2();
                    
            g2d.draw(new Line2D.Float(x0, y0, x1, y1));
            
        }
    }
    
    
    /**
     * This is a function that is called from the MouseHandler class, it handles border changing to
     * signify that a Content object has been selected.
     */
    public void select() {
        
        setBorder(BorderFactory.createLineBorder(Color.RED));
        
    }
    
    
    /**
     * This is another function that is called from the MouseHandler class, it handles border changing to
     * signify that a Content object has been deselected.
     */
    public void deselect() {

        setBorder(null);
        
    }
    
    
    /**
     * This function is accessed by the MouseHandler class to manually increment the 
     * Theta value a Content object has. This allows for object rotation by modifying the
     * Radian value that the object is painted with. 
     */
    public void incrementTheta() {
        
        theta_ = (theta_ + 1) % 4;
        this.repaint(); 
    }
    
    
    /**
     * This is a setter that is used to keep track of which Content object
     * gets a certain set of lines. 
     * @param importedLines, the 2D Lines to be drawn on a specified Content
     * object.
     */
    public void setLines(Line2D[] importedLines) {
        currentLines_ = importedLines;
    }
    
    public Line2D[] getLines() {
        return currentLines_;
    }
    
    
    /**
     * This setter is used to "pre-load" the rotation values that each
     * Content object is spawned with. Primarily used in the reset sequence.
     * @param setting
     */
    public void setRotateCount(int setting) {
        theta_ = setting;
        defaultRotation_ = theta_;
        this.repaint();
    }
    
    public void setSavedRotation(int savedSetting) {
        theta_ = savedSetting;
        this.repaint();
    }
    
    
    /**
     * This getter returns the pre-loaded rotation values that
     * each Content object is spawned with.
     * @return defaultRotation_, an int value to be used in Radian calculation.
     */
    public int getDefaultRotation() {
        return defaultRotation_;
    }
    
    public int getCurrentRotation() {
        return theta_;
    }
    
  
    /**
     * This is a getter function that DOES NOT
     * actually return the position of a Content object.
     * Instead, it returns the ID of it's parent container.
     * @return an int value that represents the current parent
     * container ID.
     */
    public int getPosition() {
        Container parent = this.getParent();
        if (parent instanceof Cell) {
            Cell cParent = ((Cell) parent);
            return cParent.getPosition();
        
        } else {
            Tile tParent = ((Tile) parent);
            return tParent.getPosition();
        }
           
    }
};
