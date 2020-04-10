/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Apr 3, 2020
 */

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.*;




/**
 * Child class that is derived from JLabel, for now
 * serves the purposes of representing an object that can be
 * shifted around the game board.
 * @tileCounter_ is a temporary integer argument that is meant to 
 * give a textual identifier for each Tile. 
 * @CONTENT_DIMENSIONS_ is the immutable size constant for all tile objects.
 * @contentBackground_ is the color of the label.
 * @contentForeground_ is the color of the text.
 * @mouseHandler_ is a variable that allows for mouse listening capabilities,
 * and is directly derived from the MouseHandler class. 
 * As such, all actions of a mouse events are handled in that class.
 */
public class Content extends JLabel implements Serializable {
        
    //We only want these file handler related methods called once, hence static
    //declarations. Content is called 16 times, to fill and so this was a problem
    //When everything was being called 16 times, when the file only needs to be loaded 
    //once
    private static RawFileHandler rawFileHandler_ =  new RawFileHandler("input/default.mze"); //could also be ../../input/default.mze
    private static int tileIndex_ = 0;
    private static ArrayList<Line2D[]> lineInfo_ = rawFileHandler_.getLineInfo();
    private Line2D[] currentLines_;
    
    private static final Dimension CONTENT_DIMENSIONS_ = new Dimension(90, 90);
    private Color contentBackground_ = new Color(96, 165, 218);

    private MouseHandler mouseHandler_ = new MouseHandler(); 
    
    private int rotateCount;
    private int defaultRotation;
   
    
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
        setLines(lineInfo_.get(tileIndex_));
        tileIndex_++;
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
        
        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;
        double radians = (rotateCount * 90 * Math.PI) / (180);
        
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
    
    
    public void rotate() {
        if (rotateCount < 3) {
            rotateCount++;
        }
        else {
            rotateCount = 0;
        }
        this.repaint(); 
    }
    
    
    public void setLines(Line2D[] importedLines) {
        currentLines_ = importedLines;
    }
    
    public void setRotateCount(int setting) {
        rotateCount = setting;
        defaultRotation = rotateCount;
        System.out.println(rotateCount);
        this.repaint();
    }
    
    public int getDefaultRotation() {
        return defaultRotation;
    }
};
