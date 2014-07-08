/******************* begin text field programs ***********************/

//public void Starting_Voltage() {              //get start voltage from text box
public void getParams() {  
  //public void getVoltages() {  
  vInit = cp5.get(Textfield.class, "Starting_Voltage").getText();
  iInit = round(float(vInit));
  iInit=iInit+2000;                // changed to 2000 from ldo
  vInit = nf(iInit, 6);   // Pad with zero if to 6 digits
//}
//public void End_Voltage() {               // get end voltage from text box
  vFinal = cp5.get(Textfield.class, "End_Voltage").getText();
  iFinal = round(float(vFinal));
  iFinal=iFinal+2000;
  vFinal = nf(iFinal, 6);   // pad with zeros to 6 digits

  cGain = cp5.get(Textfield.class, "Gain").getText();
 // iGain = round(float(cGain)+5);       // changed to +5 from added 128 makes range from -128 to +128
  iGain = round(float(cGain));       
  if (iGain <= -1) {
    iGain = 0;
  }
  if (iGain >= 31) {
    iGain = 30;
  }
  cGain = nf(iGain, 6);   // pad with zeros to 6 digits

  vOffset = cp5.get(Textfield.class, "offset").getText();
  iOffset = round(float(vOffset))+165; //512;
  vOffset = nf(iOffset, 6);   // pad with zeros to 6 digits
//}
//public void Scan_Rate() {                 // get scan rate from text box
  scanRate = cp5.get(Textfield.class, "Scan_Rate").getText();
  iRate = round(float(scanRate));
  scanRate = nf(iRate, 6);   // pad with zeros to 6 digits
//}
//public void Delay_Time() {                // get delay time from text box
  initialDelay = cp5.get(Textfield.class, "Delay_Time").getText();
  iDelay = round(float(initialDelay));
  initialDelay = nf(iDelay, 6);   // pad with zeros to 6 digits
//}
//public void getLogParams() {  
  //public void Number_of_Runs() {                // get run count from text box
  nRuns = cp5.get(Textfield.class, "Number_of_Runs").getText();
  iRuns = round(float(nRuns));
  nRuns = nf(iRuns, 6);   // Pad with zeros to 6 digits

//public void Run_Interval() {                // get delay time from text box
  logIvl = cp5.get(Textfield.class, "Run_Interval").getText();
  iIvl = round(float(logIvl));
  logIvl = nf(iIvl, 6);   // pad with zeros to 6 digits

  del2 = cp5.get(Textfield.class, "delay2").getText();
  iDl2 = round(float(del2));
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
