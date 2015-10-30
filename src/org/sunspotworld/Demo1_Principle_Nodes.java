/*
 * Demo1_Principle_Nodes.java
 *
 * Created on Sep 10, 2015 5:33:25 PM;
 */

package org.sunspotworld;

//import com.sun.spot.io.j2me.tinyos.TinyOSRadioConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
//import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
//import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;  // Utils.sleep();

//import javax.microedition.io.Connector;
//import javax.microedition.io.Datagram;
//import javax.microedition.io.DatagramConnection;
//import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
//import com.sun.spot.peripheral.IAT91_TC;
//import com.sun.spot.peripheral.Spot;
//import com.sun.spot.peripheral.TimerCounterBits;
import java.io.IOException;

import com.sun.spot.resources.transducers.ITemperatureInput;
import com.sun.spot.resources.transducers.ILightSensor;
import java.util.Vector;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class Demo1_Principle_Nodes extends MIDlet {

  // tools
  public final ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
  private final ISwitch sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
  private final ISwitch sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");
  private ITemperatureInput tsensor = (ITemperatureInput) Resources.lookup(ITemperatureInput.class);
  private ILightSensor lsensor = (ILightSensor) Resources.lookup(ILightSensor.class);  
  // constants
  private final int BROADCAST_CONSTANT = 0xFF;
  private final String BROADCAST_ID = "0014.4F01.0000.FFFF";
  
  
  // setup values
  // specific nodes
  private Tiny_connection_pri rx_broadcast = null;
  private Tiny_connection_pri tx_connection = null;
  
  private String ss_id = null;
  private int cluster_no = 0; 
  private int ss_index = -1;
  private String[] telosb_nodes = null;
  private String telosb_up_right = null;
  private String telosb_down = null;
  private String telosb_right_side = null;
  // storage values & resettable values
  private int update_period_dis = -1;
  private int update_period_cen = -1;
  public static Vector current_values = new Vector(Constants.TOTAL_MOTES);
  // local reading values
  // sensor_type = 2 -> light sensor
  //             = 3 -> temp10 sensor
  //             = 4 -> light and temp10 sensors
  // this is the sensor we expect to receive
  private int sensor_type = -1;
  private int[] telosb_temp_reading = null;
  private int[] telosb_light_reading = null;
  private int ss_temp_reading = -1;
  private int ss_light_reading = -1;
  private Thread thread_update = null;
  private String BASE_STATION_ID = null;
  // below node reading values
  // S:0x7EBA uses to keep S:0x79A3's values
  // S:0x7F45 uses to keep other threee nodes' values
  // 8 elements for the cluster_no 0, 2, 3
  private int[] other_temp_reading = null;
  private int[] other_light_reading = null;
  private boolean rx_pck1_setup = false;
  
  private boolean tx_busy = false;
  private int cl1_slabfile_count = 0;
  private DistSlabfile[] cl1_slabfile = new DistSlabfile[2];
          
  protected void startApp() throws MIDletStateChangeException {
    //BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
    
    //setting all node values to 0 first
    for(int i=0; i<Constants.TOTAL_MOTES; i++){
        current_values.addElement(new Short((short)0));
        DistributedMaxRS.currentValues.addElement(new Short((short)0));
    }
    DistributedMaxRS.area = new Area(Constants.AREA_WIDTH, Constants.AREA_HEIGHT);
    System.out.println("*** Start up the PRINCIPLE CODE sun spot ***");
    long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    System.out.println("*** Node ID S:0x" + IEEEAddress.toDottedHex(ourAddr) + " ***");
    Constants.setAddresIDMapping();
    setup(IEEEAddress.toDottedHex(ourAddr));
    leds.getLED(1).setRGB(0, 100, 0);
    leds.getLED(2).setRGB(0, 100, 0);    
    leds.getLED(0).setRGB(0,100,0);  // set color to moderate red
    leds.getLED(0).setOn();
    
    //reset();                       // only the base station will reset all nodes
    
    receive_node_data();
    
    notifyDestroyed();               // cause the MIDlet to exit
  }

  protected void setup(String id)
  {
    this.ss_id = new String(id);
    ss_index = (int) Constants.getNodeId(id).shortValue();
    
    if(id.equals("0014.4F01.0000.7EBA"))
    {
      cluster_no = 0;
      telosb_nodes = new String[] {"0000", "0101",
                                   "0202", "0306",
                                   "0407", "0510",
                                   "0611", "0712"};
      telosb_up_right = "0202";
      // telosb_down = "0712"; unused variables
    } else if(id.equals("0014.4F01.0000.7F45"))
    {
      cluster_no = 1;
      telosb_nodes = new String[] {"1003", "1104",
                                   "1205", "1308",
                                   "1409", "1513",
                                   "1614", "1715"};
      telosb_up_right = "1205";
      // telosb_down = "1715"; unused variables
    } else if(id.equals("0014.4F01.0000.79A3"))
    {
      cluster_no = 2;
      telosb_nodes = new String[] {"2016", "2117",
                                   "2218", "2322",
                                   "2423", "2526",
                                   "2627", "2728"};
      telosb_up_right = "2218";
      telosb_right_side = "2728";
    } else if(id.equals("0014.4F01.0000.7997"))
    {
      cluster_no = 3;
      telosb_nodes = new String[] {"3019", "3120",
                                   "3221", "3324",
                                   "3425", "3529",
                                   "3630", "3731"};
      telosb_up_right = "3221";
    }

    // #@debug info
    System.out.print("*** Gr No: " + cluster_no + " T Nodes: ");
    for(int i = 0; i < telosb_nodes.length; i++)
      System.out.print(telosb_nodes[i] + ", ");
    System.out.println(" |");

    if(telosb_up_right != null)
      System.out.println("T up right: " + telosb_up_right);

    if(telosb_down != null)
      System.out.println("T down: " + telosb_down);
  }

  protected void reset()
  {
    System.out.println("Sending the reset message to telosbs");
    Tiny_connection_pri tiny_reset = new Tiny_connection_pri(null, 
                                                             Constants.CONNECTION_PORT, 
                                                             telosb_nodes);
    tiny_reset.send_reset();
    tiny_reset.close();
    tiny_reset = null;
  }
  
  protected void receive_node_data()
  {
    // new Thread() {
    //   public void run() {
    rx_broadcast = new Tiny_connection_pri(null, Constants.CONNECTION_PORT, telosb_nodes);
    
    while(true)
    {
      System.out.print("\n\nReceive_data function ");
      Rx_package pck_rx = null;
      do {
        System.out.println("Waiting for a new pck");
        pck_rx = rx_broadcast.receive();
      } while (pck_rx == null);
      
      System.out.print("pck_type type: " + pck_rx.get_pck_type() + " -> ");
      int rx_new_pck_type = pck_rx.get_pck_type();
      if((pck_rx.get_pck_type() == 0) && (pck_rx.get_payload()[0] == BROADCAST_CONSTANT))
      { // it will turn off the sun sport and reset itself
        // automatically
        reset_all_setup_values();
        // get of the while loop and reset itself
      } else if(pck_rx.get_pck_type() == 15)
      { // the setup package
        // check the package type to respond
        // forward the setup data to other telosbs
        System.out.println("setup + sensor_type: " + pck_rx.get_payload()[2] + " + broadcast");
        Tiny_connection_pri tx_broadcast_pck1 = new Tiny_connection_pri(Constants.BROADCAST_ID, 
                                                                        Constants.CONNECTION_PORT, 
                                                                        telosb_nodes);        
        tx_broadcast_pck1.send(15, pck_rx, null);
        tx_broadcast_pck1.close();
        System.out.println("Reset the old setup");
        reset_all_setup_values();
        switch(pck_rx.get_payload()[2])
        {
          case 2:
            // prepare to receive the light reading
            System.out.println("Prepare to receive the light reading");
            telosb_light_reading = new int[8];
          
            if(cluster_no == 1)
              other_light_reading = new int[24];
            
            leds.getLED(7).setRGB(255, 255, 0); // yellow
            leds.getLED(7).setOn();
            break;
              
          case 3:
            // prepare to receive the temperature reading
            System.out.println("Prepare to receive the temp reading");              
            telosb_temp_reading = new int[8];
          
            if(cluster_no == 1)
              other_temp_reading = new int[24];
            
            leds.getLED(7).setRGB(0, 0, 204); // blue
            leds.getLED(7).setOn();            
            break;
        } // switch(sensor_type)
        DistributedMaxRS.coverage = new Area(pck_rx.get_payload()[3], pck_rx.get_payload()[4]);
        // update_period of both algorithms are the same
        update_period_dis = pck_rx.get_payload()[1] / 2;
        update_period_cen = update_period_dis;
        // pck_rx.set_payload(0, pck_rx.get_payload()[0]);
        System.out.println("delay_sec: " + pck_rx.get_payload()[1]);
        // set up how to send the data back to the base station 
        // only when we receive the setup package for the first time
        rx_pck1_setup = true;
        BASE_STATION_ID = "0014.4F01.0000." + Integer.toHexString(pck_rx.get_payload()[0]).toUpperCase();
        System.out.println("Base station: " + BASE_STATION_ID);        
        // update the data based on the update period only
        // cluster 2 (0.4 cycle), 3 (0.4 cycle), 1 (1 cycle)
        switch(cluster_no)
        {
          case 2:
            System.out.print("update 5, 13 + ");
            // update the package 5
            thread_update = new Thread(new Periodic_Update(5, 13), 
                                       "Thread_Update");
            break;

          case 3:
            System.out.print("update 8 or 9 ");
            // update the package 5
            thread_update = new Thread(new Periodic_Update(), 
                                       "Thread_Update");            
            
            tx_connection = new Tiny_connection_pri(full_addr(telosb_up_right),
                                                    Constants.CONNECTION_PORT, 
                                                    telosb_nodes);
            break;

          case 1:
            System.out.print("update 7 + ");
            // update the package 7
            thread_update = new Thread(new Periodic_Update(), 
                                       "Thread_Update");

            tx_connection = new Tiny_connection_pri(full_addr(telosb_up_right),
                                                    Constants.CONNECTION_PORT, 
                                                    telosb_nodes);            
            break;

          case 0:// cluster 0 -> send the data to T:0x0202
            System.out.println("update only 8 or 9");
            tx_connection = new Tiny_connection_pri(full_addr(telosb_up_right),
                                                    Constants.CONNECTION_PORT, 
                                                    telosb_nodes);
          
            thread_update = new Thread(new Periodic_Update(), 
                                       "Thread_Update");   
            break;
        }
        thread_update.start();
        
        // save the sensor we expect to receive
        sensor_type = pck_rx.get_payload()[2];
        // leds.getLED(1).setRGB(0, 100, 0);
        leds.getLED(1).setOn();
      } else if((rx_new_pck_type == 2) && (sensor_type == rx_new_pck_type))
      { // keep only a temp10 value
        System.out.println("save data");

        current_values.setElementAt(new Short((short)pck_rx.get_payload()[0]), pck_rx.get_node_index());
      } else if((rx_new_pck_type == 3) && (sensor_type == rx_new_pck_type))
      { // keep only a light reading
        System.out.println("save data");        

        current_values.setElementAt(new Short((short)pck_rx.get_payload()[0]), pck_rx.get_node_index());
      } else if(pck_rx.get_pck_type() == 5)
      { // for pck_type1 == 5, data = 8x2 bytes containing only
        // either light or temp10 sensor readings
        System.out.println("Pck_type 5 Received");
        
        if(cluster_no == 0) // this is S:0x7EBA
        { // forward data
          System.out.println("Cluster 0");
          /*switch(sensor_type)
          {
            case 2:
              tx_connection.send(6, pck_rx, telosb_light_reading);
              break;
              
            case 3:
              tx_connection.send(6, pck_rx, telosb_temp_reading);
              break;
          }*/
          DistSlabfile fw_slapfile = DistributedMaxRS.processingC_0(pck_rx.get_slap_file());  //Next Step, Send the result to C-1
          Rx_package fw_pck = new Rx_package(6, 0, full_addr(telosb_up_right), fw_slapfile);
          tx_connection.send(6, fw_pck, null);
          System.out.println("fw Package 6 to T:0x" + telosb_up_right);
        } else // this is S:0x7F45 cluster_no = 1
        { // save the data
          /*switch(sensor_type)
          {
            // arraycopy(Object src, int srcPos, Object dest,
            //           int destPos, int length);
            // Copies an array from the specified source array, beginning at
            // the specified position, to the specified position of
            // the destination array.
            case 2:
              System.arraycopy(pck_rx.get_payload(), 0, other_light_reading, 16,
                               pck_rx.get_payload().length);
              // System.out.println("==== Pck_type 5 Content Cluster 3 =====");
              // for(int i = 16; i < other_light_reading.length; i++)
              //   System.out.println("i: " + (i - 16) + " = " + other_light_reading[i]);
              // System.out.println("==== Pck_type 5 End Cluster 3 =====");              
              break;
            case 3:
              System.arraycopy(pck_rx.get_payload(), 0, other_temp_reading, 16,
                               pck_rx.get_payload().length);
              break;
          }*/
            // #$#$ cluster 1 receives the data from the 0th cluster
          cluster_1_process(1, pck_rx.get_slap_file()); 
          System.out.println("save data");
        }
        
        leds.getLED(2).setOn();        
        
      } else if(pck_rx.get_pck_type() == 6)
      { // this is S:0x7F45
        System.out.println("Pck_type 6 Received");        
        /*switch(sensor_type)
        {
          case 2:
            System.arraycopy(pck_rx.get_payload(), 0, 
                             other_light_reading, 0,
                             pck_rx.get_payload().length);
            System.out.println("==== Pck_type 6 Content Cluster 0 =====");
            for(int i = 0; i < pck_rx.get_payload().length; i++)
            {
              if(i == 8)
                System.out.println("==== Cluster 2 ====");
              System.out.println("i: " + i + " = " + pck_rx.get_payload()[i]);
            }
            System.out.println("==== Pck_type 6 End Cluster 2 =====");            
            break;
          case 3:
            System.arraycopy(pck_rx.get_payload(), 0, 
                             other_temp_reading, 0,
                             pck_rx.get_payload().length);              
            break;
        }*/
        /// #$#$ the 1st cluster receives the data from the 3rd cluster
        cluster_1_process(0, pck_rx.get_slap_file()); 
        System.out.println("save data");
        leds.getLED(3).setRGB(0, 100, 0);
        leds.getLED(3).setOn();                
      } else if(pck_rx.get_pck_type() == 13)
      {
        System.out.println("Pck_type 13 Received val: ");
        //pck_rx.get_dst_addr();
        //pck_rx.get_pck_type();
        DistSlabfile fw_slapfile = DistributedMaxRS.processingC_3(pck_rx.get_slap_file());  //Next Step, Send the result to C-1
        Rx_package fw_pck = new Rx_package(6, 0, full_addr(telosb_up_right), fw_slapfile);
        tx_connection.send(5, fw_pck, null);
        leds.getLED(2).setRGB(0, 100, 0);
        leds.getLED(2).setOn();
      }
    } 
  }
  
  private void cluster_1_process(int index, DistSlabfile sl_new) {
      if(index == 0) {
          System.out.println("save the data slabfile[0]");
          cl1_slabfile[0] = sl_new;
          cl1_slabfile_count++;
      }else if(index == 1) {
          System.out.println("save the data slabfile[1]");
          cl1_slabfile[1] = sl_new;
          cl1_slabfile_count++;
      }
      
      if(cl1_slabfile_count == 2) {
          // #$#$
          System.out.println("processing the slabfile");
          Window opt_wind = DistributedMaxRS.processingC_1(cl1_slabfile[0], cl1_slabfile[1]);
          cl1_slabfile_count = 0;
          
          Rx_package pck_tx = new Rx_package(7, opt_wind);
          
          tx_connection.send(7, pck_tx, null);
      }
  }
  private class Periodic_Update implements Runnable {
    // only the ss from cluster_no 2,  3 and 1 will call this function
    private int pck_type1 = -1;
    private int pck_type2 = -1;
    private Tiny_connection_pri dis_update = null;
    private Tiny_connection_pri dis_update_side_way = null;
    private Tiny_connection_pri cen_update = null;
    
    public Periodic_Update(int type1, int type2) {
      pck_type1 = type1;
      pck_type2 = type2;
      cen_update = new Tiny_connection_pri(BASE_STATION_ID,
                                           Constants.CONNECTION_PORT,
                                           null);
      
      dis_update = new Tiny_connection_pri(full_addr(telosb_up_right),
                                           Constants.CONNECTION_PORT, 
                                           null);
      
      dis_update_side_way = new Tiny_connection_pri(full_addr(telosb_right_side),
                                                    Constants.CONNECTION_PORT,
                                                    null);
    }
    
    public Periodic_Update(int type) {
      pck_type1 = type;
      cen_update = new Tiny_connection_pri(BASE_STATION_ID,
                                           Constants.CONNECTION_PORT,
                                           null);
      dis_update = new Tiny_connection_pri(full_addr(telosb_up_right),
                                           Constants.CONNECTION_PORT, 
                                           null);
    }

    public Periodic_Update() {
      pck_type1 = 8;
      cen_update = new Tiny_connection_pri(BASE_STATION_ID,
                                           Constants.CONNECTION_PORT,
                                           null);
    }
    
    private void update_distributed(int type, DistSlabfile slap_file) {
      Rx_package pck_tx = null;          
      switch(type)
      {
        case 5:
        {
          /*switch(sensor_type)
          {
            case 2:
              pck_tx = new Rx_package(5, 2, null, telosb_light_reading);
              break;
            case 3:
              pck_tx = new Rx_package(5, 2, null, telosb_temp_reading);
              break;
          }*/
          pck_tx = new Rx_package(5, 2, null, slap_file);
          threadMessage("Periodic Update: pck_type 5 sent");
          dis_update.send(5, pck_tx, null);
          break; 
        }

        // case 10:
        // {
        //   int[] temp10 = new int[9];
        //   temp10[0] = 8;
        //   switch(sensor_type)
        //   {
        //     case 2:
        //       System.arraycopy(telosb_light_reading, 0, 
        //                        temp10, 1, telosb_light_reading.length);
        //       pck_tx = new Rx_package(10, 2, null, temp10);
        //       break;
        //     case 3:
        //       System.arraycopy(telosb_temp_reading, 0, 
        //                        temp10, 1, telosb_temp_reading.length);                
        //       pck_tx = new Rx_package(10, 2, null, temp10);
        //       break;
        //   }
        //   System.out.println("Periodic Update: pck_type1 5 sent");
        //   dis_update.send(5, pck_tx, null);
        //   break; 
        // }
            
        case 7:
        {
          switch(sensor_type)
          {
            case 2:
              System.out.println(" case 2");
              pck_tx = new Rx_package(7, 2, null, telosb_light_reading);
              dis_update.send(7, pck_tx, other_light_reading);
              break;
            case 3:
              System.out.println(" case 3");
              pck_tx = new Rx_package(7, 2, null, telosb_temp_reading);
              dis_update.send(7, pck_tx, other_temp_reading);
              break;
          }
          threadMessage("Periodic Update: pck_type 7 sent");  
          break;
        }
            
        // case 12:
        // {
        //   int[] temp12 = new int[24+8+1];
        //   temp12[0] = 12;            
        //   switch(sensor_type)
        //   {
        //     case 2:
        //       // copy the data in cluster 0                
        //       System.arraycopy(other_light_reading, 0, 
        //                        temp12, 1, 8);                  
        //       // copy the data in cluster 1              
        //       System.arraycopy(telosb_light_reading, 0, 
        //                        temp12, 9, telosb_light_reading.length);
        //       // copy the data in cluster 2 and 3              
        //       System.arraycopy(other_light_reading, 0, 
        //                        temp12, 17, other_light_reading.length - 8);              
        //       dis_update.send(7, null, temp12);
        //       break;
        //     case 3:
        //       // copy the data in cluster 0
        //       System.arraycopy(other_temp_reading, 0, 
        //                        temp12, 1, 8);                  
        //       // copy the data in cluster 1
        //       System.arraycopy(telosb_temp_reading, 0, 
        //                        temp12, 9, telosb_temp_reading.length);
        //       // copy the data in cluster 2 and 3
        //       System.arraycopy(other_light_reading, 0, 
        //                        temp12, 17, other_temp_reading.length - 8); 
        //       dis_update.send(7, null, temp12);
        //       break;
        //   }
        //   System.out.print("Periodic Update: pck_type1 12 sent");  
        //   break;    
        // }
        case 13: 
          pck_tx = new Rx_package(5, 2, null, slap_file);
          threadMessage("Periodic Update: pck_type 13 sent");          
          dis_update_side_way.send(13, pck_tx, null);            
          break;
      }          
    } 
      
    private void update_centralized() {
      int[] data = null;
      Rx_package pck_tx = null;
      switch(sensor_type)
      {
        case 2:
          data = new int[1];
          data[0] = ss_light_reading;
          pck_tx = new Rx_package(8, 0, BASE_STATION_ID, data);                
          cen_update.send(8, pck_tx, null); 
          threadMessage("pck 8 sent");
          break;
        case 3:
          data = new int[1];
          data[0] = ss_temp_reading;
          pck_tx = new Rx_package(9, 0, BASE_STATION_ID, data);                
          cen_update.send(9, pck_tx, null);           
          threadMessage("pck 9 sent");
          break;
        case 4:
          //data = new int[2];
          //data[0] = ss_light_reading;
          //data[1] = ss_temp_reading;
          //pck_tx = new Rx_package(9, 0, BASE_STATION_ID, data);                
          //dis_update.send(9, pck_tx, null);           
          break;
      }
    }
    
    public void run() {
      while(true) {
        try {
          if((pck_type1 == 5) || (pck_type1 == 7)) { // cluster 2
            threadMessage("Periodic Update: sleep dis: " 
                          + update_period_cen);
            Thread.sleep(update_period_cen * 1000);
            sensor_reading();
            update_centralized();
          
            threadMessage("Periodic Update: sleep cen: " 
                        + update_period_dis);
            Thread.sleep(update_period_dis * 900);    // do a thread sleep in millisecond

            DistSlabfile[] slab_values_to_sent = DistributedMaxRS.processingC_2();  
            update_distributed(pck_type1, slab_values_to_sent[0]);
            update_distributed(pck_type2, slab_values_to_sent[1]);

            // update_distributed(pck_type1);  // cluster 1, 2       
            
            // System.out.println("done update distributed pck1 send pck2");
            // if(cluster_no == 2) // cluster 2
            //  update_distributed(pck_type2);
            
          } else if(pck_type1 == 8) // cluster 0, 3, 1
          { // only the principle node of the cluster 0, 3 will 
            // call these commands
            // ignore the pck_type1  
            Thread.sleep(update_period_cen * 1900);
            sensor_reading();
            if(!tx_busy)
                update_centralized();
          }
        } catch(InterruptedException ie) {
          if(dis_update != null)
            dis_update.close();
          
          if(dis_update_side_way != null)
            dis_update_side_way.close();
          
          cen_update.close();            
          return;
        }
      }
    } // public void run()
  } 

  protected void reset_all_setup_values() {
    System.out.println("Reset all setup values");    
    sensor_type = -1;
    telosb_temp_reading = null;
    telosb_light_reading = null;
    ss_temp_reading = -1;
    ss_light_reading = -1;
    other_temp_reading = null;
    other_light_reading = null;
    update_period_dis = -1;       
    update_period_cen = -1;
    BASE_STATION_ID = null;    
    leds.getLED(1).setOff();
    leds.getLED(2).setOff();
    leds.getLED(3).setOff();
    leds.getLED(7).setOff();
    cl1_slabfile_count = 0;
    
    if(tx_connection != null)
    {
      tx_connection.close();
      tx_connection = null;
    }
    
    
    if(thread_update != null)
    {
      System.out.println("Interrupt and join the thread");
      if(thread_update.isAlive())
      {
        System.out.println("Thread is alive");  
        thread_update.interrupt();
        try {
          thread_update.join();
          thread_update = null;
        } catch (InterruptedException ie) {
          System.out.println("reset fn: thread join interrupt exception");
        }
      }
    }
  }
  
  protected void sensor_reading() {
    // local reading values
    // sensor_type = 2 -> light sensor
    //             = 3 -> temp10 sensor
    //             = 4 -> light and temp10 sensors
    // this is the sensor we expect to receive      
    switch(sensor_type)
    {
      case 2:
        ss_light_reading = read_light_sensor();
        current_values.setElementAt(new Short((short) ss_light_reading), ss_index);
        break;
      case 3:
        ss_temp_reading = read_temp_sensor_f();
        current_values.setElementAt(new Short((short) ss_temp_reading), ss_index);
        break;
    }
  }
  
  protected String full_addr(String telosb_id) {
    return "0014.4F01.0000." + telosb_id;
  }
   
  protected void pauseApp() {
    // This is not currently called by the Squawk VM
  }

  public int read_temp_sensor_c() {
    double temp_reading_c = 0;
    try 
    {
      System.out.print("--- Read the Temperature Sensor C: ");
      temp_reading_c = tsensor.getCelsius();
      System.out.println(temp_reading_c + " ---");
    } catch (IOException e) {
      System.out.println("Cannot read the temp sensor C");
      e.printStackTrace();
    }                
    return (int) temp_reading_c;
  }
  
  public int read_temp_sensor_f() {
    double temp_reading_f = 0;
    try 
    {
      System.out.print("--- Read the Temperature Sensor F: ");
      temp_reading_f = tsensor.getFahrenheit();
      System.out.println(temp_reading_f + " ---");
    } catch (IOException e) {
      System.out.println("Cannot read the temp sensor F");
      e.printStackTrace();
    }                
    return (int) temp_reading_f;
  }  
  
  public int read_light_sensor() {
    int light_reading = 0;
    try 
    {
      light_reading = lsensor.getValue();
      System.out.println("--- Read the Light Sensor :" + light_reading + " ---");      
    } catch (IOException e) {
      System.out.println("Cannot read the light sensor");
      e.printStackTrace();
    }                
    return light_reading;
  }   

  static void threadMessage(String message) {
    String thread_name = Thread.currentThread().getName();
    System.out.println("*** T: " + thread_name + ": " + message);
  }
  
/**
 * Called if the MIDlet is terminated by the system.
 * It is not called if MIDlet.notifyDestroyed() was called.
 *
 * @param unconditional If true the MIDlet must cleanup and release all resources.
 */
  protected void destroyApp(boolean unconditional)
    throws MIDletStateChangeException {
    leds.setOff();
  }
}
