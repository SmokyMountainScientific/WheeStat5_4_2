// SerialRead tab.  
// WheeStat5_4 GUI
//  May 26, 2014-work on gain and offset adjustments to GUI
//  May 29, moved to Energia; lines 8,22, 27 changed

void  read_serial() {
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
                  myTextarea2.setColor(#036C09);
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
               myTextarea2.setColor(#036C09);
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


