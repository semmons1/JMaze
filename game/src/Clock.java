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
import javax.swing.Timer;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.awt.*;
import java.awt.event.*;
/**
 * This class serves the purpose of tracking the users
 * play time. It is essentially a digital display that uses a swing timer
 * to mimic how a real timer would act. The play time is saved and 
 * manipulated through a long float data type.
 * @simpleDateFormat_, a specialty variable this is used to format a long float date type, into 
 * a "time" based format.
 * @timePanel_, a JPanel that is used to "hold" our digital clock/timer
 * @capturedTime_, a long float variable that represents time played so far.
 * @isGoTime_, a boolean variable that appropriately measures when it is "go"
 * time for the timer, in other words, it triggers when the timer stops and runs.
 *
 */
public class Clock implements Serializable {
    
    private SimpleDateFormat simpleDateFormat_ = new SimpleDateFormat("KK:mm:ss");
    private final JLabel timePanel_ = new JLabel();
    //Magic number needed to format the default hour value in SDF to appear as though
    //it starts at 0
    private static long capturedTime_ = 0;
    
    private static boolean isGoTime_ = false;
          
    private static final long serialVersionUID = 1;
   
      /**
       * Constructor that initiates the timing process.  
       */
    public Clock() {
        Clock.ClockActions clockStart = new Clock.ClockActions();
        timePanel_.setForeground(Color.WHITE);
        simpleDateFormat_.setTimeZone(TimeZone.getTimeZone("GMT"));
        clockStart.startTimer();
              
    }
   
    
    /**
     * This is an inner class that deals with the Swing timer delay that makes this 
     * timer "look" so real. An incremented variable (capturedTime_) is adjusted every second, 
     * such that it looks like a second has passed on the digital timer.
     * @timer_, a Swing timer used to mimic a real digital timer.
     *
     */
    public class ClockActions implements ActionListener {
        
       
        private Timer timer_ = new Timer(100, this);
         

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            
            if (isGoTime_) {
                
                capturedTime_ += 100;
                timePanel_.setText(simpleDateFormat_.format(capturedTime_));
            }
            else if (!isGoTime_) {
                
                timePanel_.setText(simpleDateFormat_.format(capturedTime_)); 
            }
                        
        }
        
        public void startTimer() {
            
            timer_.start();            
        }
        
    };
    
    
    /**
     * This is a getter for the clock panel.
     * @return a JPanel holding the digital timer.
     */
    public JLabel getClockPanel() {
        
        return timePanel_;
    }
    
    
    /**
     * This is a getter that retrieves the boolean variable
     * that determines when the timer increments.
     * @return isGoTime_, a boolean variable for the timer.
     */
    public static boolean getGoTime() {
        
        return isGoTime_;
    }
    
    
    /**
     * This is the getter that returns the overall played time.
     * @return capturedTime_, a long float variable with time played so far.
     */
    public static long getCurrentTime() {
        
        return capturedTime_;
    }
    
    
    /**
     * This is a setter that adjusts the boolean value that determines
     * if the timer increments.
     * @param status, a boolean value for timer actions.
     */
    public static void setGoTime(boolean status) {
        
        isGoTime_ = status;   
    }
    
    /**
     * This is a setter that adjusts the current time played so far.
     * @param time, a long float variable that shows how long a game has been
     * played so far.
     */
    public static void setCurrentTime(long time) {
        
        capturedTime_ = time;
    }
   
};

    
