//limitBar tab in WheeSat5_4 Processing sketch
// begun 7/6/14 by JSS
// intent is to show relationship betwen obs current range and limits

void bar() {

   iHiI = int(hiI);  //hiI and lowI set to zero on line 314, first tab
   iLowI = int(lowI);  // should be in mV at this point?    
if(iHiI >=2000) {
  if(iLowI <= -1900) {
   errorText.setText("Scale Error: Decrease Gain");         //
   }
 else {
      errorText.setText("ERROR: I-max Too High, Decrease Offset");         //
 } 
}
  if(iLowI <=-1900 && iHiI <=2000) {
   errorText.setText("ERROR: I-min Too Low, Increase Offset");         //
   }
  int hiVal = (2048-iHiI)*yBarSz/4096; //(1650-iHiI)*yBarSz/3300;// Bar drawn from top = 0 down
  int lowVal = (2048-iLowI)*yBarSz/4096;   
  translate(xBarPos,yBarPos);
for (int i = hiVal; i < lowVal; i++) {
float red;    // = 255-(300*exp(-0.01*i));
float green; //*(1/(1+i);
float blue;  // = 50-(0.5*i);
float rMax = 0.5*yBarSz;  // posn of maximum green intensity 
float gMax = 0.5*yBarSz;  // posn of maximum green intensity 
float bMax = 0.5*yBarSz;  // posn of maximum green intensity 
float rS = 1/yBarSz;   // was 4.8 width parameter for red
float gS = 5.1/yBarSz;   //width parameter for green
float bS = 4.9/yBarSz;   // was 0.8 width parameter for blue
float rD = exp(rS*(i-rMax));
float gD = exp(gS*(i-gMax));
float bD = exp(bS*(i-bMax));
int rA = 800;     // maximum red intensity
int gA = 800;     // maximum green intensity
int bA = 900;     // maximum blue intensity
 red = rA*rD/sq(1+rD);
 green = gA*gD/sq(1+gD);
 blue = bA*bD/sq(1+bD);
  
  stroke(red, green, blue, 250);

  line(0, i, bWidth, i);
  
} 
 ////  Center and limits  /////

 stroke(255,255,255,250);
 line(0,0, bWidth,0);
 translate(0, 0.5*yBarSz); 
 line(0,0, bWidth,0);
 translate(0, 0.5*yBarSz);  
 line(0,0,bWidth,0);
 translate(0,-yBarSz);
 
 translate(-xBarPos, -yBarPos); 
 }

