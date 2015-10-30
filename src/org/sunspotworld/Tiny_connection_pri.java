/*
 * To change this license header, choose License Headers in Project Properties
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.io.j2me.tinyos.TinyOSRadioConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
/**
 *
 * @author PanitanW
 */

public class Tiny_connection_pri 
{
  private TinyOSRadioConnection tiny_connection = null;
  private boolean is_broadcast = false;
  private Datagram dg = null;
  private String dst_addr = null;
  private int port_no = -1;
  private final int broadcast_constant = 0xFF;
  private String[] telosb_nodes = null;
  
  // constructor
  public Tiny_connection_pri(String dst_addr, int port, String[] nodes) {
    this.dst_addr = dst_addr;
    this.port_no = port;
    this.telosb_nodes = nodes;
    
    if(dst_addr == null)
      open_broadcast(this.port_no);
    else
      open(this.dst_addr, this.port_no);
  }

  public String node_addr_dot_hex() {
    long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    return IEEEAddress.toDottedHex(ourAddr);
  }
  
  public boolean open(String dst_addr, int port) {
    if(tiny_connection == null)
    {
      try
      {
        System.out.println("$TC Open Connection: dst: " + dst_addr + " port: " +
                           String.valueOf(port));
        // broadcast the threshold
        tiny_connection = (TinyOSRadioConnection) Connector.open("tinyos://" + 
                                                                 dst_addr + ":" + port);
        // Then, we ask for a datagram with the maximum size allowed
        dg = tiny_connection.newDatagram(tiny_connection.getMaximumLength());
        return true;
      } catch (IOException e) {
        System.out.println("$TC cannot open tinyos broadcast connection to " + dst_addr);
        e.printStackTrace();
      }
    }
    
    return false;
  }

  public boolean open_broadcast(int port) {
    if(tiny_connection == null)
    {
      try
      {
        System.out.println("$TC Open Broadcast port: " + String.valueOf(port));
        // broadcast the threshold
        tiny_connection = (TinyOSRadioConnection) Connector.open("tinyos://:" + port);
        // Then, we ask for a datagram with the maximum size allowed
        dg = tiny_connection.newDatagram(tiny_connection.getMaximumLength());
        is_broadcast = true;
        return true;
      } catch (IOException e) {
        System.out.println("$TC Could not open tinyos broadcast connection");
        e.printStackTrace();
      }
    } 

    return false;
  }

  public boolean send_reset() {
    if((tiny_connection != null) && (dg != null))
    {
      try {
        // We send the message (UTF encoded)
        dg.reset();
        dg.writeByte(0);
        dg.writeByte(broadcast_constant);
        tiny_connection.send(dg);
        System.out.println("$TC Reset Sent");
        return true;
      } catch (IOException ex) {
        System.out.println("$TC Could not send the reset pck!!!");
        ex.printStackTrace();
      }
    }

    return false;
  }

  public boolean send(int pck_type, Rx_package pck_rx, int[] more_data)
  { // forward the data to telosb    
    if((tiny_connection != null) && (dg != null))
    {
      try {
        // We send the message (UTF encoded)
        dg.reset();
        switch(pck_type)
        {
          case 1:
            // forward the setup data to telosb nodes
            dg.writeByte(1);
            dg.writeByte(pck_rx.get_payload()[0]);
            dg.writeByte(pck_rx.get_payload()[1]);
            break;
            
          case 5:
            dg.writeByte(5);
            //for(int i = 0; i < pck_rx.get_payload().length; i++)
            //  dg.writeShort(pck_rx.get_payload()[i]);
            break;

            // case 10:
            //   dg.writeByte(10);
            //   for(int i = 0; i < pck_rx.get_payload().length; i++)
            //     dg.writeShort(pck_rx.get_payload()[i]);
            //   break;
              
          case 6:
            dg.writeByte(6);
            // if sensor_type = 3, tx_broadcast.send(6, pck_rx, temp_reading);
            /*for(int i = 0; i < more_data.length; i++)
              dg.writeShort(more_data[i]);

            for(int i = 0; i < pck_rx.get_payload().length; i++)
              dg.writeShort(pck_rx.get_payload()[i]);*/
            break;
            
          case 7:
            dg.writeByte(7);
            /*for(int i = 0; i < 8; i++)
              dg.writeShort(more_data[i]); // cluster 0
            for(int i = 0; i < pck_rx.get_payload().length; i++)
              dg.writeShort(pck_rx.get_payload()[i]); // cluster 1
            for(int i = 8; i < more_data.length; i++)
              dg.writeShort(more_data[i]); // cluster 2 3
            */
            break;
            
          case 8:
            dg.writeByte(8);
            dg.writeShort(pck_rx.get_payload()[0]);
            break;
              
          case 9:
            dg.writeByte(9);
            dg.writeShort(pck_rx.get_payload()[0]);
            break;
            
            // case 12:
            //   dg.writeByte(9);
            //   dg.writeByte(41);
            //   for(int i = 0; i < more_data.length; i++)
            //     dg.writeShort(more_data[i]);
            //   break;
          case 13:
            dg.writeByte(13);
            break;
              
          case 15:
            dg.writeByte(15);
            for(int i = 0; i < Constants.VALUE_SIZE; i++)
              dg.writeShort(pck_rx.get_payload()[i]);
            break;
        }
        
        if((pck_type == 5) || (pck_type == 13) || (pck_type == 6)) {
            DistSlabfile sl = pck_rx.get_slap_file();
            for(int i = 0; i < sl.hintervals.size(); i++) {
                dg.writeShort(((Window)sl.hintervals.elementAt(i)).l);
                dg.writeShort(((Window)sl.hintervals.elementAt(i)).r);
                dg.writeShort(((Window)sl.hintervals.elementAt(i)).h);
                dg.writeShort(((Window)sl.hintervals.elementAt(i)).score);
            }
            dg.writeShort(Constants.SEPERATOR);
            for(int j = 0; j < sl.neededValues.size(); j++) {
                dg.writeShort(((Short)sl.neededValues.elementAt(j)).shortValue());
            }
            dg.writeShort(Constants.TERMINATOR);
            
        } else if(pck_type == 7) {
            dg.writeShort(pck_rx.get_window().l);
            dg.writeShort(pck_rx.get_window().r);
            dg.writeShort(pck_rx.get_window().h);
            dg.writeShort(pck_rx.get_window().score);
            dg.writeShort(Constants.TERMINATOR);
        }

        tiny_connection.send(dg);
        System.out.println("$TC Sent Pck type: " + pck_type + " to " + dst_addr);
        return true;
      } catch (IOException ex) {
        System.out.println("$TC cannot send the pck type " + pck_type);
        ex.printStackTrace();
      }
    }

    return false;
  }
  
  public Rx_package receive() {
    Rx_package res = null;
    DistSlabfile sl_rx = null;
    try
    {
      dg.reset();
      System.out.println("$TC: Waiting for the data");
      tiny_connection.receive(dg);             // a blocking call
      String dest_addr = dg.getAddress();
      
      System.out.print("Rx " + dest_addr);
      
      int node_index = within_cluster(dest_addr);
      int pck_type = dg.readByte();
      if((node_index < 0) && (pck_type != 0))
      {
        System.out.println(" Node Index: " + node_index + ", not in the same cluster.");
        return null; // receive from other nodes not in the same cluster
      } else 
        System.out.println(" Node Index: " + node_index);
    
      System.out.println("-- pck_type: " + pck_type);
      if(pck_type == 0)
      {
        int[] data = new int[1];
        data[0] = (int) dg.readByte();
        res = new Rx_package(pck_type, node_index, dest_addr,
                             data);
      } else if(pck_type == 1)
      { // update period and pck type
        int[] temp = new int[2];
        temp[0] = dg.readByte();
        temp[1] = dg.readByte();
        res = new Rx_package(pck_type, node_index, dest_addr,
                             temp);
      } else if((pck_type == 2) || (pck_type == 3))
      { // either light or temp sensor data 
        int[] temp = new int[1];
        temp[0] = dg.readShort();
        node_index = (int) Constants.getNodeId(dest_addr).shortValue();
        res = new Rx_package(pck_type, node_index, dest_addr,
                             temp);
      } /*else if(pck_type == 4)
      { // both temp and light sensors
        int[] temp = new int[2];
        temp[0] = dg.readShort();
        temp[1] = dg.readShort();
        res = new Rx_package(pck_type, node_index, dest_addr,
                             temp);        
      } /*else if(pck_type == 5)
      {
        int[] temp = new int[8];
        for(int i = 0; i < temp.length; i++)
          temp[i] = dg.readShort();
        res = new Rx_package(pck_type, node_index, dest_addr,
                             temp);                
        // } else if(pck_type == 10)
        // { // keep the node count in temp
        //   // node count = 8 bits
        //   int[] temp = new int[9];
        //   temp[0] = dg.readByte();
        //   for(int i = 1; i < temp.length; i++)
        //     temp[i] = dg.readShort();
        //   res = new Rx_package(pck_type, node_index, dest_addr,
        //                        temp);                        
      } else if(pck_type == 6)
      {
        int[] temp = new int[16];
        for(int i = 0; i < temp.length; i++)
          temp[i] = dg.readShort();        
        res = new Rx_package(pck_type, node_index, dest_addr,
                             temp);  
        //}  else if(pck_type == 11)
        //{ // keep the node count in temp
        // node count = 8 bits
        //int[] temp = new int[17];
        //temp[0] = dg.readByte();
        //for(int i = 1; i < temp.length; i++)
        //  temp[i] = dg.readShort();
        //res = new Rx_package(pck_type, node_index, dest_addr,
        //                     temp);    
      } else if(pck_type == 7)
      {
        int[] temp = new int[32];
        for(int i = 0; i < temp.length; i++)
          temp[i] = dg.readShort();
        res = new Rx_package(pck_type, 0, dest_addr, temp);
        
      } *///else if(pck_type == 12)
      //{ // keep the node count in temp
      // node count = 8 bits
      // int[] temp = new int[33];
      // temp[0] = dg.readByte();
      // for(int i = 1; i < temp.length; i++)
      //  temp[i] = dg.readShort();
      // res = new Rx_package(pck_type, node_index, dest_addr,
      //                     temp); 
      //}
      else if(pck_type == 8)
      {
        int[] temp = new int[1];
        for(int i = 0; i < temp.length; i++)
          temp[i] = dg.readShort();
        res = new Rx_package(pck_type, 0, dest_addr, temp);
        
      } else if(pck_type == 9)
      {
        int[] temp = new int[32];
        for(int i = 0; i < temp.length; i++)
          temp[i] = dg.readShort();
        res = new Rx_package(pck_type, 0, dest_addr, temp);
        
      } /**else if(pck_type == 13)
      {
        int[] temp = new int[1];
        temp[0] = dg.readShort();
        res = new Rx_package(pck_type, 0, dest_addr, temp);
        
      } */else if(pck_type == 15)
      { // setup package
        int[] temp = new int[Constants.VALUE_SIZE];
        for(int i = 0; i < Constants.VALUE_SIZE; i++)
            temp[i] = dg.readShort();
        res = new Rx_package(pck_type, node_index, dest_addr, temp);
      }

        System.out.println("Read the dg");
        if((pck_type == 5) || (pck_type == 13) || (pck_type == 6)) {
            System.out.println("initialize the vectors");
            Vector hiv = new Vector();
            Vector nv = new Vector();
            short rd_start = dg.readShort();
            
            while(rd_start != Constants.SEPERATOR) {
                System.out.println("test");
                short wl = rd_start;
                short wr = dg.readShort(); 
                short wh = dg.readShort(); 
                short wscore = dg.readShort();
                hiv.addElement(new Window(wl, wr, wh, wscore));
                rd_start = dg.readShort();
            }
            System.out.println("test2");            
            rd_start = dg.readShort();
            while(rd_start != Constants.TERMINATOR) {
                System.out.println("test3");                
                nv.addElement(new Short(rd_start)); 
                rd_start = dg.readShort();
            }
            sl_rx = new DistSlabfile(hiv, nv);
            res = new Rx_package(pck_type, node_index, dest_addr, sl_rx);
        }   
        

        
    } catch (IOException e) {
      System.out.println("$TC Nothing Received");
      e.printStackTrace();
      res = null;
    }
    return res;
  }
  
  public void close() {
    if(tiny_connection != null)
    {
      if(is_broadcast)
        System.out.println("$TC Close the broadcast connection");
      else
        System.out.println("$TC Close T: " + dst_addr + " connection");
    
      try {
        // error handling for radio broadcasting
        tiny_connection.close();
        tiny_connection = null;
        dg = null;
        is_broadcast = false;
        port_no = -1;
      } catch (IOException e) {
        System.out.println("$TC Close the connection");
        e.printStackTrace();
      }
    }
  }

  private int within_cluster(String addr) {
    // if this sun spot receives a package from a principle node,
    // return asap.
    // this if statement assumes that if an address is 
    // sun spot's, return -1.
    if(Integer.parseInt(addr.substring(15,16)) == 7)
      return -1;
    
    String sub_addr = last_4addr(addr);
    for(int i = 0; i < telosb_nodes.length; i++)
    {
      if(sub_addr.equals(telosb_nodes[i]))
        return i;
    }

    return -1;      
  }
  
  public String last_4addr(String addr) {
    // "0014.4F01.0000.7F42" -> "7F42"
    return addr.substring(15, addr.length());
  }
}
