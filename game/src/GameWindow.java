/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Date: Apr 3, 2020
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.io.*;
/**
 * Main GameWindow class, responsible for alignment and generation of 
 * game pieces.
 * @leftTiles_ is initialized as a private variable here, so that it may be used
 * in it's appropriate method (addTilesWest). It is private so as to avoid any modifications
 * that could be made by other classes interacting with it. This is consistent
 * for all other panels in this class.
 * @centerGrid_ is initialized as a private variable here, so that it may be used
 * in it's appropriate method (addGridBoard). 
 * @rightTiles_ is initialized as a private variable here, so that it may be used
 * in it's appropriate method (addTilesEast).
 * @buttonPanel_ is initialized as a private variable here, so that it may be used
 * in it's appropriate method (addButtons).
 * @boardLayout_ is initialized as a private variable here, so that it may be used
 * in it's appropriate methods (all panel related methods).
 * @defaultTileIds_ is the array meant to hold our tile ID #'s, it must be
 * accessed by different panels, hence why it is declared here.
 * @tileComponents_, an ArrayList that is used to keep track of all Tile 
 * objects at the time of their generation.
 * @cellComponents_, an ArrayList that is used to keep track of all Cell 
 * objects at the time of their generation.
 */
public class GameWindow extends JFrame implements ActionListener, Serializable {
    
    private GridBagConstraints boardLayout_ = new GridBagConstraints();
    private JPanel leftTiles_ = new JPanel();
    private JPanel centerGrid_ = new JPanel();
    private JPanel rightTiles_ = new JPanel();
    private JPanel buttonPanel_ = new JPanel(new GridLayout(1, 0));
    
    private Tile tile_;
    private Cell cell_;
    private Content content_;
       
    private int[] defaultTileIds_ = new int[16];
    private static int[] defaultRotations = new int[16];
    private static ArrayList<Component> tileComponents_ = new ArrayList<Component>();
    private static ArrayList<Component> cellComponents_ = new ArrayList<Component>();
    private static ArrayList<Component> randomComponents_ = new ArrayList<Component>();
    
    public static final long serialVersionUID = 1;
    
    
    /**
     * Constructor sets the window name using super(), changes the layout.
     * @param teamName
     */
    public GameWindow(String teamName) {
	
	super(teamName);
	GridBagLayout gbl = new GridBagLayout();
	setLayout(gbl);
	
    }
    
    
    /**
     * For the buttons.
     * @param e is the ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        
	if ("exit".equals(e.getActionCommand())) {
	    System.exit(0);
	}
	
	if ("reset".equals(e.getActionCommand())) {   
	    reset();
	}
	
	if ("new".equals(e.getActionCommand())) {
	    System.out.println("new pressed\n");
	}
	
    }
    
    /**
     * This function makes use of a Tile and Cell object to manipulate the positions
     * of all Content objects, based on their original position.
     * In other words, once this function is called, all content 
     * objects will return to their native Tile containers.
     */
    public void reset() {
        
        for (int i = 0; i < defaultTileIds_.length; i++) {
           tile_ = (Tile) tileComponents_.get(i);
           content_ = (Content) randomComponents_.get(i);
           int index = content_.getDefaultRotation();
           content_.setRotateCount(index);
           tile_.add(content_);
           
           cell_ = (Cell) cellComponents_.get(i);
           cell_. setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
        revalidate();
        repaint();    
        
    }
    
    
    /**
     * For Game Board setup. Does no actual work,
     * just calls out to different methods that render and align our panels.
     */
    public void setUp() {
	
	this.addTilesWest();
	this.addGridBoard();
	this.addTilesEast();
	this.addButtons();
	
	setVisible(true);
	randomize();
	
	return;
	
    }


    /**
     * Initializes west panel and related constraints for tile holding purposes.
     * This is a common setup ritual that will be seen repeated
     * in the following methods.
     * @tile, which is a child class of JPanel, is accessed here. It can be found in Cell.java.
     */
      private void addTilesWest() {
	  
	  leftTiles_.setLayout(new GridBagLayout());
	  leftTiles_.setBackground(Color.GRAY);
    	  
          boardLayout_.anchor = GridBagConstraints.CENTER;
          boardLayout_.insets = new Insets(10, 120, 10, 120);
          boardLayout_.weightx = 0;
          boardLayout_.weighty = 0;
          boardLayout_.gridx = 0;
          boardLayout_.gridy = 0;
          boardLayout_.ipadx = 0;
          boardLayout_.ipady = 0;
          boardLayout_.gridwidth = 1;
          boardLayout_.gridheight = 1;
    	   
          for (int i = 0; i < 8; i++) {
              
              JPanel tile = new Tile();
              boardLayout_.gridy = i;
              leftTiles_.add(tile, boardLayout_);
              defaultTileIds_[i] = i;
              tileComponents_.add(tile); 
             
              
          }
          
          add(leftTiles_, boardLayout_);
        
    	  return;
    	  
      }

  
    /**
     * Initializes center grid panel and related constraints for
     * tile holding purposes. This method is home to a nested "for" loop
     * that is responsible for generating a 4x4 matrix.
     * @cell, which is a child class of JPanel, is used here. It can be found in Cell.java.
     */
      private void addGridBoard() {
	  
	  centerGrid_.setLayout(new GridBagLayout());
	  centerGrid_.setBackground(Color.GRAY);
	  
	  boardLayout_.anchor = GridBagConstraints.CENTER;
          boardLayout_.insets = new Insets(0, 0, 0, 0);
          boardLayout_.weightx = 1;
          boardLayout_.weighty = 1;
          boardLayout_.gridx = 1;
          boardLayout_.ipadx = 0;
          boardLayout_.ipady = 0;
          boardLayout_.gridy = 0;
          boardLayout_.gridwidth = 1;
          boardLayout_.gridheight = 1;
          
          for (int i = 0; i < 4; i++) {
              
              boardLayout_.gridy = i;

              for (int j = 0; j < 4; j++) {
                  
        	  JPanel cell = new Cell();
        	  boardLayout_.gridx = j;
        	  centerGrid_.add(cell, boardLayout_);
        	  cellComponents_.add(cell);
        	  
              }
          }
         
          add(centerGrid_, boardLayout_);
          
          return;
          
      }
  

    /**
      * Initializes east panel and related constraints for tile holding purposes.
      * @tile, which is a child class of JPanel, is accessed here, and can be found in Tile.java.
      */
      private void addTilesEast() {
	  
	  rightTiles_.setLayout(new GridBagLayout());
	  rightTiles_.setBackground(Color.GRAY);
      
          boardLayout_.anchor = GridBagConstraints.CENTER;
          boardLayout_.insets = new Insets(10, 120, 10, 120);
          boardLayout_.weightx= 0;
          boardLayout_.weighty = 0;
          boardLayout_.gridx = 0;
          boardLayout_.gridy = 0;
          boardLayout_.ipadx = 0;
          boardLayout_.ipady = 0;
          boardLayout_.gridwidth = 1;
          boardLayout_.gridheight = 1;
          
          for (int i = 0; i < 8; i++) {
              
              JPanel tile = new Tile();
              boardLayout_.gridy = i;
              rightTiles_.add(tile, boardLayout_);
              defaultTileIds_[i + 8] = i+8;
              tileComponents_.add(tile);
          }
          
          add(rightTiles_, boardLayout_);
       
          return;
          
      }

  
    /**
     * Initializes button panel and related constraints for button holding purposes.
     * This method contains it's own constraints so that our buttons can maintain
     * their own look that is separate from the global constraints.
     */
      private void addButtons() {
	  
	  setLayout(new GridBagLayout());
	  GridBagConstraints buttonConstraints = new GridBagConstraints();
	  
	  JButton lButton, mButton, rButton;
	  
	  lButton=new JButton("New Game");
	  mButton=new JButton("Reset");
	  rButton=new JButton("Exit");
	  
	  rButton.setActionCommand("exit");
	  rButton.addActionListener(this);
	  rButton.setToolTipText("Click to exit the game");
	  
	  mButton.setActionCommand("reset");
	  mButton.addActionListener(this);
	  mButton.setToolTipText("Click to reset all game peices");
	  
	  buttonPanel_.add(lButton);
	  buttonPanel_.add(mButton);
	  buttonPanel_.add(rButton);
          
	  buttonConstraints.anchor = GridBagConstraints.NORTH;
	  buttonConstraints.insets = new Insets(0, 0, 0, 0);
	  buttonConstraints.weightx = 0;
	  buttonConstraints.weighty = 0.2;
	  buttonConstraints.gridx = 1;
	  buttonConstraints.gridy = 0;
	  
	  add(buttonPanel_, buttonConstraints);
        
	  return;
      }
      
      
      //These getters will be needed at some point in the future to return vital components of the game board.
      /**
       * This function can be called from external classes to retrieve important Gameboard components.
       * @return leftTiles_, the backbone panel of the left tile column.
       */
      public JPanel getLeftPanel() {
	  
	  return leftTiles_;
	  
      }
      
      
      /**
       * This function can be called from external classes to retrieve important Gameboard components.
       * @return rightTiles_, the backbone panel of the right tile column.
       */
      public JPanel getRightPanel() {
	  
	  return rightTiles_;
	  
      }
      
      
      /**
       * This function can be called from external classes to retrieve important Gameboard components.
       * @return centerGrid_, the backbone panel of the GameBoard grid.
       */
      public JPanel getCenterPanel() {
	  
        return centerGrid_;
        
      }
      
      
      /**
       * This function can be called from external classes to retrieve important Gameboard components.
       * @return tileId_, the tile ID's that will likely be needed to check a win condition.
       */
      public int[] getTileIds() {
	  
        return defaultTileIds_;
        
      }
      
      /**
       * This function can be called from external classes to retrieve important Gameboard components.
       * @return buttonPanel_, the backbone panel of the buttons.
       */
      public JPanel getButtonPanel() {
	  
        return buttonPanel_;
        
      }
      
      public void randomize() {
          int counter = 0;
          Random random = new Random();
          Integer[] randomIndexOptions = new Integer[16];
          for (int i = 0; i < 16; i++) {
              randomIndexOptions[i] = i;
          }
          Collections.shuffle(Arrays.asList(randomIndexOptions));
          
          ArrayList<JComponent> contentList = Tile.getContentArray();
          
          while(counter != 16) {
              int randomIndex = randomIndexOptions[counter];
              
              tile_ = (Tile) tileComponents_.get(counter);
              tile_.add(contentList.get(randomIndex));
              
              content_ = (Content) contentList.get(counter);
              int randomRotation = random.nextInt(4);
              content_.setRotateCount(randomRotation);
              defaultRotations[counter] = randomRotation;
              randomComponents_.add(contentList.get(randomIndex));
              counter++;
          }       
      }
};


