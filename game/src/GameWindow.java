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
 * @clockPanel_, a JPanel that is used to hold the game timer. It is 
 * positioned directly above the button panel. 
 * @boardLayout_ is initialized as a private variable here, so that it may be used
 * in it's appropriate methods (all panel related methods).
 * @rawFileHandler_, an un-used variable that serves the purposes of checking if a 
 * default file is present. If not, all game setup is stopped, and the user 
 * is forced to ensure that this file is in place.
 * @tile_, a null variable related to the Tile class, to be used for object
 * manipulation.
 * @cell_, a null variable related to the Cell class, to be used for object
 * manipulation.
 * @content_, a null variable related to the Content class, to be used for
 * objects manipulation.
 * @clock_, a variable that initializes the game timer, and allows 
 * us to modify it's values based on actions the user follows through with.
 * @defaultTileIds_ is the array meant to hold our tile ID #'s, it must be
 * accessed by different panels, hence why it is declared here.
 * @defaultRotations_ is a primitive array used to keep track of 
 * original Theta values when components are first randomized.
 * @newResetRotations_ is a primitive array that is used to keep track of the 
 * rotation values that should be used upon reset, IFF a new file has been loaded.
 * @newResetPositions_ is a primitive array that is used to keep track of the 
 * position values that should be used upon reset, IFF a new file has been loaded.
 * @isChanged_, a boolean variable that keeps track of when the game has been modified.
 * This lets the user save in case they forget upon exiting the game.
 * @isNewLoad_, another boolean variable that is used to determine if the reset needs to 
 * shift pieces based on a previously loaded file, or a brand new game.
 * @tileComponents_, an ArrayList that is used to keep track of all Tile 
 * objects at the time of their generation.
 * @cellComponents_, an ArrayList that is used to keep track of all Cell 
 * objects at the time of their generation.
 * @randomComponents_, an ArrayList that is used to keep track of all 
 * randomized Content objects at the time of their generation.
 * @loadComponents_, an ArrayList to collect all Tile and Cell objects upon
 * initial generation. It is primarily used when new ID's and rotations must
 * be reassigned for a file the has been loaded.
 * @newResetPieces_, a JComponents ArrayList used to keep track of pieces that need to be shifted
 * upon reset, IFF a new file has been loaded.
 */
public class GameWindow extends JFrame implements ActionListener, Serializable {
    
    private GridBagConstraints boardLayout_ = new GridBagConstraints();
    private JPanel leftTiles_ = new JPanel();
    private JPanel centerGrid_ = new JPanel();
    private JPanel rightTiles_ = new JPanel();
    private JPanel buttonPanel_ = new JPanel(new GridLayout(1, 0));
    private JPanel clockPanel_ = new JPanel();
    
    RawFileHandler rawFileHandler_ = new RawFileHandler(new File("game/input/default.mze"));
    private Tile tile_;
    private Cell cell_;
    private Content content_;
    private Clock clock_ = new Clock();
            
    private int[] defaultTileIds_ = new int[16];
    private static int[] defaultRotations_ = new int[16];
    private static int[] newResetRotations_;
    private static int[] newResetPositions_;
    
    
    private static boolean isChanged_ = false;
    private static boolean isNewLoad_ = false;
     
    private static ArrayList<Component> tileComponents_ = new ArrayList<Component>();
    private static ArrayList<Component> cellComponents_ = new ArrayList<Component>();
    private static ArrayList<Component> randomComponents_ = new ArrayList<Component>();
    private static ArrayList<Component> loadComponents_ = new ArrayList<Component>();
    private static ArrayList<JComponent> newResetPieces_ = new ArrayList<JComponent>();
        
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
	        int saveCatch = JOptionPane.showOptionDialog(null, "Would you like to save before you proceed?",
                        "Save Options", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (saveCatch == JOptionPane.YES_OPTION) {
                    
                    FileOptions.saveOption();
                    System.exit(0);
                } 
	        	        
	        
	        System.exit(0);
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
     * If a new file has been loaded and a boolean check has been triggered,
     * reset pieces will be set much in the same way as they are initially 
     * loaded.
     */
    public void reset() {
        
        //Fresh reset, no file load.
        if (!isNewLoad_) {
            
            for (int i = 0; i < defaultTileIds_.length; i++) {
                tile_ = (Tile) tileComponents_.get(i);
                content_ = (Content) randomComponents_.get(i);
           
                int index = content_.getDefaultRotation();
           
                content_.setRotateCount(index);
                tile_.add(content_);
           
                cell_ = (Cell) cellComponents_.get(i);
                cell_. setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
            //Set timer to 00:00:00
            Clock.setCurrentTime(0);
            Clock.setGoTime(false);
            revalidate();
            repaint();    
        }
        //Reset based on last file loaded.
        else if (isNewLoad_) {
            
            FileOptions.arrangePieces();
            
            repaint();
            revalidate();
        }
    
    }
        
    
    
    /**
     * This function containing many if's and loops is used to make decisions
     * based on what "file option" the user chooses. It contains many
     * instance variables that are only declared in this function, because they
     * are only ever used, by this function.
     * It leverages functions from RawFileHandler, and FileOptions.
     * The majority of checks are used in the loading process, as this is the place where
     * most severe errors can occur. However, these have been shifted to their own class
     * as to prevent bloat.
     */
    public void fileOptions() {
        
      //Options for loading up a default file manually if it is not present
        if (!RawFileHandler.getMazeFileCheck() && !RawFileHandler.getDefaultCheck()) {
            
            JFileChooser fileChooser = new JFileChooser("game/input"); //Go straight to input folder
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            
            int request = fileChooser.showOpenDialog(null);
            
            if (request == JFileChooser.APPROVE_OPTION) {
                
                //Send file to be loaded to the RawFileHandler constructor
                //for parsing.
                File fileToLoad = fileChooser.getSelectedFile();
                
                //For if a junk filename is given.
                if (!fileToLoad.exists()) {
                    fileOptions();
                    return;
                }
                new RawFileHandler(fileToLoad);
                //Check formatting
                if (RawFileHandler.getHexCheck().contentEquals("CAFEBEEF")) {
                    
                    //Fix file path for Content Objects
                    Content.setOriginalFile(fileToLoad);
                    RawFileHandler.setMazeFileCheck(true);
                    //Reload game
                    setUp();
                    return;
                    
                } 
                else if(RawFileHandler.getHexCheck().contentEquals("CAFEDEED")) {
                   
                    Content.setOriginalFile(fileToLoad);
                    RawFileHandler.setMazeFileCheck(true);
                    //Reload game and set played pieces
                    setUp();
                    FileOptions.setLoadFile(fileToLoad);
                    FileOptions.arrangePieces();
                    repaint();
                    revalidate();
                    return;
                } else {
                    
                    //So you have chosen.... death?
                    JOptionPane.showMessageDialog(null, "This is not a valid file! Please try again.",
                            "Format Error", JOptionPane.ERROR_MESSAGE);
                    fileOptions();
                    return;
                    
                }
                
            }
              
        }
        
        //Normal load and save options if all is well                  
        int saveOrLoad = JOptionPane.showOptionDialog(null,"Would you like to load or save a game?", "File Options",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Load", "Save"}, null);
        
        if (saveOrLoad == JOptionPane.YES_OPTION) {
            
            //You want to load, but have you saved?
            if (isChanged_) {
                
                int saveCatch = JOptionPane.showOptionDialog(null, "Would you like to save before you proceed?",
                        "Save Options", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (saveCatch == JOptionPane.YES_OPTION) {
                    
                    isChanged_ = false;
                    FileOptions.saveOption();
                } 
            }
            //Go to load sequence
            FileOptions.loadOption();
            
                repaint();
                revalidate();
                               
        }
         
        if (saveOrLoad == JOptionPane.NO_OPTION) {
            
            //Saving is performed here.
            FileOptions.saveOption();
        }
    }
  
    
    
    /**
     * For Game Board setup. Does no actual work,
     * just calls out to different methods that render and align our panels.
     * Uses initial File Handler query to determine if a default file is present.
     * If not, force user to find one. Else, give blank frame.
     */
    public void setUp() {
        
        //Basically to avoid any nasty errors when a default file is not 
        //present. Forces the user to reload because they cannot play otherwise.
        if (!RawFileHandler.getMazeFileCheck()) { 
            
            fileOptions();
        }
            
	
        //Default file is present.
        else if (RawFileHandler.getMazeFileCheck()) {
            this.addTilesWest();
            this.addGridBoard();
            this.addTilesEast();
            this.addButtons();
		
            this.randomizeTiles();
	
            //Needed for loading purposes used in fileOptions(), all original
            //parent containers must be saved.
            for (int i = 0;  i < 32; i++) {
                if (i < 16) {
                    Tile tilesToAdd = (Tile) tileComponents_.get(i);
                    loadComponents_.add(tilesToAdd);
                }
                else if (i < 32 && i >=16 ) {
                    Cell cellsToAdd = (Cell) cellComponents_.get(i-16);
                    loadComponents_.add(cellsToAdd);
                }
            }

	  	
            setVisible(true);
            RawFileHandler.setDefaultCheck(true);
            return;
        }
	
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
     * their own look that is separate from the global constraints. This is also
     * where the clock is added to the game frame.
     */
      private void addButtons() {
	  
	  setLayout(new GridBagLayout());
	  GridBagConstraints buttonConstraints = new GridBagConstraints();
	  
	  clockPanel_.add(clock_.getClockPanel());
	  clockPanel_.setBackground(Color.GRAY);
	  
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
	  buttonConstraints.weighty = 0.4;
	  buttonConstraints.gridx = 1;
	  buttonConstraints.gridy = 0;
	  
	  add(clockPanel_, buttonConstraints);
	  
	  buttonConstraints.weighty = 0.1;
	  buttonConstraints.insets = new Insets(25, 0, 0, 0);
	  
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
              
              defaultRotations_[j] = randomRotation;
              randomComponents_.add(contentList.get(randomNumber));
              
          }
          revalidate();
          repaint();
      }
           
           
      /**
       * This function can be called from external classes to retrieve important Gameboard components.
       * @return tileId_, the tile ID's that will likely be needed to check a win condition.
       */
      public int[] getTileIds() {
	  
        return defaultTileIds_;
        
      }
      
      /**
       * This is a getter for new reset rotations, used when a new file is loaded.
       * @return newResetRotations_, an int array with rotation values.
       */
      public static int[] getNewResetRotations() {
          
          return newResetRotations_;
      
      }
     
      /**
       * This is a getter for new reset positions, used when a new file is loaded.
       * @return newResetPoostions_, an int array with position values.
       */
      public static int[] getNewResetPositions() {
          
          return newResetPositions_;
      
      }
      
      
      /**
       * This is a getter for the boolean flag that changes when the game has been
       * modified. 
       * @return isChanged_, a boolean value that marks when a move has been made.
       */
      public static boolean getIsChanged() {
          
          return isChanged_;
      }
      
      
      /**
       * This is a getter for the boolean flag that changes when a new load has 
       * been triggered.
       * @return isNewLoad_, a boolean value that marks when a new file has been loaded.
       */
      public static boolean getIsNewLoad() {
          
          return isNewLoad_;
          
      }
      
      /**
       * This a getter for all parent components in the game. Mainly used for resetting
       * and loading.
       * @return loadComponents_, an ArrayList of parent containers.
       */
      public static ArrayList<Component> getLoadComponents() {
          
          return loadComponents_;
      }
      
      
      /**
       * This is a getter for all new reset pieces. Mainly used for resetting and loading
       * @return newResetPieces_, an ArrayList of game pieces.
       */
      public static ArrayList<JComponent> getNewResetPieces() {
          
          return newResetPieces_;
      }
      
      /**
       * This is a getter for all tile parent containers.
       * @return tileComponents_, an ArrayList of Tile pieces.
       */
      public static ArrayList<Component> getTileComponents() {
          
          return tileComponents_;
          
      }
      
      /**
       * This is a getter for all cell parent containers.
       * @return cellComponents_, and ArrayList of Cell pieces.
       */
      public static ArrayList<Component> getCellComponents() {
          
          return cellComponents_;
          
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
      
      /**
       * This is a setter that is used to modify the flag that marks
       * when a new file has been loaded.
       * @param newLoad, a boolean value that modifies isNewLoad_.
       */
      public static void setIsNewLoad(boolean newLoad) {
          isNewLoad_ = newLoad;
      }
      
      /**
       * This is a setter that adjusts reset rotations based on a 
       * new file load.
       * @param rotations, an int array with new rotation values for reset.
       */
      public static void setNewResetRotations(int[] rotations) {
          newResetRotations_ = rotations;
      }
      
      /**
       * This is a setter that adjust reset positions based on a
       * new file load.
       * @param positions, an int array with new positions values for reset.
       */
      public static void setNewResetPositions(int[] positions) {
          
          newResetPositions_ = positions;
      }
      
      /**
       * This is a setter that adjusts game pieces based on a new file load.
       * @param components, an ArrayList with game components.
       */
      public static void setNewLoadComponents(ArrayList<Component> components) {
          
          loadComponents_ = components;
          
      }
      
      /**
       * This is a setter that adjusts reset pieces based on a new file load.
       * @param jcomponents, an ArrayList with new reset components.
       */
      public static void setNewResetPieces(ArrayList<JComponent> jcomponents) {
          
          newResetPieces_ = jcomponents;
          
      }
     
};
