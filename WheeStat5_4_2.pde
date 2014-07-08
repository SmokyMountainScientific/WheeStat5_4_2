/*  WheeStat5_4_2 Processing sketch
 *    7/07/14
 *   GUI for WheeStat 5 series Potentiostats. 
 *    Compatible with WheeStat5_4b or later Energia Code
 *    
 *    by Jack Summers, Ben Hickman 
 *  
 *    Output file in csv format
 *    July 7, 2014, max and min current output transmitted by LaunchPad,
 *                  read to generate limit bar beside graph. 
 *                  Error flags moved to GUI from Energia code
 *    May 19, added chronoamerometry and normal pulse voltammetry modes
 *    
 *    Revision 1_4 improves update speed using readStringUntil(LINE_FEED)
 *    End of run signaled by transmission of "99999,999999\n" from LaunchPad
 */
 
///////////////////////////////////////// Imports///////////////////////////////
import org.gicentre.utils.gui.TextPopup; // for warning window
import org.gicentre.utils.stat.*;    // For chart classes.
//import org.gicentre.utils.multisketch.*; // for integration window
import controlP5.*;
import processing.serial.*;
import java.io.*;                        // this is needed for BufferedWriter
import java.util.Arrays;
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
// stuff for limit bar
int xBarPos = 700;
int yBarPos = 70;
int yBarSz = 400;
int yBarMin;
int yBarMax;
int bWidth = 15;   // bar width
  float hiI = 0;
  float lowI = 0;
int iHiI;
int iLowI;
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
void setup()
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


void draw()
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
  bar();
  stroke(255);
  noFill();
  rect (12,50,160,85);
  rect (12,143,160,85);
  rect (12,236,160,85);
  rect (12,330,160,85);
  pushMatrix();
  textFont(font,12);

  fill(#DEC507);
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
  else if (runMode=="norm_Pulse") {
    text("Delay 1     Pulse time", 20, 256);
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
  
  fill(#EADFC9);               // background color
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
  rotate(3.14159*3/2);
  textAlign(CENTER);
  text("Current  (microamps)", 0, 0);
  rotate(3.14159/2);        // return orientation and location
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
      hiI = 0;
      lowI = 0;
//      hiVal = 0;
  //    lowVal = 0;
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




 

