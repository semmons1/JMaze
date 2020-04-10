/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Date: Apr 3, 2020
 */

import java.io.*;
import java.nio.*;
import javax.swing.JOptionPane;
import java.awt.geom.Line2D;
import java.util.*;

/**
 * This class handles all of the parsing necessary for 
 * binary file formats. For now, it will take a hard-coded file path,
 * ensure the file exists, and then parses information into several different
 * data structures.
 * @fileName_, the hard coded filename that is passed to the constructor.
 * @tileIndex_, the number of tiles retrieved from the file.
 * @tileIds_, the IDS of each tile, as read from the file.
 * @tileLines_, the number of lines to be drawn on each game piece.
 * @floatValues_, the data structure to store two pairs of coordinates for 
 * each line on a game piece.
 * @xyCoords, an intermediary data structure that is meant to temporarily house
 * all four coordinate values from the file, and then transfer these values to 
 * floatValues_.
 * @lineInfo_, takes information housed in floatValues, and is exported to other
 * classes such as Content. It is much easier to manipulate and extract 
 * values with an ArrayList over a primitive type array.
 */
public class RawFileHandler implements Serializable {
    
    private String fileName_;
    
    private int tileIndex_;
    private int[] tileIds_ = new int[16];
    private int[] tileLines_;
    private Line2D[] floatValues_;
    private float[] xyCoords_;
    private ArrayList<Line2D[]> lineInfo_ = new ArrayList<Line2D[]>();
   
    
    private static final long serialVersionUID = 2;
    
    
    /**
     * This is the constructor that All RawFileHandler objects must be 
     * initiated through. Before any parsing is done, the given file path
     * is checked to ensure that it exists, and if it is the correct format.
     * If all is well, the file is parsed. 
     * @param fileName, the hardcoded file path provided in Content.
     */
    public RawFileHandler(String fileName) {
            
            File file = new File(fileName);
            //Does this file exist at this path?
            if(!file.exists()) {
                JOptionPane.showMessageDialog(null, "This file does not exist at the given path!",
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            else if(!getFileExtension(file).equals("mze")) {
                JOptionPane.showMessageDialog(null, "This file does not appear to be in the correct format!",
                        "File Format Issue", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
                
            }
            else {
                setFileName(fileName);
                parseBytes();
            }
    }
    
    /**
     * This function reads all information from the binary file in 
     * one go, and places all information in the data structures listed previously.
     * It can be confusing as to how it works, so please read the comments in the function body.
     * @return 0 if file is parsed successfully, -1 if an IO exception is caught.
     */
    public int parseBytes() {
        try (
                InputStream inputStream = new FileInputStream(fileName_);
                
        ) {
            
            byte[] byteSection = new byte[4];
                
            //Read in first line to determine how many tiles there are, or find "N".
            inputStream.read(byteSection);
            tileIndex_ = convertToInt(byteSection);
            //Size int arrays based on how many tiles we have.
            tileIds_ = new int[tileIndex_];
            tileLines_ = new int[tileIndex_];
            //For each tile
            for(int i = 0; i < tileIndex_; i++) {
                
                inputStream.read(byteSection);
                //tile "number", take current chunk and assign ID based on loop index
                tileIds_[i] = convertToInt(byteSection);
                inputStream.read(byteSection);
                //tile lines, take next chunk, assign int value for lines based on loop index
                tileLines_[i] = convertToInt(byteSection);
                // Since we will be using 2D lines on a JLabel object, store float values in a 2D
                //line array.
                floatValues_ = new Line2D[tileLines_[i]];
                //For each line, two pairs (four values) of floats
                for(int j = 0; j < tileLines_[i]; j++) {
                    
                    //make room for four values
                    xyCoords_ = new float[4];
                    for(int k = 0 ; k < 4; k++) {
                        
                        //get next chunk, and convert coordinates to floats
                        inputStream.read(byteSection);
                        xyCoords_[k] = convertToFloat(byteSection);
                    }
                    //Put the previously calculated coordinates into the 2DLine array.
                    float x0 = xyCoords_[0];
                    float y0 = xyCoords_[1];
                    float x1 = xyCoords_[2];
                    float y1 = xyCoords_[3];
                            
                    floatValues_[j] = new Line2D.Float(x0, y0, x1, y1);
                        
                }
                //Add the float values for the lines of tile 0, 1, 2, etc this array object,
                //then ship this data structure to "Contents".
                lineInfo_.add(floatValues_);
                    
            }
            
            inputStream.close();
            return 0;
            
        } catch (IOException exception) {
            exception.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Taken from java examples @ java2s.com, 
     * in convert data to byte array back and forth section.
     * This section focuses on taking primitive data types
     * such as int and float, and convert them into byte 
     * arrays.
     */
    public static byte[] convertToByteArray(int value) {
        
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putInt(value);
        return buffer.array();
        
    }
    
    public static byte[] convertToByteArray(long value) {
        
        byte[] bytes = new byte[8];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putLong(value);
        return buffer.array();
        
    }
    
    public static byte[] convertToByteArray(float value) {
        
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putFloat(value);
        return buffer.array();
        
    }
    
    /**
     * This section focuses on converting byte arrays
     * into primitive data types such as int and float.
     * These will likely be needed to direct
     * where to print 2d lines onto Jcomponents.
     */
    public static int convertToInt(byte[] array) {
        
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getInt();
        
    }
    
    public static float convertToFloat(byte[] array) {
        
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getFloat();
        
    }
    
    /**
     * This is a simple getter for file extension types.
     * @param file, the hard coded file path brought in through the constructor.
     * @return the file extension type, as a string.
     */
    public static String getFileExtension(File file) {
        
        String fileExtension = "";
        // Get file Name first
        String fileName = file.getName();
        
        // If fileName do not contain ".mze" or starts with "." then it is not a valid file
        if(fileName.contains(".") && fileName.lastIndexOf(".")!= 0) {
            
            fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
        }
        
        return fileExtension;
        
    }
    
    
    /**
     * This is a getter for the tile index.
     * @return int object tileIndex_.
     */
    public int getTileIndex() {

        return tileIndex_;
    }
    
    /**
     * This is a getter for the line information.
     * @return ArrayList<Line2D[]> object tileIndex_.
     */
    public ArrayList<Line2D[]> getLineInfo() {

        return lineInfo_;
        
    }
    
    /**
     * This is a getter for the individual tile lines.
     * @return int object tileLines_.
     */
    public int[] getTileLines() {
        
        //To avoid array mutation, clone data structure/object.
        return tileLines_.clone();
        
    }
    
    /**
     * This is a getter for the tile IDS.
     * @return int object tileIds_.
     */
    public int[] getTileIds() {
        //To avoid array mutation, clone data structure/object.
        return tileIds_.clone();
        
    }
    
    
    public void setFileName(String fileName) {
        
        fileName_ = fileName;
        
    } 
};

