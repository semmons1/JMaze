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
import java.io.Serializable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is called each time a mouse event is observed. It checks to see
 * if the player has made a winning move, each time a move is made. This is accomplished
 * through checking parent container IDs, content IDs, and rotations values.
 * This does not look to a file for a winning solution, that seems unnecessary given the 
 * fact that we can easily use getters that track current rotation and position.
 * @checkList_, the original Content pieces. Their IDs are checked, along with the IDs
 * of the parent container they currently reside in.
 * @isMatched_ is a boolean variable used to determine if all game pieces have been matched.
 * @simpleDateFormat_, a specialty variable this is used to format a long float date type, into 
 * a "time" based format.
 *
 */
public class WinCheck implements Serializable {
        
    private static ArrayList<JComponent> checkList_ = Tile.getContentArray();
    
    private static boolean isMatched_ = false;
    
    private static SimpleDateFormat simpleDateFormat_ = new SimpleDateFormat("KK:mm:ss");
    
    private static final long serialVersionUID = 1;
    
    
    /**
     * This function is called from MouseHandler each time a move or rotation is made.
     * It briefly checks the IDs, and positions IDs of each content object.
     * The rotation of each object is also checked. If all pieces match the winning condition, 
     * the user is notified that they have won the game.
     */
    public static void IdCheck() {
        
        for (int i = 0; i < 16; i++) {
            
            Content checkedPiece = (Content) checkList_.get(i);
            if ((checkedPiece.getContentId() + 16) == checkedPiece.getPosition() &&
                    checkedPiece.getCurrentRotation() == 0) {
                
                isMatched_ = true;
                continue;
                
            } else {
                
                isMatched_ = false;
                return;
            }
            
            
        }
        if(isMatched_) {
            
            Clock.setGoTime(false);
            JOptionPane.showMessageDialog(null, "Congrats! You won the Game! Your time was " + 
                    simpleDateFormat_.format(Clock.getCurrentTime()),"Game Won", JOptionPane.INFORMATION_MESSAGE);
            GameWindow.setChanged(false);
            
        } else {
            
            return;
        }
        
        
    }
    
};