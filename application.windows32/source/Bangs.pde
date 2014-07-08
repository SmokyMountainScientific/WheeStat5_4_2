/////////////////////////////Bang's///////////////////////////////////////////////////////////
void setup_bangs() {
  cp5 = new ControlP5(this);
  PImage[] imgs = {loadImage("run_button1.png"),loadImage("run_button2.png"),loadImage("run_button3.png")};
  cp5.addBang("Start_Run")
    .setColorBackground(#FFFEFC)//#FFFEFC 
        .setColorCaptionLabel(#030302) //#030302
          .setColorForeground(#AA8A16)
          .setColorActive(#06CB49)
            .setPosition(40, 430)
            .setImages(imgs)      // new
              .setSize(40, 40)
                .setTriggerEvent(Bang.RELEASE)
//                  .setLabel("Run / Stop") //"Start Run"
                    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
                      ;

  cp5.addBang("Connect")
    .setColorBackground(#FFFEFC) 
        .setColorCaptionLabel(#030302) 
          .setColorForeground(#AA8A16)  
          .setPosition(100, 8)
            .setSize(40, 20)
              .setTriggerEvent(Bang.RELEASE)
                .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  //.setLabel("Start_Run")
                  ;

  cp5.addBang("Save_run")
    .setColorBackground(#FFFEFC)//#FFFEFC 
        .setColorCaptionLabel(#030302) //#030302
          .setColorForeground(#AA8A16)  
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
  myTextarea2.setColor(#D8070E);
  myTextarea2.setText("RUNNING SCAN");
  }
  else {
  serialPort.write('%');
  println("Attempt to reset LP");
                  run = false;    // stops program
                  println("end the madness");
                  gotparams = false;
                  myTextarea2.setColor(#036C09);
                  myTextarea2.setText("FINISHED");
                  cData = 'a';   
  }
}

public void Save_run() {             // set path bang   
  selectInput("Select a file to process:", "fileSelected");
}

void fileSelected(File selection) {
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


