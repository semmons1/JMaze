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
import javax.swing.filechooser.FileSystemView;

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
 * @tile_, a null variable related to the Tile class, to be used for object
 * manipulation.
 * @cell_, a null variable related to the Cell class, to be used for object
 * manipulation.
 * @content_, a null variable related to the Content class, to be used for
 * objects manipulation.
 * @defaultTileIds_ is the array meant to hold our tile ID #'s, it must be
 * accessed by different panels, hence why it is declared here.
 * @defaultRotations_ is a primitive array used to keep track of 
 * original Theta values when components are first randomized.
 * @isChanged_, a boolean variable that keeps track of when the game has been modified.
 * This lets the user save in case they forget upon exiting the game.
 * @tileComponents_, an ArrayList that is used to keep track of all Tile 
 * objects at the time of their generation.
 * @cellComponents_, an ArrayList that is used to keep track of all Cell 
 * objects at the time of their generation.
 * @randomComponents_, an ArrayList that is used to keep track of all 
 * randomized Content objects at the time of their generation.
 * @saveComponents_, an ArrayList to collect all Tile and Cell objects upon
 * initial generation. It is primarily used when new ID's and rotations must
 * be reassigned for a file the has been loaded.
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
    
    private static boolean isChanged_ = false;
     
    private static ArrayList<Component> tileComponents_ = new ArrayList<Component>();
    private static ArrayList<Component> cellComponents_ = new ArrayList<Component>();
    private static ArrayList<Component> randomComponents_ = new ArrayList<Component>();
    private ArrayList<Component> saveComponents_ = new ArrayList<Component>();
    
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
	    if(isChanged_) {
	        int saveOption = JOptionPane.showOptionDialog(null,"Would you like to save your game?", "Exit Options",
	                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"No", "Yes"}, null);
	        if (saveOption == JOptionPane.NO_OPTION) {
	        
	        fileOptions();
	        System.exit(0);
	        } else {
	            
	        System.exit(0);
	        }
	        
	    } else {
	        System.exit(0);
	    }
	}
	
	if ("reset".equals(e.getActionCommand())) {   
	    reset();
	    isChanged_ = false;
	}
	
	if ("file".equals(e.getActionCommand())) {
	    fileOptions();
	    
	}
	    
	
    }
    
    
    /**
     * This function makes use of Tile, Cell, and Content objects to manipulate the positions
     * of all movable objects, based on their original position.
     * In other words, once this function is called, all Content 
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
     * This wonderful block of ifs and loops is used to make decisions
     * based on what "file option" the user chooses. It contains many
     * instance variables that are only declared in this function, because they
     * are only ever used, by this function. 
     */
    public void fileOptions() {
        
        ArrayList<JComponent> movablePieces = new ArrayList<JComponent>();
        
        int saveOrLoad = JOptionPane.showOptionDialog(null,"Would you like to load or save a game?", "File Options",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Load", "Save"}, null);
        
        if (saveOrLoad == JOptionPane.YES_OPTION) {
            
            //Loading is performed here
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            
            int request = fileChooser.showOpenDialog(null);
            
            if (request == JFileChooser.APPROVE_OPTION) {
                
                //Send file to be loaded to the RawFileHandler constructor
                //for parsing.
                File fileToLoad = fileChooser.getSelectedFile();
                RawFileHandler rawFileHandler = new RawFileHandler(fileToLoad);
                
                int[] newPositions = rawFileHandler.getTileIds();
                int[] newRotations = rawFileHandler.getTileRotations();
                movablePieces = Tile.getContentArray(); //Load complete Content array.
                //For all 32 parent containers
               for (int i = 0; i < 32; i++) {
                    
                   //For all positions marked by 16 movable pieces.
                    for(int j = 0; j < 16; j ++) {
                        //if a position marked by load information is the same as the
                        //current position.
                        if (newPositions[j] == i ) {
                            //If this is for a Tile object
                            if ( i < 16) {
                                //Get tile based on ID. Add a piece and update rotation based on loaded IDs.
                                Tile saveTile = (Tile) saveComponents_.get(newPositions[j]);
                                Content saveContent = (Content) movablePieces.get(j);
                                saveContent.setSavedRotation(newRotations[j]);
                                saveTile.addLabel(saveContent);
                            }
                            //If this is for a Cell object
                            else if (i >= 16) {
                              //Get cell based on ID. Add a piece and update rotation based on loaded IDs.
                                Cell saveCell = (Cell) saveComponents_.get(newPositions[j]);
                                Content saveContent = (Content) movablePieces.get(j);
                                saveContent.setSavedRotation(newRotations[j]);
                                saveContent.setBorder(null);
                                saveCell.setBorder(null);
                                saveCell.addLabel(saveContent);
                            }
                        }
                    }
                    
                }
                repaint();
                revalidate();
                               
            }
          
        } 
        if (saveOrLoad == JOptionPane.NO_OPTION) {
            
            //Saving is performed here.
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            
            int request = fileChooser.showSaveDialog(null);
            
            if (request == JFileChooser.APPROVE_OPTION) {
                
                File fileToSave = fileChooser.getSelectedFile();
                
                if (!fileToSave.exists()) {
                    
                    RawFileHandler.saveFile(fileToSave);
                    isChanged_ = false;
                }
           
                else if (fileToSave.exists()) {
                    //Are you overwriting a file?
                    int overwriteRequest = JOptionPane.showOptionDialog(null, "Do you want to overwrite this file?",
                            "File Options", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null , null);
                    
                    if (overwriteRequest == JOptionPane.YES_OPTION) {
                        RawFileHandler.saveFile(fileToSave);
                        isChanged_= false;
                    }
                    
                    if (overwriteRequest == JOptionPane.NO_OPTION) {
                        fileOptions();
                    }
                }
            }
        }
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
	
	this.randomizeTiles();
	
	//Needed for loading purposes used in fileOptions()
	for (int i = 0;  i < 32; i++) {
	    if (i < 16) {
	        Tile tilesToAdd = (Tile) tileComponents_.get(i);
	        saveComponents_.add(tilesToAdd);
	    }
	    else if (i < 32 && i >=16 ) {
	        Cell cellsToAdd = (Cell) cellComponents_.get(i-16);
	        saveComponents_.add(cellsToAdd);
	    }
	}
	  	
	setVisible(true);
	
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
	  
	  lButton=new JButton("File");
	  mButton=new JButton("Reset");
	  rButton=new JButton("Quit");
	  
	  rButton.setActionCommand("exit");
	  rButton.addActionListener(this);
	  rButton.setToolTipText("Click to exit the game");
	  
	  mButton.setActionCommand("reset");
	  mButton.addActionListener(this);
	  mButton.setToolTipText("Click to reset all game peices");
	  
	  lButton.setActionCommand("file");
	  lButton.addActionListener(this);
	  lButton.setToolTipText("Click to save or load game");
	  
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
      
      
      /**
       * This function is tasked with the "randomization" of 
       * all movable Content objects. To summarize, this function
       * shuffles potential indices of Content objects, and 
       * assigns said indices to a Tile container. From there,
       * a random rotation index is generated, and assigned to
       * the Theta value of each Content object, effectively
       * randomizing the rotation of that object.
       */
      public void randomizeTiles() {
          
          Random random = new Random();
          ArrayList<Integer> randomNumberArray = new ArrayList<Integer>();
          
          for (int i = 0; i < defaultTileIds_.length; i++) {
              
              randomNumberArray.add(i);
              
          }
          
          Collections.shuffle(randomNumberArray);
          
          ArrayList<JComponent> contentList = Tile.getContentArray();
          
          for (int j = 0; j < defaultTileIds_.length; j++) {
              
              int randomNumber = randomNumberArray.get(j);
              
              tile_ = (Tile) tileComponents_.get(j);
              tile_.add(contentList.get(randomNumber));
              
              content_ = (Content) contentList.get(j);
              int randomRotation = random.nextInt(4);
              content_.setRotateCount(randomRotation);
              
              defaultRotations[j] = randomRotation;
              randomComponents_.add(contentList.get(randomNumber));
              
          }
          revalidate();
          repaint();
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
      
      /**
       * This is a setter this is used to determine if changes to the game
       * have been made, in case the user forgets to save. 
       * @param changed, a Boolean value that modifies the 
       * isChanged_ variable based on game events.
       */
      public static void setChanged(boolean changed) {
          isChanged_ = changed;
      }
     
};
