/**
 * @author Alexander Finch
 * @author Colin Woods
 * @author Mariah Moore
 * @author Peter Harris
 * @author Stefan Emmons
 *
 * Date: Apr 16, 2020
 */

import java.io.*;
import java.nio.*;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import java.awt.geom.Line2D;
import java.util.*;

/**
 * This class handles all of the parsing necessary for 
 * binary file formats. For now, it will take a hard-coded file path,
 * ensure the file exists, and then parses information into several different
 * data structures.
 * @fileName_, the filename that is passed to the constructor via
 * default load, or custom save/load.
 * @tileIndex_, the number of tiles retrieved from the file.
 * @tileIds_, the IDS of each tile, as read from the file.
 * @tileRotations_, the integer value of rotations extracted from a saved file.
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
    
    private File fileName_;
    
    private int tileIndex_;
    private int[] tileIds_;
    private int[] tileRotations_;
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
     * @param fileName, the file path provided in Content or GameWindow.
     */
    public RawFileHandler(File fileName) {
            
            //Does this file exist at this path?
            if(!fileName.exists()) {
                JOptionPane.showMessageDialog(null, "This file does not exist at the given path!",
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                
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
     * This function also decides whether to load the default file, or a saved file
     * based on the first four hex values present. If the values are not correct,
     * no file will be loaded beyond the default.
     * @return 0 if file is parsed successfully, -1 if an IO exception is caught.
     */
    public int parseBytes() {
        try (
                InputStream inputStream = new FileInputStream(fileName_);
                
        ) {
            
            byte[] byteSection = new byte[4];
                
            //Read in first line to determine how many tiles there are, or find "N".
            inputStream.read(byteSection);
            
            String hexValues = convertToHex(byteSection);
            
            if (hexValues.contentEquals("CAFEDEED")) {
                
                loadFile(fileName_);
                return 0;
            } 
            else if (hexValues.contentEquals("CAFEBEEF")) {
                
                inputStream.read(byteSection);           
                tileIndex_ = convertToInt(byteSection);
           
                //Size int arrays based on how many tiles we have.
                tileIds_ = new int[tileIndex_];
                tileLines_ = new int[tileIndex_];
                //For each tile
                for(int i = 0; i < tileIndex_; i++) {
                    
                    //Need to read ahead by two byte sections because of new hex offset.
                    inputStream.read(byteSection);
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
            } else {
                
                System.out.println("Bad file!");
                return -1;
              }
            
        } catch (IOException exception) {
            
            exception.printStackTrace();
            return -1;
          }
    }
        
    
    /**
     * This function is called from GameWindow and is meant to export
     * information in the form of integer Position ID's, 
     * rotations values, and float line information. Ultimately, the most important
     * part to be saved is the position ID, and the rotation setting.
     * This function contains several instance variables of its own that 
     * are not included in the class declaration. They are only used as 
     * intermediaries that help write information to a file.
     * @param saveFile, the selected file to save raw int and float data.
     */
    public static void saveFile(File saveFile) {
        
        int[] hexValues = new int[4];
        float[] tempCoordinateArray = new float[4];
        
        try (
                OutputStream outputStream = new FileOutputStream(saveFile);
        ) {
            //Mark this file with needed hex values
            hexValues[0] = 0xca;
            hexValues[1] = 0xfe;
            hexValues[2] = 0xde;
            hexValues[3] = 0xed;
            for (int i = 0; i < hexValues.length; i++) {
                
                outputStream.write(hexValues[i]);
                
            }
            //Number of movable tiles
            outputStream.write(convertToByteArray(16));
                    
            for (int j = 0; j < 16; j++) {
                
                //Get current Content pieces, find their positions.
                ArrayList<JComponent> contentList = Tile.getContentArray();
                Content content = (Content) contentList.get(j);
                
                //Handy function that determines current parent container,
                //and returns their ID.
                int currentPosition = content.getPosition();
                outputStream.write(convertToByteArray(currentPosition));
                   
                //Get rotation of current piece.
                int currentRotation = content.getCurrentRotation();
                outputStream.write(convertToByteArray(currentRotation));
                 
                //Get lines of current piece. 
                Line2D[] currentLines = content.getLines();
                outputStream.write(convertToByteArray(currentLines.length));
                
                //Extract line coordinates one at a time, and write to file.
                for (Line2D line : currentLines) {
                    
                    float x0 = (float) line.getX1();
                    float y0 = (float) line.getY1();
                    float x1 = (float) line.getX2();
                    float y1 = (float) line.getY2();
                    tempCoordinateArray[0] = x0;
                    tempCoordinateArray[1] = y0;
                    tempCoordinateArray[2] = x1;
                    tempCoordinateArray[3] = y1;
                    for(int k = 0; k < 4; k++) {
                        
                        outputStream.write(convertToByteArray(tempCoordinateArray[k]));
                   }
                }
            }
            
            outputStream.close();
        } catch (IOException exception) {
            
            exception.printStackTrace();
          }
    }
                
     
    
    /**
     * Based on the first four hex values that are read, this function is called
     * if a given file is marked as previously played and saved. It is quite
     * similar in structure to parseBytes(), with the exception that it reads 
     * rotation information. This function uses variables that are declared
     * with the class, as the data they hold needs to be manipulated and
     * accessed by other classes.
     * @param loadFile, the file the has been selected for loading.
     */
    public void loadFile(File loadFile) {
        
        //clear out any potentially old or outdated line info.
        lineInfo_.clear();
        
        try (
                InputStream inputStream = new FileInputStream(loadFile);
                
        ) {
            
            byte[] byteSection = new byte[4];
            //Does nothing, just reads over the hex values that have already
            //been checked.
            inputStream.read(byteSection);
            
            //See parseBytes(), very similar read structure.
            inputStream.read(byteSection);
            tileIndex_ = convertToInt(byteSection);
            
            
            tileIds_ = new int[tileIndex_];
            tileLines_ = new int[tileIndex_];
            tileRotations_ = new int[tileIndex_];
            
            for (int i = 0; i < tileIndex_; i++) {
               
                inputStream.read(byteSection);
                //tile position ID
                tileIds_[i] = convertToInt(byteSection);
                
                inputStream.read(byteSection);
                //tile rotation ID
                tileRotations_[i] = convertToInt(byteSection);
                
                inputStream.read(byteSection);
                //Number of lines for tile
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
            
        } catch (IOException exception) {
            exception.printStackTrace();
            
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
     * This is a handy conversion function that uses
     * bitwise operations to quickly convert hex values
     * into a readable string. 
     * Taken from bytesToHex examples @ java2s.com
     * @param array, the byte array that is passed in containing 
     * hex values
     * @return a String with readable hex information.
     */
    public static String convertToHex(byte[] array) {
        
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[array.length*2];
        
        for (int i = 0; i < array.length; i++) {
            
            int vector = array[i] & 0xFF;
            hexChars[i * 2] = hexArray[vector >>> 4];
            hexChars[i * 2 + 1] = hexArray[vector & 0x0F];
            
        }
        return new String(hexChars);
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
        return tileLines_;
        
    }
    
    /**
     * This is a getter for the tile IDS.
     * @return int object tileIds_.
     */
    public int[] getTileIds() {
        //To avoid array mutation, clone data structure/object.
        return tileIds_;
        
    }
    
    
    /**
     * This is a getter for tile Rotations. 
     * @return int object tileRotations_.
     */
    public int[] getTileRotations() {
        return tileRotations_;
    }
    
    
    public void setFileName(File fileName) {
        
        fileName_ = fileName;
        
    } 
};

