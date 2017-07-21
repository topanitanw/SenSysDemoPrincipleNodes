/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld;

import java.util.Hashtable;
/**
 *
 * @author PanitanW
 */
public class Constants {
    ////////////////////////Default static values..............................
    public static final short AREA_WIDTH= 350;  //area width
    public static final short AREA_HEIGHT= 350;  //area size
    // gap 
    // |_0_1_2_3_4_5_6_| the underscores are the gap widths
    public static final short GAP_HEIGHT= AREA_HEIGHT/7;  //area gap
    public static final short GAP_WIDTH= AREA_WIDTH/7;  //area gap
    
    public static final short TOTAL_MOTES = 36;
    
    public static final short LIGHT_PHENOMENA = 2;  //if the phenomena is light
    public static final short TEMP_PHENOMENA = 3;  //if the phenomena is temperature
    
    public static final short MAX_X = 350;
    public static final short MAX_Y = 350;
    
    public static String[] ss_id = new String[] { "7EBA", "7F45", "79A3", "7997"};
    public static final short TERMINATOR = -127;
    public static final int VALUE_SIZE = 31;
    public static final String T1205_ID = "0014.4F01.0000.1205";
    public static final String BROADCAST_ID = "0014.4F01.0000.FFFF";
    public static final String[] TELOSB_NODES = {"1205"}; 
    public static final int CONNECTION_PORT = 65; 
    public static final short SEPERATOR  = -100;
    public static final int BROADCAST_CONSTANT = 0x7F;
    
    public static Hashtable nodeIds= new Hashtable();
    
    public static void setAddresIDMapping(){
        nodeIds.put("0000", new Integer(0));
        // nodeIds.put("0101", new Integer(1));
        nodeIds.put("0202", new Integer(2));
        nodeIds.put("1003", new Integer(3));
        // nodeIds.put("1104", new Integer(4));
        nodeIds.put("1205", new Integer(5));
        // nodeIds.put("0306", new Integer(6));
        nodeIds.put("7EBA", new Integer(7));
        // nodeIds.put("0407", new Integer(8));
        // nodeIds.put("1308", new Integer(9));
        nodeIds.put("7F45", new Integer(10));
        // nodeIds.put("1409", new Integer(11));
        nodeIds.put("0510", new Integer(12));
        // nodeIds.put("0611", new Integer(13));
        nodeIds.put("0712", new Integer(14));
        nodeIds.put("1513", new Integer(15));
        // nodeIds.put("1614", new Integer(16));
        nodeIds.put("1715", new Integer(17));
        nodeIds.put("2016", new Integer(18));
        // nodeIds.put("2117", new Integer(19));
        nodeIds.put("2218", new Integer(20));
        nodeIds.put("3019", new Integer(21));
        // nodeIds.put("3120", new Integer(22));
        nodeIds.put("3221", new Integer(23));
        // nodeIds.put("2322", new Integer(24));
        nodeIds.put("79A3", new Integer(25));
        // nodeIds.put("2423", new Integer(26));
        // nodeIds.put("3324", new Integer(27));
        nodeIds.put("7997", new Integer(28));
        // nodeIds.put("3425", new Integer(29));
        nodeIds.put("2526", new Integer(30));
        // nodeIds.put("2627", new Integer(31));
        nodeIds.put("2728", new Integer(32));
        nodeIds.put("3529", new Integer(33));
        // nodeIds.put("3630", new Integer(34));
        nodeIds.put("3731", new Integer(35));
    }
    
    public static Short getNodeId(String addr){
        String addr2=addr;
        if(addr.length()==19){
            addr2=addr.substring(15);
        }
        
        if(nodeIds.containsKey(addr2)){
            Integer v = (Integer)nodeIds.get(addr2);
            return new Short((short)v.intValue());   
        }
        return new Short((short)-1);    
    }
    
    public static Point getNodeLocation(short id){
        if(id<0 || id>35){
            return null;
        }
        short offsetw = Constants.AREA_WIDTH/7;
        short offseth = Constants.AREA_HEIGHT/7;
        int indw = id%6;
        int indh = id/6;
        return new Point((short)(offsetw+(indw*offsetw)),(short)(offseth+(indh*offseth)));
    }
    
    public static boolean isTelos(short id){
        if(id==7 || id==10 || id==25 || id==28){
            return false;
        }
        return true;
    
    }
    public static boolean isWithin(Rectangle rect, Objects obj){
        if(obj.x<=rect.x2 && obj.x >=rect.x1 && obj.y<=rect.y2 && obj.y >= rect.y1){
            return true;
        }
        return false;
    }
    
    public static boolean isWithin(Rectangle rect, Point p){
            if(p.x<=rect.x2 && p.x >=rect.x1 && p.y<=rect.y2 && p.y >= rect.y1){
                return true;
            }
            return false;
    }      
}
