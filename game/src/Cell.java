/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Date: Apr 16, 2020
 */

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Child class that is derived from JPanel, for now
 * serves the purposes of rendering a cell object on our
 * CENTER panel.
 * @CELL_DIMENSIONS_ is the immutable size constant for all cell objects.
 * @cellColors_ is a private custom color variable. Grey and deep sea blue
 * are complementary colors and help everything look nice.
 * @mouseHandler_ is a variable that allows for
 * mouse listening capabilities, and is directly
 * derived from the MouseHandler class. As such, 
 * all actions of a mouse event are handled in that class.
 * @positionNumber_, an static int variable that is used to keep track of this particular 
 * object ID. It is incremented in a counter-like fashion each time this object is called
 * in a factory method.
 * @generatedCellId_, an int variable that is used by Content to identify it's exact parent
 * container upon save.
 */
public class Cell extends JPanel implements Serializable {
    
    private static final Dimension CELL_DIMENSIONS_ = new Dimension(100, 100);
    private Color cellColors_ = new Color(1, 7, 91);
  
    private MouseHandler mouseHandler_ = new MouseHandler();
    
    private static int positionNumber_ = 16;
    private int generatedCellId_;
      
    private static final long serialVersionUID = 999L;
    
    /**
     * This is the constructor that defines the main attributes that each Cell object will have.
     * Along with size and color, each Cell object is given it's own mouse listener.
     */
    public Cell() {
	
	setBackground(cellColors_);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(CELL_DIMENSIONS_);
        setMinimumSize(CELL_DIMENSIONS_);
        addMouseListener(mouseHandler_);
        generatedCellId_ = positionNumber_;
        positionNumber_++;
                    
    }
    
    
    /**
     * This is a function that is called from the MouseHandler class, when a Label
     * object is shifted to a Cell object.
     * @param copiedLabel is brought in from the outcome of an event handled in MouseHandler.
     * When a new Label object is added to the container that is Cell, a new layout manager is 
     * needed, and BorderLayout fulfills the needs to get a Label object perfectly centered.
     */
    public void addLabel(JLabel copiedLabel) {

        setLayout(new BorderLayout());
        add(copiedLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
    
    }
    
    
    /**
     * This is a getter for returning a cell position.
     * Used for saving Content position in parent container.
     * @return generatedCellId_, an int value of the parent container.
     */
    public int getPosition() {
        return generatedCellId_; 
    }
};
