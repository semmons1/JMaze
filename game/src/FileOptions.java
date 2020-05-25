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
import java.util.*;
import java.io.*;

/**
 * This is a class that is meant to be statically accessed only when the user
 * wishes to carry out file options with their current game. Since file options
 * have many choices that must be made, along with lengthy data read/writing procedures,
 * everything has been consolidated in this class. There is no need for a constructor
 * or factory method initialization. 
 * @loadFile_, a File variable that is needed to pass path information
 * between loadOption and arrangePieces. This is primarily used when a default file
 * is not present.
 */
public class FileOptions implements Serializable {
 
    
    private static File loadFile_;
    
    static final long serialVersionUID = 1L;
    
    
    /**
     * This static function performs all saving actions needed for this game.
     * It interacts with functions from RawFileHandler, and modifies variables
     * in GameWindow. It is the simpler of the two file options.
     */
    public static void saveOption() {
        
        JFileChooser fileChooser = new JFileChooser("game/input");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        int request = fileChooser.showSaveDialog(null);
        
        if(request == JFileChooser.APPROVE_OPTION) {
            
            File fileToSave = fileChooser.getSelectedFile();
            
            if(!fileToSave.exists()) {
                
                RawFileHandler.saveFile(fileToSave);
                
                //Quit without save prompt
                GameWindow.setChanged(false);
            }
            
            else if (fileToSave.exists()) {
                
                //Are you overwriting a file?
                int overwriteRequest = JOptionPane.showOptionDialog(null, "Do you want to overwrite this file?",
                        "File Options", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null , null);
                
                if (overwriteRequest == JOptionPane.YES_OPTION) {
                    
                    RawFileHandler.saveFile(fileToSave);
                    GameWindow.setChanged(false);
                }
                
                if (overwriteRequest == JOptionPane.NO_OPTION) {
                    
                    saveOption();
                    
                }
            }
        }   
    }
    
    
    /**
     * This static function performs all loading actions needed for this game.
     * It interacts with functions and modifies variables from RawFileHandler, and GameWindow.
     * This function has a large amount of catches so that the user has a great degree of flexibility
     * as to what they can do. Most normal, and mistaken actions, are covered.
     */
    public static void loadOption() {
        
        JFileChooser fileChooser = new JFileChooser("game/input");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        int request = fileChooser.showOpenDialog(null);
        
        if (request == JFileChooser.APPROVE_OPTION) {
            //Send file to be loaded to the RawFileHandler constructor
            //for parsing.
            File fileToLoad = fileChooser.getSelectedFile();
            RawFileHandler rawFileHandler = new RawFileHandler(fileToLoad);
            loadFile_ = fileToLoad;
            
            //A simple check for if a bad file is loaded. Remove the maze, but in a 
            //"creative" way, wipe the whole maze.
            if (!RawFileHandler.getMazeFileCheck() && RawFileHandler.getDefaultCheck()) {
                
                JOptionPane.showMessageDialog(null, "This is not a valid file! Please try again.",
                        "Format Error", JOptionPane.ERROR_MESSAGE);
                ArrayList<Component> tileList =  GameWindow.getTileComponents();
                ArrayList<Component> cellList = GameWindow.getCellComponents();
                
                for (int i = 0; i < 16; i++) {
                    Tile tiles = (Tile) tileList.get(i);
                    Cell cells = (Cell) cellList.get(i);
                    tiles.removeAll();
                    tiles.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    cells.removeAll();
                    cells.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                }
                
                RawFileHandler.setMazeFileCheck(true);
                return;
            }
            
            //Are you trying to load a default file?
            if (RawFileHandler.getHexCheck().contentEquals("CAFEBEEF")) {
                
                return;
            }
          //File is good? Move along with load.
            arrangePieces();
        } 
        else if (request == JFileChooser.CANCEL_OPTION) {
            
            return;
        }
      
    }
    
    /**
     * This is the last static function of FileOptions. It deals exclusively with rearranging all pieces
     * based on information provided in a .MZE file. Most checks for reading position and rotation information
     * are handled in loadOption, so that this can smoothly carry out piece adjustments. It is so flexible, that it can even
     * be used by other classes such as GameWindow.
     */
    public static void arrangePieces() {
        
        ArrayList<JComponent> originalContentData = Tile.getContentArray();
        ArrayList<Component> gameCells = GameWindow.getCellComponents();
        ArrayList<Component> loadPieces = GameWindow.getLoadComponents();
        RawFileHandler rawFileHandler = new RawFileHandler(loadFile_);
        int[] newPositions = rawFileHandler.getTileIds();
        int[] newRotations = rawFileHandler.getTileRotations();
                
        //Get new rest values ready
        int[] newResetRotations = new int[16];
        int[] newResetPositions = new int[16];
        
        
        //Reset all border for Cell containers.
        for (int i = 0; i <16; i++) {
            
            Cell cell = (Cell) gameCells.get(i);
            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
        
        //For all 32 parent containers
        for (int i = 0; i< 32; i++) {
            
            //For all positions marked by 16 movable pieces
            for (int j = 0; j <16; j++) {
                
                //if a position marked by load information is the same 
                //as the current position
                if (newPositions[j] == i) {
                    
                    //if this is for a Tile object
                    if (i < 16) {
                        
                        //Get tile based on ID. Add a piece and update rotations based on loaded IDs.
                        Tile loadTile = (Tile) loadPieces.get(newPositions[j]);
                        Content loadContent = (Content) originalContentData.get(j);
                        
                        loadContent.setSavedRotation(newRotations[j]);
                        loadTile.addLabel(loadContent);
                    }
                    //if this is for a Cell object
                    else if (i >= 16) {
                        
                        //Get cell based on ID. Add a piece and update rotations based on loaded IDs.
                        Cell loadCell = (Cell) loadPieces.get(newPositions[j]);
                        Content loadContent = (Content) originalContentData.get(j);
                        
                        loadContent.setSavedRotation(newRotations[j]);
                        loadContent.setBorder(null);
                        loadCell.setBorder(null);
                        loadCell.addLabel(loadContent);
                    }
                }
            }
        }
        //Flag that this is a new load
        GameWindow.setIsNewLoad(true);
        //Set new reset positions.
        newResetRotations = newRotations;
        newResetPositions = newPositions;
        GameWindow.setNewResetRotations(newResetRotations);
        GameWindow.setNewResetPositions(newResetPositions);
        //Adjust clock values and pause
        long newClockTime = rawFileHandler.getLoadTime();
        Clock.setCurrentTime(newClockTime);
        Clock.setGoTime(false);    
    }
    
    
    /**
     * This is a setter that adjusts the file path location for a load file, if 
     * a default file is not present.
     * @param file, and File variable that adjusts loadFile_.
     */
    public static void setLoadFile(File file) {
        loadFile_ = file;
    }
               
};
