import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.gicentre.utils.gui.TextPopup; 
import org.gicentre.utils.stat.*; 
import controlP5.*; 
import processing.serial.*; 
import java.io.*; 
import java.util.Arrays; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class WheeStat5_4 extends PApplet {

/*  WheeStat5_4 Processing sketch
 *    6/13/14
 *   GUI for WheeStat 5 series Potentiostats. 
 *    
 *    by Jack Summers, Ben Hickman 
 *  
 *    May 1, 2014, conversion of output file to csv format
 *      replaced line 9 of SerialRead tab:  String[] tokens = sData3.split(tab);
 *    May 19, added chronoamerometry and normal pulse voltammetry modes
 *    
 *    Revision 1_4 improves update speed using readStringUntil(LINE_FEED)
 *    End of run signaled by transmission of "99999,999999\n" from LaunchPad
 */
 
///////////////////////////////////////// Imports///////////////////////////////
 // for warning window
    // For chart classes.
//import org.gicentre.utils.multisketch.*; // for integration window


                        // this is needed for BufferedWriter

/////////////////////////////////////////Classes////////////////////////////////
XYChart lineChart;  

ControlP5 cp5,cp5b,cp5c;
Serial serialPort;
Textarea errorText;   // com port and status window
Textarea myTextarea2;    // save file path window
Textfield Starting_Voltage, End_Voltage, Scan_Rate, Delay_Time, Gain, offset;
Textfield InitialV_Time, FinalV_Time, Number_of_Runs, Run_Interval, delay2;
DropdownList ports, mode, ovrLy;              //Define the variable ports and mode as Dropdownlists.

//////////////////////////////////variables/////////////////////////////////////

char[] strtochar;
//char cData;
String sData3;
//String sData3 ="";
//String[] sData = new String[3];  //String sData;
/*float[] V = {0};
float[] I1 = {0};
float[] newV = {0};   // added to reset V after each run Nov 19 BH
float[] newI1 = {0}; */ // added to reset I1 after each run Nov 19 BH
boolean Modesel = false;
int overlay = 0;
String runMode;
////////////// is this necessary? ///////
/*String RAMP = "0";
String CV = "1";
String ASV = "2";            //missing differential pulse
String logASV = "3";
String ChronoAmp = "5";
String ChronoAmp2 = "6"; */
/////////////////////////////
int LINE_FEED = 10; // used in serial read to identify data sets
String tab ="\t";   // used in serial read to identify data splits
String xChartLabel;

float[] xData = {0};   
float[] yData = {0};
float[] nullData = {0};
float[] nullY = {0};
float xRead = 0;   
float yRead = 0;
float yRead1 = 0;
float yRead2 = 0;
//float yMax = 3280;      // maximum current reading from hardware
//float yMin = 90;       // minimum current reading from hardware
//int error = 0;
//int error1 = 0;
//int error2 = 0;
int Ss;                          //The dropdown list returns a float, must convert into an int. 
String[] comList ;               //A string to hold the ports in.
String[] comList2;               // string to compare comlist to and update
boolean serialSet;               //A value to test if we have setup the Serial port.
boolean Comselected = false;     //A value to test if you have chosen a port in the list.
boolean gotparams = false;

boolean run = false;           // start run at bang
boolean stopped = true;        // is program actually stopped?, added 6/17/14
float p1;
float p2;

String ComP;
int serialPortNumber;
String file1 = "logdata.txt";
String file2;                  // save file path
//String file;
String[] sData = new String[3];  //String sData;
String sData2 = " ";
char cData;
char cData2;

String Go = "1";
//String star = "*";
int i =0;
int p = 0;           //stop signal
//int updatechart;
/************* parameters for retrieving values from text fields**********/
//String box3lable = "mV / s";
String vInit;          
String vFinal;
String scanRate;
String initialDelay;
String nRuns;
String logIvl;
String cGain;
String vOffset;
String del2;   // delay used in chronoamperometry?
int iInit;
int iFinal;
int iRate;
int iDelay;
int iRuns;
int iIvl;
int iGain;
int iOffset;
int iDl2;
String runTxt;      // run or stop

//float res0[] = {0.3,0.69,1.081,1.472,1.862,2.253,2.644,3.425,4.206,4.988,6.550,8.112,10.066,12.800,15.925,20.222,25.300,31.550,39.750};  // R4, gain = 10,000*R4/R5
//float res1[] = {10.456,7.722,6.195,4.988,3.816,3.034,2.253,1.862,1.472,1.081,0.691,0.300};  // R5, offset = 10/R5
//float gainK;
//float offG;
PImage logo;
//////////////font variables////////////////////////////////////////////////////
PFont font = createFont("arial", 18);
PFont font2 = createFont("arial", 16);
PFont font3 = createFont("arial",12); 
PFont font4 = createFont("andalus",16);
/////////////// setup //////////////////////////////////////////////////////////
public void setup()
{
  setup_bangs();
  charts_gic_setup();
  cp5_controllers_setup();

  frameRate(2000);
  size(730, 550); 
  //textFont(font2);
  //frame.setResizable(true);
  logo = loadImage("LogoSMS.png");
//cp5 = new ControlP5(this);

//PImage[] imgs = {loadImage("run_button1.png"),loadImage("run_button2.png"),loadImage("run_button3.png")};
//PImage[] img2 = {loadImage("stop_button1.png"),loadImage("stop_button2.png"),loadImage("stop_button3.png")};

}
///////////////////End Setup////////////////////////////////////////////////////


public void draw()
{
  background(0);
  image(logo, 29, 500, 130, 34);
/*  textFont(font4,18);
  pushMatrix();
  fill(#DEC507);
  textAlign(LEFT);
  text("Smoky Mountain",30,height-30);
  text("Scientific",58,height-12);
  popMatrix(); */
  
  stroke(255);
  noFill();
  rect (12,50,160,85);
  rect (12,143,160,85);
  rect (12,236,160,85);
  rect (12,330,160,85);
  pushMatrix();
  textFont(font,12);

  fill(0xffDEC507);
  textAlign(RIGHT);
  text("https://github.com/SmokyMountainScientific",680,height-12);
  popMatrix(); 
  textAlign(LEFT); 

 if (run == true) {
   runTxt = "Stop";
   }
 else { 
   runTxt = "Run";
   } 
 textFont(font,24);
  fill(255);
  text(runTxt, 90, height-93);

  pushMatrix();
  if (Modesel==false) {
    Starting_Voltage.hide();
    End_Voltage.hide();
    Delay_Time.hide();
    Scan_Rate.hide();
    Gain.hide();
    offset.hide();
    delay2.hide();
    Number_of_Runs.hide();
    Run_Interval.hide();
  }
   popMatrix(); 

////////////// update com ports from Ben, added 6/13/14 /////
comList2 = Serial.list();
if(Arrays.equals(comList,comList2)==false) {
  ports.clear();
  comList = comList2;
  for (int i=0; i<comList.length; i++) {
    ports.addItem(comList[i],i);
  }
}
if(comList.length == 0){
  myTextarea2.setText("NOT CONN.");
  ports.clear();
  ports.captionLabel().set("Select COM port");
  try{
    serialPort.stop();
  }
  catch(Exception e){
  }
  Comselected = false;
}
////////////// end of com port update addition //////////////
   if (Modesel==true) {
  Starting_Voltage.show();  
  End_Voltage.show();
  Delay_Time.show();
  Gain.show();
  offset.show();
  // rotation of text
   textFont(font2);
  fill(250,250,250);             //Chart heading color
  textSize(16);
  text("Voltage limits (mV)", 20, 70);
  text("Current", 20, 163);
 
  if (runMode=="ChronoAmp"||runMode=="ChronoAmp2") {   //chronoAmperometry experiments
    text("Delay 1     Read time", 20, 256);
   delay2.show();
   Scan_Rate.hide();
 }
  else {
        text("Delay 1   Scan Rate", 20, 256);
        Scan_Rate.show();
        delay2.hide();
  }
  if (runMode=="logASV") {
    Number_of_Runs.show();
    Run_Interval.show();
    text("Multiple Runs", 20, 350);
  }
  else {
    Number_of_Runs.hide();
    Run_Interval.hide();
  }

    
  }
 textFont(font2);
  
  fill(0xffEADFC9);               // background color
  rect(200, 70, 475, 450);    // chart background

////// catch errors while drawing chart - from BH
  try {
    lineChart.draw(250, 70, 430, 420);
  }
  catch(Exception e){}  
     fill(0,0,0);
   int posX =220;  // x position for center of y axis
   int posY = 260;  // y position for center of y axis
  translate(posX,posY);
  rotate(3.14159f*3/2);
  textAlign(CENTER);
  text("Current  (microamps)", 0, 0);
  rotate(3.14159f/2);        // return orientation and location
  translate(-posX,-posY);  
  
  if (runMode=="ChronoAmp"||runMode=="ChronoAmp2") { 
  xChartLabel = "Time (miliseconds)";
  }
  else {
      xChartLabel = "Voltage (mV)";
    }
  //}
  posX = 475;
  posY = 515;
  translate(posX,posY);
  textAlign(CENTER);
  text(xChartLabel, 0, 0);
  translate(-posX,-posY);  

////////////////// Start of run ///////////////////////////////
/*  if (run == false && stopped == false){   //send stop command
   serialPort.write('%');
   stopped = true;
 }*/
  
  if (run == true && Comselected == true)             
  {
    //println("run-304"); 
    if(gotparams == false)   // added to update chart in real time Nov19 BH
    {
//if (yData[0] != 0 && overlay == 0) {  // jack's original
if (yData.length != 0 && overlay == 0) {   // from Ben's-6/13/14
  xData = nullData;  /// Clear X and Y data to redraw chart
  yData = nullY;
  xData[0] = 0;  //shows up in the final graph when in SerialRead.
}
getParams();    // get paramaters from text fields (text field programs)
    
 //////// serialPort.write writes to microcontroller to begin run //////////////////
delay(100);  // added from Ben's work on reset
serialPort.write("&");
delay(100);

if (runMode=="RAMP") {
  serialPort.write("000000");  
  println(0);
}
else if (runMode=="CV"){
    serialPort.write("000001");  
  println(1);
}
else if (runMode=="ASV") {
  serialPort.write("000002");  
  println(0);
}
else if (runMode=="logASV") {
    serialPort.write("000003");  
  println(2);
}
else if (runMode=="dif_Pulse") {
  serialPort.write("000004");  
  println(0);
}
else if (runMode=="ChronoAmp") {
    serialPort.write("000005");  
  println(2);
}
else if (runMode=="ChronoAmp2") {
    serialPort.write("000006");  
  println(2);
}
else if (runMode=="norm_Pulse") {
    serialPort.write("000007");  
  println(2);
}
else {}
    
      delay(100);
      serialPort.write(vInit);
      delay(100);
      serialPort.write(vFinal);
      delay(100);
      serialPort.write(scanRate);
      delay(100);
      serialPort.write(cGain);
      delay(100);      
      serialPort.write(vOffset);
      delay(100);
      serialPort.write(initialDelay);
      delay(100);
      serialPort.write(nRuns);
      delay(100);
      serialPort.write(logIvl);
      delay(100);
      serialPort.write(del2);
      delay(100);

      println(vInit);
      println(vFinal);
      println(scanRate);
      println(cGain);
      println(vOffset);
      println(initialDelay);
      println(nRuns);
      println(logIvl);
      println(del2);

      p=0;                    // reset counter for serial read
      println("begin run");   // shows up in bottom window

      logData(file1, "", false);     // log data to file 1, do not append, start new file
      
      ////////read parameter input until LaunchPad transmits '&'/////////
      while (cData!='&' && cData !='@')       // '&' character signifies parameters received
                                              // '@' character signifies ?? 
      {         
          if (serialPort.available () <= 0) {}
          if (serialPort.available() > 0)
          {
            cData =  serialPort.readChar();     // cData is character read from serial comm. port
            sData2 = str(cData);            //sData2  is string of cData 
            logData(file1, sData2, true);   // at this point we are logging the parameters
            println(sData2);
            errorText.setText(""); 
            if (cData == '&')               //  Launchpad sends & char at end of serial write
            {
              println("parameters received");
              gotparams = true;
              logData(file1,"\r\n",true);  // added 6/13-from Ben, what does this do?
           }
          }
      }  // end of while loop with params
  } // end if gotparam == false   Nov 19 BH
    
       //////////// graph data //////////////////////////////////////////////
  
          read_serial();
      }  // end of "if run == true" loop
        if (xData.length>4 && xData.length==yData.length)
        {
    //      lineChart.setMaxX(max(xData));   //  changed to have 'subset', as below-why?
          lineChart.setMaxX(max(subset(xData,1)));   
          lineChart.setMaxY(max(subset(yData,1)));
          lineChart.setMinX(min(subset(xData,1)));
          lineChart.setMinY(min(subset(yData,1)));
          lineChart.setData(subset(xData,1), subset(yData, 1));

        } // End of if (V.length stuff
  if(run==true && comList.length == 0){
    run = false;
    Comselected = false;
    myTextarea2.setText("No COM");
    println("comm not connected");
  }
    } /// end of while (cData not @) loop




 

/////////////////////////////Bang's///////////////////////////////////////////////////////////
public void setup_bangs() {
  cp5 = new ControlP5(this);
  PImage[] imgs = {loadImage("run_button1.png"),loadImage("run_button2.png"),loadImage("run_button3.png")};
  cp5.addBang("Start_Run")
    .setColorBackground(0xffFFFEFC)//#FFFEFC 
        .setColorCaptionLabel(0xff030302) //#030302
          .setColorForeground(0xffAA8A16)
          .setColorActive(0xff06CB49)
            .setPosition(40, 430)
            .setImages(imgs)      // new
              .setSize(40, 40)
                .setTriggerEvent(Bang.RELEASE)
//                  .setLabel("Run / Stop") //"Start Run"
                    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
                      ;

  cp5.addBang("Connect")
    .setColorBackground(0xffFFFEFC) 
        .setColorCaptionLabel(0xff030302) 
          .setColorForeground(0xffAA8A16)  
          .setPosition(100, 8)
            .setSize(40, 20)
              .setTriggerEvent(Bang.RELEASE)
                .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  //.setLabel("Start_Run")
                  ;

  cp5.addBang("Save_run")
    .setColorBackground(0xffFFFEFC)//#FFFEFC 
        .setColorCaptionLabel(0xff030302) //#030302
          .setColorForeground(0xffAA8A16)  
          .setPosition(600, 10)    // was 450
            .setSize(80, 20)
              .setTriggerEvent(Bang.RELEASE)
                .setLabel("Save Run")
                  .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  //.setLabel("Start_Run")
                    ;
     
 /* cp5.addBang("Reset")
    .setColorBackground(#FFFEFC) 
        .setColorCaptionLabel(#030302) 
          .setColorForeground(#AA8A16)  
          .setPosition(600, 40)
            .setSize(80, 20)
              .setTriggerEvent(Bang.RELEASE)
                .setLabel("Reset")
                .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  //.setLabel("Start_Run")
                  ;*/
}
/////////////////////bang programs ////////////////////////////////////////

public void Connect() {             // conect to com port bang
//   if(Comselected==false){
// try{
  serialPort = new Serial(this, comList[Ss], 9600);
  println(comList[Ss]);
  myTextarea2.setText("CONNECTED");
  Comselected = true;
// }
/* catch (Exception e){
   warning.show();
   warningtxt.setText("Some type of com port error");
   println("Some type of com port error. Restart program");
   myTextarea2.setText("COM ERROR");
 }
  }
   else{
   println("already connected");
   }*/   }


/////Start bang ///////////////////
public void Start_Run() {  // start run bang
  if(run == false) {run = true;
  myTextarea2.setColor(0xffD8070E);
  myTextarea2.setText("RUNNING SCAN");
  }
  else {
  serialPort.write('%');
  println("Attempt to reset LP");
                  run = false;    // stops program
                  println("end the madness");
                  gotparams = false;
                  myTextarea2.setColor(0xff036C09);
                  myTextarea2.setText("FINISHED");
                  cData = 'a';   
  }
}

public void Save_run() {             // set path bang   
  selectInput("Select a file to process:", "fileSelected");
}

public void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } 
  else {
    file2 = selection.getAbsolutePath();
    println("User selected " + file2);
   // myTextarea.setText(file2);
      ///////////////////////////////////////
  //  String file2 = "C:/Users/Ben/Documents/Voltammetry Stuff/log/data.txt";
  try{
  saveStream(file2,file1);
      }
      catch(Exception e){}
/////////////////////////////////////////
  }
} 

/*public void Reset() {
  serialPort.write('%');
  println("Attempt to reset LP");
                  run = false;    // stops program
                  println("end the madness");
                  gotparams = false;
                  myTextarea2.setColor(#036C09);
                  myTextarea2.setText("FINISHED");
                  cData = 'a';   
            // cData = '&', prevents program from moving out of read parameters mode
//  sData3 = null;
}*/


//Controllers tab
// controllers setup


public void cp5_controllers_setup(){
 ////////////////////////////////////////////////Text Fields//////////////////////////////
  cp5 = new ControlP5(this);  
  PFont font = createFont("arial", 20);
  PFont font2 = createFont("arial", 16);
  PFont font3 = createFont("arial",12); 
  
  
  Starting_Voltage = cp5.addTextfield("Starting_Voltage")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6)//(#FFFEFC) 
        .setColorForeground(0xffAA8A16) 
         .setPosition(20, 80)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                     .setText("-400");
                      controlP5.Label svl = Starting_Voltage.captionLabel(); 
                        svl.setFont(font2);
                          svl.toUpperCase(false);
                            svl.setText("Initial");
  ;

  
  End_Voltage = cp5.addTextfield("End_Voltage")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(100, 80)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("400");
                      controlP5.Label evl = End_Voltage.captionLabel(); 
                        evl.setFont(font2);
                          evl.toUpperCase(false);
                            evl.setText("Final");
  ;

  Scan_Rate = cp5.addTextfield("Scan_Rate")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
           .setPosition(100, 266)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("100");
                      controlP5.Label srl = Scan_Rate.captionLabel(); 
                        srl.setFont(font2);
                          srl.toUpperCase(false);
                           srl.setText("mV/sec");
   ;
   delay2 = cp5.addTextfield("delay2")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
           .setPosition(100, 266)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("500");
                      controlP5.Label dl2 = delay2.captionLabel(); 
                        dl2.setFont(font2);
                          dl2.toUpperCase(false);
                           dl2.setText("mSec");
   ;
    Gain = cp5.addTextfield("Gain")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
           .setPosition(20, 173) 
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("10");
                      controlP5.Label gain = Gain.captionLabel(); 
                        gain.setFont(font2);
                          gain.toUpperCase(false);
                            gain.setText("Gain");
  ;
  offset = cp5.addTextfield("offset")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16)  //position next
           .setPosition(100, 173)       

            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("0");
                      controlP5.Label oLb = offset.captionLabel(); 
                        oLb.setFont(font2);
                          oLb.toUpperCase(false);
                            oLb.setText("Offset");
  ;

  Delay_Time = cp5.addTextfield("Delay_Time")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(20, 266)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                    .setText("2");
                      controlP5.Label dtl = Delay_Time.captionLabel(); 
                        dtl.setFont(font2);
                          dtl.toUpperCase(false);
                            dtl.setText("seconds");                    
  ;

  
    Number_of_Runs = cp5.addTextfield("Number_of_Runs")  // time based txt field
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(20, 360)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                    .setText("3");
                      controlP5.Label norl = Number_of_Runs.captionLabel(); 
                        norl.setFont(font2);
                          norl.toUpperCase(false);
                            norl.setText("Number");                    
  ;
  
    Run_Interval = cp5.addTextfield("Run_Interval")  // time based txt field
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(100, 360)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("1");
                      controlP5.Label ril = Run_Interval.captionLabel(); 
                        ril.setFont(font2);
                          ril.toUpperCase(false);
                            ril.setText("Delay Min");                    
  ;

  ///////////////////////////////////////text area//////////////////////////

  errorText = cp5.addTextarea("txt")  // save path text area
    .setPosition(350, 5) // was 280,5
      .setSize(240, 45)
        .setFont(font)      // was font 4
          .setLineHeight(20)
            .setColor(0xffFF9100)        //(#D60202)
              .setColorBackground(0)         //(#CEC6C6)
                .setColorForeground(0xffAA8A16)//#CEC6C6
                    ;  

 myTextarea2 = cp5.addTextarea("txt2")  // status and com port text area
    .setPosition(150, 8)
      .setSize(100, 20)   //was 30
        .setFont(createFont("arial", 12)) //(font)
          .setLineHeight(10)
            .setColor(0xff030302)
              .setColorBackground(0xffCEC6C6)
                .setColorForeground(0xffAA8A16)//#CEC6C6
                    ;

// cp5 = new ControlP5(this);

/*PImage[] imgs = {loadImage("run_button1.png"),loadImage("run_button2.png"),loadImage("run_button3.png")};
//PImage[] img2 = {loadImage("stop_button1.png"),loadImage("stop_button2.png"),loadImage("stop_button3.png")};
  cp5.addButton("play")
     .setValue(128)
     .setPosition(50,450)
     .setImages(imgs)
     .updateSize()
     ;*/


/******************* end cp5_controllers-setup ***********************/


 /////////////////////////////////////////Dropdownlist//////////////////////////
  ports = cp5.addDropdownList("list-1", 10, 30, 80, 84)
    .setBackgroundColor(color(200))
      .setItemHeight(20)    // was 20
        .setBarHeight(20) 
          .setColorBackground(color(60))
            .setColorActive(color(255, 128))
              .setUpdate(true)
                ;
  ports.captionLabel().set("Select Port");
  ports.captionLabel().style().marginTop = 3;
  ports.captionLabel().style().marginLeft = 3;
  ports.valueLabel().style().marginTop = 3;
  comList = serialPort.list(); 
  for (int i=0; i< comList.length; i++)
  {
    ports.addItem(comList[i], i);
  }  

//}
ovrLy = cp5.addDropdownList("list-3", 200, 60, 80, 64)  // last digit was 84
    .setBackgroundColor(color(200))
      .setItemHeight(20)
          .setBarHeight(20)
          .setColorBackground(color(60))
            .setColorActive(color(255, 128))
              .setUpdate(true)
                ;
  ovrLy.captionLabel().set("No_Overlay");
  ovrLy.captionLabel().style().marginTop = 3;
  ovrLy.captionLabel().style().marginLeft = 3;
  ovrLy.valueLabel().style().marginTop = 3;
  ovrLy.setScrollbarWidth(10);

  ovrLy.addItem("no_overlay",0);
  ovrLy.addItem("overlay", 1);

 ///////////// mode dropdown list /////////////////////////////
  mode = cp5.addDropdownList("list-2", 260, 30, 80, 184)  // last digit was 124
    .setBackgroundColor(color(200))
      .setItemHeight(20)
          .setBarHeight(20)
          .setColorBackground(color(60))
            .setColorActive(color(255, 128))
              .setUpdate(true)
                ;
  mode.captionLabel().set("Select Mode");
  mode.captionLabel().style().marginTop = 3;
  mode.captionLabel().style().marginLeft = 3;
  mode.valueLabel().style().marginTop = 3;
  mode.setScrollbarWidth(10);

  mode.addItem("RAMP",0);
  mode.addItem("CV", 1);
  mode.addItem("dif_Pulse", 2);
  mode.addItem("ASV", 3);
  mode.addItem("logASV",4);
  mode.addItem("ChronoAmp",5);
  mode.addItem("ChronoAmp2",6);
  mode.addItem("norm_Pulse",7);

}

/////////////////////////////////////////////////group programs/////////////////////////////////

public void controlEvent(ControlEvent theEvent) {
  if (theEvent.isGroup()) 
  {
    if (theEvent.name().equals("list-1")) {

      float S = theEvent.group().value();
      Ss = PApplet.parseInt(S);
      Comselected = true;
    }
    if (theEvent.name().equals("list-2")) {
      float Mod = theEvent.group().value(); 
      int Modi = PApplet.parseInt(Mod);
      String [][] Modetype = mode.getListBoxItems(); 
      //Modetorun = Modetype[Modi][Modi];
      runMode = Modetype[Modi][0]; // replaced earlier line in newer sketch?
      Modesel = true;
      println(runMode);
    }
    if (theEvent.name().equals("list-3")) {
      float ovr = theEvent.group().value(); 
      overlay = PApplet.parseInt(ovr);
//      String [][] Modetype = mode.getListBoxItems(); 
//      runMode = Modetype[Modi][0]; // replaced earlier line in newer sketch?
//      Modesel = true;
//      println(runMode);
    }
  }
}

public void charts_gic_setup(){
  
              ////////////////////////////////gicentre charts///
  lineChart = new XYChart(this);
  lineChart.setData(new float[] {1, 2, 3}, new float[] {1, 2, 3});
  lineChart.showXAxis(true); 
  lineChart.showYAxis(true);
 // lineChart.setXAxisLabelColour(color(234, 28, 28));  
//  fill(#DEC507);    
//  lineChart.setXAxisLabel("Potential (mV)");
//  lineChart.setYAxisLabel("Current Response (of 3287)"); 
  //lineChart.setMinY(0);   
  lineChart.setYFormat("##.##");  
  lineChart.setXFormat("##.##");       
  // Symbol colours
  lineChart.setPointColour(color(234, 28, 28));
  lineChart.setPointSize(5);
  lineChart.setLineWidth(2);

 
}

////////////////////////////////////////////////end charts_gic_setup///////////////////////////////////////////////
public void logData( String fileName, String newData, boolean appendData)  
{
  BufferedWriter bw=null;
  try {                            //try to open the file
    FileWriter fw = new FileWriter(fileName, appendData);
    bw = new BufferedWriter(fw);
    bw.write(newData);
  } 
  catch (IOException e) {
  } 
  finally {
    if (bw != null) { //if file was opened try to close
      try {
        bw.close();
      } 
      catch (IOException e) {
      }
    }
  }
}

// SerialRead tab.  
// WheeStat5_4 GUI
//  May 26, 2014-work on gain and offset adjustments to GUI
//  May 29, moved to Energia; lines 8,22, 27 changed

public void  read_serial() {
      if (serialPort.available () <= 0) {}
      if (serialPort.available() > 0) { 
        sData3 = serialPort.readStringUntil(LINE_FEED);  // new JS11/22
     
         if(sData3 != null && p != 0) {            //p = reset counter
           String[] tokens = sData3.split(",");
           tokens = trim(tokens);  
             if (run == true)  {  
                if (runMode == "ASV" || runMode == "logASV" || runMode == "dif_Pulse") {
                  xRead = Float.parseFloat(tokens[0]);  
                  yRead1 = Float.parseFloat(tokens[1]);  
                  yRead2 = Float.parseFloat(tokens[2]); 
                  yRead = (yRead1 - yRead2);
                  }
                else {    // for RAMP and CV experiments
                  xRead = Float.parseFloat(tokens[0]);  
                  yRead = (Float.parseFloat(tokens[1]));  // had offset, gainK and factor of 1000  
                  }
                if (xRead == 99999)  {  // signals end of run
       //       if (xRead == 99999  && yRead == 99999) { // signals end of run
                  run = false;    // stops program
                  println("end the madness");
                  gotparams = false;
                  myTextarea2.setColor(0xff036C09);
                  myTextarea2.setText("FINISHED");
                  cData = 'a';   
/*                  error = error1 + error2;
                  if (error == 1){
                     errorText.setText("ERROR: I-max Too Hi, Decrease Offset");   
                     }
                  else if (error == 2){
                     errorText.setText("ERROR: I-min Too Low, Increase Offset");   
                     }
                  else if (error == 3){
                     errorText.setText("Scale Error: Decrease Gain");   
                     }
                  error1 = 0;
                  error2 = 0;
                  error = 0;
   */
                  xRead = 0;  
                  yRead = 0;
               }  // end of if xRead = 99999 

             else if(xRead == 55555)  // start of log run
               {
               println("new run");
               myTextarea2.setColor(0xff036C09);
               myTextarea2.setText("run-"+(yRead1));
               }
             else if(xRead == 11111)  // read error message
               {
               print("error flag ");
               println(yRead);
                   if (yRead == 1){
                     errorText.setText("ERROR: I-max Too Hi, Decrease Offset");   
                     }
                  else if (yRead == 2){
                     errorText.setText("ERROR: I-min Too Low, Increase Offset");   
                     }
                  else if (yRead == 3){
                     errorText.setText("Scale Error: Decrease Gain");   
                     }
               }
        
             else if (xData[0] == 0) 
               {
               delay(10);
               xData[0] = xRead;
               yData[0] = yRead; 
         println(xRead);
         println(yRead1);
         println(yRead2);
         println(yRead);
         println(xData[0]);
         println(yData[0]);
               }
             else {  
               xData = append(xData, xRead);
               yData = append(yData, yRead);
               logData(file1, sData3, true);
               }   
 /*            if (yRead2 > yMax && run == true)  {
               error1 = 1; //  
               println (yRead2);
               }
             if (yRead1 !=0 && yRead1 < yMin && run == true && xRead != 55555)  {
               error2 = 2;
               println (yRead1);
               }*/
         }
     }
         p +=1;
    } // end of if serial available > 0
  }


/*public void play() {

//Go button
if (run == false){
  run = true;
  println("runnning");  // what?
  myTextarea2.setColor(#D8070E);
  myTextarea2.setText("Running SCAN");
}
else {
  //Stop button
 //   serialPort.write('%');  // this messes up program if in the button program
  println("Attempt to reset LP");
                  run = false;    // stops program
                  println("end the madness");
                  gotparams = false;
                  myTextarea2.setColor(#036C09);
                  myTextarea2.setText("FINISHED");
                  cData = 'a';   
    }
}*/
/******************* begin text field programs ***********************/

//public void Starting_Voltage() {              //get start voltage from text box
public void getParams() {  
  //public void getVoltages() {  
  vInit = cp5.get(Textfield.class, "Starting_Voltage").getText();
  iInit = round(PApplet.parseFloat(vInit));
  iInit=iInit+2000;                // changed to 2000 from ldo
  vInit = nf(iInit, 6);   // Pad with zero if to 6 digits
//}
//public void End_Voltage() {               // get end voltage from text box
  vFinal = cp5.get(Textfield.class, "End_Voltage").getText();
  iFinal = round(PApplet.parseFloat(vFinal));
  iFinal=iFinal+2000;
  vFinal = nf(iFinal, 6);   // pad with zeros to 6 digits

  cGain = cp5.get(Textfield.class, "Gain").getText();
 // iGain = round(float(cGain)+5);       // changed to +5 from added 128 makes range from -128 to +128
  iGain = round(PApplet.parseFloat(cGain));       
  if (iGain <= -1) {
    iGain = 0;
  }
  if (iGain >= 31) {
    iGain = 30;
  }
  cGain = nf(iGain, 6);   // pad with zeros to 6 digits

  vOffset = cp5.get(Textfield.class, "offset").getText();
  iOffset = round(PApplet.parseFloat(vOffset))+165; //512;
  vOffset = nf(iOffset, 6);   // pad with zeros to 6 digits
//}
//public void Scan_Rate() {                 // get scan rate from text box
  scanRate = cp5.get(Textfield.class, "Scan_Rate").getText();
  iRate = round(PApplet.parseFloat(scanRate));
  scanRate = nf(iRate, 6);   // pad with zeros to 6 digits
//}
//public void Delay_Time() {                // get delay time from text box
  initialDelay = cp5.get(Textfield.class, "Delay_Time").getText();
  iDelay = round(PApplet.parseFloat(initialDelay));
  initialDelay = nf(iDelay, 6);   // pad with zeros to 6 digits
//}
//public void getLogParams() {  
  //public void Number_of_Runs() {                // get run count from text box
  nRuns = cp5.get(Textfield.class, "Number_of_Runs").getText();
  iRuns = round(PApplet.parseFloat(nRuns));
  nRuns = nf(iRuns, 6);   // Pad with zeros to 6 digits

//public void Run_Interval() {                // get delay time from text box
  logIvl = cp5.get(Textfield.class, "Run_Interval").getText();
  iIvl = round(PApplet.parseFloat(logIvl));
  logIvl = nf(iIvl, 6);   // pad with zeros to 6 digits

  del2 = cp5.get(Textfield.class, "delay2").getText();
  iDl2 = round(PApplet.parseFloat(del2));
  del2 = nf(iDl2, 6);   // Pad with zero to 6 digits
}
/************* parameters for retrieving values from text fields
String vInit;          
String vFinal;
String scanRate;
String initialDelay;
String nRuns
String logIvl
int iInit;
int iFinal;
int iRate;
int iDelay;
int iRuns;
int iIvl;
*/
/*public void InitialV_Time() {                 // get scan rate from text box
  InitVT = cp5.get(Textfield.class, "InitialV_Time").getText();
  iInitVT = round(float(InitVT));
  InitVT = nf(iInitVT, 6);   // make ScanR have 3 digits. pad with zero if no digits
}
public void FinalV_Time() {                // get delay time from text box
  FnlVT = cp5.get(Textfield.class, "FinalV_Time").getText();
  iFnlVT = round(float(FnlVT));
  FnlVT = nf(iFnlVT, 6);   // make DelayT have 3 digits. pad with zero if no digits
}*/
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "WheeStat5_4" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
