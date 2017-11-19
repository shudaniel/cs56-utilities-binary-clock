package edu.ucsb.cs56.projects.utilities.clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.reflect.Array;
import javax.swing.Timer;
import java.io.*;
import javax.sound.sampled.*;
import java.applet.*;
import java.net.*;

/**
 * Creates a power of 2 binary clock widget that gets the
 * current time and displays it as a set of 2D blocks.
 * @@@ To Do:
 * @@@ - Configuration settings import and GUI
 * @@@ - Support for multiple render styles such as 'tutorial', 'no text',
 *          and maybe a multicolored one with tick animations.
 * @@@ - More efficient thread loop
 * @@@ - Windowless fullscreen support? (ie ctrl+shift+F11)
 * @author Kevin La
 * @author Chantel Chan
 * @version for UCSB CS56, F16, legacy code project
 */
public class BinaryClock extends JFrame implements Runnable
{
    private JFrame frame;
    private static int frameheight;
    private static int framewidth;

    protected JLabel time, tut;

    protected JTextField dateField;

    protected TimePanel panel;

    protected String hour, minute10s, minute1s, second10s, second1s, AM_PM;
    protected String date;

    protected static Boolean refresh = false;

    private long startTime,runningTime;
    private long secTimer,minTimer,hrTimer,slast,mlast,hlast;
    private long ampmTimer, ampmlast; // you need to implement these for the flickering issue
    private Boolean soundmute;

    protected Color setBackgroundColor = Color.BLACK;
    // protected Color setOnBlockColor = new Color(0xFAFFFD);
    protected Color setOnBlockColor = new Color(0x727473);
    //protected Color setOffBlockColor = new Color(0x727473);
    protected Color setOffBlockColor = new Color(0xFAFFFD);


    /**
         Constructor
    */
    public BinaryClock()
    {
    	soundmute = false;
        frameheight = 720;
        framewidth = 1280;
       

        //Make frame and all objects
        frame = new JFrame();
	frame.getContentPane().setBackground(setBackgroundColor);
	frame.setSize(framewidth, frameheight);
	frame.setTitle("Binary Clock");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//frame.getContentPane().setLayout(new FlowLayout());
        //frame.setVisible(true);
	time = new JLabel();
	time.setFont(new Font("URW Gothic L", Font.BOLD,20));
	time.setForeground(Color.WHITE);
	Date today = new Date();
	tut = new JLabel("Today is: " + today);
	tut.setForeground(Color.WHITE);
	tut.setFont(new Font("URW Gothic L", Font.BOLD,20));
       	ActionListener updatetime = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    Date today = new Date();
		    tut.setText("Today is: " + new Date().toString());
		}
	    };
	new Timer(1000, updatetime).start();
	panel = new TimePanel("Tutorial", setBackgroundColor, setOnBlockColor, setOffBlockColor); //No real modes are supported at the moment
    }

    public static int getFrameHeight(){
	
	return frameheight;
    }

    public static int getFrameWidth(){
   	
	return framewidth;
    }
   

    
        void resetAll() {
	frame.getContentPane().removeAll();
	frame.setSize(framewidth,frameheight);
        Date today = new Date();
	JLabel tut = new JLabel("Today is: "+ today);
	tut.setForeground(Color.WHITE);
	tut.setFont(new Font("URW Gothic L", Font.BOLD,20));
	panel = new TimePanel("Tutorial", setBackgroundColor, setOnBlockColor, setOffBlockColor);
	frame. getContentPane().add(BorderLayout.CENTER, panel);
	frame. getContentPane().add(BorderLayout.NORTH, time);
	frame. getContentPane().add(BorderLayout.SOUTH, tut);
	frame.getContentPane().validate();
	frame.getContentPane().repaint();
	refresh = true;
	 ActionListener updatetime = new ActionListener() {
	       public void actionPerformed(ActionEvent e) {
		   Date today = new Date();
		   tut.setText("Today is: " + new Date().toString());
	       }
	    };
	new Timer(1000, updatetime).start();
	}

    // Menu bar
    JMenuBar menubar = new JMenuBar();




    //This is the Drop Down Menu Bar, options
    void setFrameBase() {

	//add objects to the frame
   	 frame. getContentPane().add(BorderLayout.CENTER, panel);
	 frame. getContentPane().add(BorderLayout.NORTH, time);
	 frame. getContentPane().add(BorderLayout.SOUTH, tut);
	 frame. setJMenuBar(menubar);
	
	// Menus
	JMenu file = new JMenu("File");
	menubar.add(file);
	JMenuItem exit = new JMenuItem("Exit");
	file.add(exit);

	// Exit listener for exit menuItem
	class exitaction implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		System.exit(0);
	    }
	}
	exit.addActionListener(new exitaction());

	JMenu help = new JMenu("Help");
	menubar.add(help);
	JMenuItem instructions = new JMenuItem("Instructions");
	help.add(instructions);

		// Instructions under help
	class instructions implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		BinaryClock.helpBox("This is a binary clock. Think of the clock showing the time as 00:00:00 AM/PM, \n which represents hours:minutes:seconds.Find out the time by adding each column \n with its respective value, shown on the left of each row. The first column on \n the left displays if it AM or PM. The next column shows the hours. The next two \n columns represents the minutes, where the third column is the minutes by multiples \n of ten and the fourth column with the minutes in ones. The last column shows the \n seconds.", "Instructions");
	    }
	}
	instructions.addActionListener(new instructions());
    


    // Turn sound on and off
    JMenu soundOff = new JMenu("Sound Options");
    menubar.add(soundOff);
    JMenuItem mute = new JMenuItem("Sound Off");
    soundOff.add(mute);
    JMenuItem unmute = new JMenuItem("Sound On");
    soundOff.add(unmute);

    class mute implements ActionListener{
    	public void actionPerformed(ActionEvent e){
    		soundmute = true;
    	}
    }
    mute.addActionListener(new mute());

    class unmute implements ActionListener{
    	public void actionPerformed(ActionEvent e){
    		soundmute = false;
    	}
    }
    unmute.addActionListener(new unmute());

	}





    //This is to set the color of the background
    void setBackgroundColor() {
	
	JMenu backgroundColorSelector = new JMenu("Background Color");
	menubar.add(backgroundColorSelector);
	JMenuItem limegreenBackground = new JMenuItem("Lime Green");
	JMenuItem whiteblueBackground = new JMenuItem("White blue");
	JMenuItem blackBackground = new JMenuItem("Black");
	JMenuItem navyblueBackground = new JMenuItem("Navy Blue");
	JMenuItem darkblueBackground = new JMenuItem("Dark Blue");
	JMenuItem redBackground = new JMenuItem("Red");
	backgroundColorSelector.add(limegreenBackground);
	backgroundColorSelector.add(whiteblueBackground);
	backgroundColorSelector.add(blackBackground);
	backgroundColorSelector.add(navyblueBackground);
	backgroundColorSelector.add(darkblueBackground);
	backgroundColorSelector.add(redBackground);

	// Lime Green Background
	class limegreenBackgroundClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		Color limegreen = new Color(0xB7FFD8);
		frame.getContentPane().setBackground(limegreen);
		setBackgroundColor = limegreen;
		resetAll();
	    }
	}
	limegreenBackground.addActionListener( new limegreenBackgroundClass());

	// Very very very very light blue Background
	class whiteblueBackgroundClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		Color whiteblue = new Color (0xD5E9EC);
		frame.getContentPane().setBackground(whiteblue);
		setBackgroundColor = whiteblue;
	        resetAll();
	    }
	}
	whiteblueBackground.addActionListener( new whiteblueBackgroundClass());

	// Dark Blue Background
	class darkblueBackgroundClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		Color darkBlue = new Color(0x273043);
		frame.getContentPane().setBackground(darkBlue);
	        setBackgroundColor = darkBlue;
		resetAll();
	    }
	}
	darkblueBackground.addActionListener( new darkblueBackgroundClass());

	// Navy Blue Background
	class navyblueBackgroundClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		Color navyblue = new Color(0x175676);
		frame.getContentPane().setBackground(navyblue);
	        setBackgroundColor = navyblue;
		resetAll();
	    }
	}
	navyblueBackground.addActionListener( new navyblueBackgroundClass());

	// Blood Red Background
	class redBackgroundClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		Color bloodred = new Color(0xB3001B);
		frame.getContentPane().setBackground(bloodred);
		setBackgroundColor = bloodred;
		resetAll();
	    }
	}
	redBackground.addActionListener( new redBackgroundClass());

	//Black Background
	class blackBackgroundClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		Color black = new Color(0x0F0D0F);
		frame.getContentPane().setBackground(black);
		setBackgroundColor = black;
		resetAll();
	    }
	}
	blackBackground.addActionListener( new blackBackgroundClass());
    }



    
    //Set the color of the individual blocks
    void setBlockColor(){
	JMenu onBlockColorSelector = new JMenu("On Block Color");
	menubar.add(onBlockColorSelector);
	JMenuItem redOnBlock = new JMenuItem("Red");
	JMenuItem grassOnBlock = new JMenuItem("Grass Green");
	JMenuItem greyOnBlock = new JMenuItem("Grey");
	onBlockColorSelector.add(redOnBlock);
	onBlockColorSelector.add(grassOnBlock);
	onBlockColorSelector.add(greyOnBlock);

	JMenu offBlockColorSelector = new JMenu("Off Block Color");
	menubar.add(offBlockColorSelector);
	JMenuItem cyanOffBlock = new JMenuItem("Blue");
	JMenuItem blackOffBlock = new JMenuItem("Black");
	JMenuItem whiteOffBlock = new JMenuItem("White");
	offBlockColorSelector.add(cyanOffBlock);
	offBlockColorSelector.add(blackOffBlock);
	offBlockColorSelector.add(whiteOffBlock);

	
	// Red On Box
	class redOnBoxClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		setOnBlockColor = Color.RED;
		resetAll();
	    }
	}
	redOnBlock.addActionListener( new redOnBoxClass());

	// Grass On Box
	class grassOnBoxClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		setOnBlockColor = new Color(0x2AFC98);
		resetAll();
	    }
	}
        grassOnBlock.addActionListener( new grassOnBoxClass());

	// Grey On Box
	class greyOnBoxClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		setOnBlockColor = new Color(0x727473);
		resetAll();
	    }
	}
	greyOnBlock.addActionListener( new greyOnBoxClass());

	// Cyan Off Box
	class cyanOffBoxClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		setOffBlockColor = Color.CYAN;
		resetAll();
	    }
	}
	cyanOffBlock.addActionListener( new cyanOffBoxClass());

	// Black Off Box
	class blackOffBoxClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		setOffBlockColor = Color.BLACK;
		resetAll();
	    }
	}
	blackOffBlock.addActionListener( new blackOffBoxClass());

	// White Off Box
	class whiteOffBoxClass implements ActionListener{
	    public void actionPerformed(ActionEvent e){
		setOffBlockColor = Color.WHITE;
		resetAll();
	    }
	}
	whiteOffBlock.addActionListener( new whiteOffBoxClass());

    }




    //Method to be able to set the screen size in JMenu 
    void setWindowSize(){
	JMenu screenSettings = new JMenu("Screen Settings");
	menubar.add(screenSettings);
	JMenuItem minimize = new JMenuItem("Minimize");
	screenSettings.add(minimize);
	JMenuItem maximize = new JMenuItem("Maximize");
	screenSettings.add(maximize);

	// Minimize Screen 
	class minimizeScreen implements ActionListener{
	    public void actionPerformed(ActionEvent e){

		framewidth = 1050;
		frameheight = 480;
		frame.getContentPane().removeAll();
		frame.setSize(framewidth,frameheight);
		
		Date today= new Date();
		JLabel tut = new JLabel("Today is: " + today);
		tut.setForeground(Color.WHITE);
		tut.setFont(new Font("URW Gothic L", Font.BOLD,20));
	       	ActionListener updatetime = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    Date today = new Date();
			    tut.setText("Today is: " + new Date().toString());
			}
		    };
		new Timer(1000, updatetime).start();
		panel = new TimePanel("Tutorial", setBackgroundColor, setOnBlockColor, setOffBlockColor);
		frame. getContentPane().add(BorderLayout.CENTER, panel);
		frame. getContentPane().add(BorderLayout.NORTH, time);
		frame. getContentPane().add(BorderLayout.SOUTH, tut);
		FlowLayout ex = new FlowLayout();
		frame.getContentPane().setLayout(ex);
		frame.getContentPane().validate();
		frame.getContentPane().repaint();
		refresh = true;
	    }
	}

	minimize.addActionListener(new minimizeScreen());
	// Maximize Screen 
	class maximizeScreen implements ActionListener{
	    public void actionPerformed(ActionEvent e){

		framewidth = 1160;
     		frameheight = 720;
		BorderLayout ex = new BorderLayout();
		frame.getContentPane().setLayout(ex);
		resetAll();
	    }
	}

	maximize.addActionListener(new maximizeScreen());
	
    }
       


    /**
        Main Function
        @param args not used
    */
    public static void main(String[] args)
    {
	//SwingUtilities.invokeLater(new ResolutionChangedDemo());
	
	BinaryClock bc = new BinaryClock();
	bc.setFrameBase();
	bc.setBackgroundColor();
	bc.setBlockColor();
	bc.setWindowSize();
	//bc.sound();
        Thread ClockUpdater = new Thread(bc);
	ClockUpdater.start();
    }
    
    /**
       Used by main's thread to finish initializing clock display.
       Loops into itself for inefficient updates.
       @@@ Will call update() to begin more efficient loop@@@
    */
    
    public void run()
    {
        //fetch time data
        date = String.format("%tr", new Date());
	
        //break up time data into binary strings, then feed to blocks
        hour = Integer.toBinaryString(Integer.parseInt(date.substring(0, 2)));
	    updateBlocks(hour, panel.getHour());
	
        minute10s = Integer.toBinaryString(Integer.parseInt(date.substring(3, 4)));
	    updateBlocks(minute10s, panel.getMinute10s());
        minute1s = Integer.toBinaryString(Integer.parseInt(date.substring(4, 5)));
	    updateBlocks(minute1s, panel.getMinute1s());
	
        second10s = Integer.toBinaryString(Integer.parseInt(date.substring(6, 7)));
            updateBlocks(second10s, panel.getSecond10s());
        second1s = Integer.toBinaryString(Integer.parseInt(date.substring(7, 8)));
            updateBlocks(second1s, panel.getSecond1s());

        //translate Am/Pm data and feed to blocks
        if(date.charAt(9)=='A')
	    AM_PM = "1";
        else AM_PM = "0";
            updateAmPmBlocks(AM_PM, panel.getAmPm());

        //set text on the time panel for debugging purposes. Temporary.
        //time.setText(date);

	frame.setVisible(true);

	//start thread timer
	startTime = System.currentTimeMillis();
	slast = System.currentTimeMillis(); // temporary
	mlast = System.currentTimeMillis(); // temporary
	hlast = System.currentTimeMillis(); // temporary
	ampmlast = System.currentTimeMillis(); // temporary
        //tell the thread to sleep for a twentieth of a second before reiterating
        try
        {
            Thread.sleep(50);
        }

	catch(InterruptedException ex)
        {
            ex.printStackTrace();
        }

        //Keep the clock running forever until the user closes the application
        while(true){
        	update();
        }
    }

    /**
        Used by main's thread to update the clock display every 50ms.
        Currently an unused placeholder, will be a more efficient loop than run().
    */
    
    protected void update()
    {
	secTimer = System.currentTimeMillis() - slast;
	minTimer = System.currentTimeMillis() - mlast;
	hrTimer = System.currentTimeMillis() - hlast;
	ampmTimer = System.currentTimeMillis() - ampmlast;
        date = String.format("%tr", new Date());

	// This loop is more efficient because it only updates the blocks that need to be updated
	// For example, hour won't get updated every single second
	
	// update seconds after every 1000 ms
	if(secTimer > 900){
	    second10s = Integer.toBinaryString(Integer.parseInt(date.substring(6, 7)));
            updateBlocks(second10s, panel.getSecond10s());

	    second1s = Integer.toBinaryString(Integer.parseInt(date.substring(7, 8)));
	    updateBlocks(second1s, panel.getSecond1s());
	    slast = System.currentTimeMillis();

	    if(!soundmute){ 
	    	playSound();
	    }

	}
	
	
	// update minute after every 60,000 ms
	if((minTimer > 1000 * 60 - 100) || (refresh == true)){
	    minute10s = Integer.toBinaryString(Integer.parseInt(date.substring(3, 4)));
	    updateBlocks(minute10s, panel.getMinute10s());
	    minute1s = Integer.toBinaryString(Integer.parseInt(date.substring(4, 5)));
	    updateBlocks(minute1s, panel.getMinute1s());
	    mlast = System.currentTimeMillis();
	}
	
	// update hour after 3,600,000 ms
	if((hrTimer > 1000 * 60 * 60 - 100) || (refresh == true)){
	    hour = Integer.toBinaryString(Integer.parseInt(date.substring(0, 2)));
	    updateBlocks(hour, panel.getHour()); 
	    hlast = System.currentTimeMillis();
	    }
        
	// update am/pm after 12 hours (3,600,000 * 12)
	if((ampmTimer > 1000 * 60 * 60 * 12 - 100) || (refresh == true)){
	    if(date.charAt(9)=='A')
		AM_PM = "1";
	    else AM_PM = "0";
	    updateAmPmBlocks(AM_PM, panel.getAmPm());
	    refresh = false;
	}
	
        //tell the thread to sleep before reiterating
        try
	    {
		Thread.sleep(500);
	    }

	catch(InterruptedException ex)
	    {
		ex.printStackTrace();
	    }
    }

    /**
        Inputs the string into the array of blocks
        @param s Binary String to be input
        @param blocks Array of Blocks to be updated
    */
    protected void updateBlocks(String s, Block[] blocks)
    {
        for(int i =  Array.getLength(blocks) - 1; i >= 0; i--)
        {
            //associates appropriate blocks to their bits
            if(i<s.length())
                blocks[i].input(s.charAt(s.length()-1-i));
            else blocks[i].input('0');

        }
    }

    /**
        Inputs the string into the AmPm array of blocks
        @param s Binary String to be input
        @param blocks Array of Blocks to be updated
    */
    protected void updateAmPmBlocks(String s, Block[] blocks)
    {
        if(s.charAt(0) == '1') //if it's am
        {
            blocks[0].input('1'); //am is on
            blocks[1].input('0'); //pm is off
        } else
        {
            blocks[0].input('0');
            blocks[1].input('1');
        }
    }

    public static void helpBox(String helpMessage, String titleBar)
    {
	final JOptionPane pane = new JOptionPane(helpMessage);
	final JDialog d = pane.createDialog((JFrame)null, titleBar);
	d.setLocation(250,80);
	d.setVisible(true);
    }

    public static void playSound()
    {       
    	try {
    	    File soundFile = new File("Sounds/clock-tick1.wav");
    		AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
    	    Clip clip = AudioSystem.getClip();
    	    clip.open(audioIn);
    	    clip.start();
    	} catch (UnsupportedAudioFileException e) {
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	} catch (LineUnavailableException e) {
    	    e.printStackTrace();
    	}
	
    }
}

