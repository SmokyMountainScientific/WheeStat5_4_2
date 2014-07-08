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

void controlEvent(ControlEvent theEvent) {
  if (theEvent.isGroup()) 
  {
    if (theEvent.name().equals("list-1")) {

      float S = theEvent.group().value();
      Ss = int(S);
      Comselected = true;
    }
    if (theEvent.name().equals("list-2")) {
      float Mod = theEvent.group().value(); 
      int Modi = int(Mod);
      String [][] Modetype = mode.getListBoxItems(); 
      //Modetorun = Modetype[Modi][Modi];
      runMode = Modetype[Modi][0]; // replaced earlier line in newer sketch?
      Modesel = true;
      println(runMode);
    }
    if (theEvent.name().equals("list-3")) {
      float ovr = theEvent.group().value(); 
      overlay = int(ovr);
//      String [][] Modetype = mode.getListBoxItems(); 
//      runMode = Modetype[Modi][0]; // replaced earlier line in newer sketch?
//      Modesel = true;
//      println(runMode);
    }
  }
}

