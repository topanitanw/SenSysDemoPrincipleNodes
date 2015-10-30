/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Muhammed
 */
public class DistributedMaxRS {

    public static Vector currentValues = new Vector();
    public static Vector currentObjects2 = new Vector();
    public static short current_phenomena = Constants.LIGHT_PHENOMENA;
    static Area area;
    static Area coverage;
    
    /////////////////////////////////////*********** Testing Purposes ************************////////////////////////////////////
    public static Vector currentRectangle = new Vector();
    public static Vector currentObjects = new Vector();
    /////////////////////////////////////*********** Testing Purposes ************************////////////////////////////////////
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      Random rand = new Random();
      Meit meit = new Meit();
      
      for(int i=0; i<Constants.TOTAL_MOTES; i++){
          Short val=new Short((short)rand.nextInt(100));
          currentValues.addElement(val);
          Point p = Constants.getNodeLocation((short)i);
          currentObjects2.addElement(new Object(p.x, p.y, val.shortValue()));
      }
      
      //printing values to compare
      for(int i=5; i>=0; i--){
           for(int j=0; j<6;j++){
              Short val = (Short)currentValues.elementAt((i*6)+j);
              System.out.print(val.shortValue()+" ");
           }
           System.out.println(""); 
      }
      
      area = new Area(Constants.AREA_WIDTH, Constants.AREA_HEIGHT);
      //coverage <=50, nothing to send
      //coverage between 51 and 150, send only 1 layer
      //coverage between 151 and 250, send 2 layers
      //coverage between 251 and 350, send all values
      coverage = computeCoverage(10);
      
      DistSlabfile[] first_res=processingC_2();
      DistSlabfile second_res=processingC_0(first_res[0]);
      DistSlabfile third_res=processingC_3(first_res[1]);
      Window opt_window=processingC_1(second_res, third_res);
      System.out.println(opt_window.score);
    
    }
  
    static void initializeC_2(Vector myObjectIds, Vector myObjects, Vector myRectangles){
        //hard-coded ids for each cluster
        myObjectIds.addElement(new Short((short)18));
        myObjectIds.addElement(new Short((short)19));
        myObjectIds.addElement(new Short((short)20));
        myObjectIds.addElement(new Short((short)24));
        myObjectIds.addElement(new Short((short)25));  //myid
        myObjectIds.addElement(new Short((short)26));
        myObjectIds.addElement(new Short((short)30));
        myObjectIds.addElement(new Short((short)31));
        myObjectIds.addElement(new Short((short)32));
        
        //setting up the objects for once
        for(int i=0; i<9; i++){
            short id = myObjectIds.get(i);
            Point p = Constants.getNodeLocation(id);
            myObjects.add(new Object(p.x, p.y, currentValues.get(id)));
        }
        
        Vector<Object> myObects_sorted = (Vector<Object>) myObjects.clone();
        //sort the objects, add the rectangles
        Collections.sort(myObects_sorted);
        for(Object obj : myObects_sorted){
               myRectangles.add(new Rectangle((short)Math.max(0, obj.x - coverage.width/2),
                                                (short)Math.max(0, obj.y - coverage.height/2),
                                                (short)Math.min(area.width,
                                                         obj.x + coverage.width/2),
                                                (short)Math.min(area.height,
                                                         obj.y + coverage.height/2),
                                                obj.weight));
        }
        
    }
 
    static void initializeC_0(Vector<Short> myObjectIds, Vector<Object> myObjects, Vector<Rectangle> myRectangles, DistSlabfile osf, short coverage_height){
        //hard-coded ids for each cluster
        myObjectIds.add((short)0);
        myObjectIds.add((short)1);
        myObjectIds.add((short)2);
        myObjectIds.add((short)6);
        myObjectIds.add((short)7);  //myid
        myObjectIds.add((short)8);
        myObjectIds.add((short)12);
        myObjectIds.add((short)13);
        myObjectIds.add((short)14);
        
        //object id-s from cluster-2
        myObjectIds.add((short)18);
        myObjectIds.add((short)19);
        myObjectIds.add((short)20);
        myObjectIds.add((short)24);
        myObjectIds.add((short)25);  //c-2 principal id
        myObjectIds.add((short)26);
        myObjectIds.add((short)30);
        myObjectIds.add((short)31);
        myObjectIds.add((short)32);
        
        //setting up the objects for once
        for(int i=0; i<9; i++){
            short id = myObjectIds.get(i);
            Point p = Constants.getNodeLocation(id);
            myObjects.add(new Object(p.x, p.y, currentValues.get(id)));
        }
        //Add adjustments, meaning newobjects here
        //from cluster-2
        short i=9;
        short j=0;
        int limit = (coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
        for(int k=0; k<limit; k++){
            for(;i<(3 *(4+k));i++){
                Point obj = Constants.getNodeLocation(myObjectIds.get(i));
                myObjects.add(new Object(obj.x, obj.y, osf.neededValues.get(j++)));            
             }
        }        
        
        Vector<Object> myObects_sorted = (Vector<Object>) myObjects.clone();
        //sort the objects, add the rectangles
        Collections.sort(myObects_sorted);
        for(Object obj : myObects_sorted){
               myRectangles.add(new Rectangle((short)Math.max(0, obj.x - coverage.width/2),
                                                (short)Math.max(0, obj.y - coverage.height/2),
                                                (short)Math.min(area.width,
                                                         obj.x + coverage.width/2),
                                                (short)Math.min(area.height,
                                                         obj.y + coverage.height/2),
                                                obj.weight));
        }
    }
    
    static void initializeC_3(Vector<Short> myObjectIds, Vector<Object> myObjects, Vector<Rectangle> myRectangles, DistSlabfile osf, short coverage_width){
        //hard-coded ids for each cluster
        myObjectIds.add((short)21);
        myObjectIds.add((short)22);
        myObjectIds.add((short)23);
        myObjectIds.add((short)27);
        myObjectIds.add((short)28);  //myid
        myObjectIds.add((short)29);
        myObjectIds.add((short)33);
        myObjectIds.add((short)34);
        myObjectIds.add((short)35);
        
        //object id-s from cluster-2
                                            
        myObjectIds.add((short)20);
        myObjectIds.add((short)26);
        myObjectIds.add((short)32);
        myObjectIds.add((short)19);    
        myObjectIds.add((short)25);  //c-2 principal id 
        myObjectIds.add((short)31);
        myObjectIds.add((short)18);   
        myObjectIds.add((short)24);
        myObjectIds.add((short)30);



        //setting up the objects for once
        for(int i=0; i<9; i++){
            short id = myObjectIds.get(i);
            Point p = Constants.getNodeLocation(id);
            myObjects.add(new Object(p.x, p.y, currentValues.get(id)));
        }
        //Add adjustments, meaning new objects here
        //from cluster-2
        short i=9;
        short j=0;
        int limit = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
        for(int k=0; k<limit; k++){
             for(;i<(3 *(4+k));i++){
                Point obj = Constants.getNodeLocation(myObjectIds.get(i));
                myObjects.add(new Object(obj.x, obj.y, osf.neededValues.get(j++)));               
             }
        }        
        
        
        Vector<Object> myObects_sorted = (Vector<Object>) myObjects.clone();
        //sort the objects, add the rectangles
        Collections.sort(myObects_sorted);
        for(Object obj : myObects_sorted){
               myRectangles.add(new Rectangle((short)Math.max(0, obj.x - coverage.width/2),
                                                (short)Math.max(0, obj.y - coverage.height/2),
                                                (short)Math.min(area.width,
                                                         obj.x + coverage.width/2),
                                                (short)Math.min(area.height,
                                                         obj.y + coverage.height/2),
                                                obj.weight));
        }
       
    }
    
    static void initializeC_1(Vector<Short> myObjectIds, Vector<Object> myObjects, Vector<Rectangle> myRectangles, DistSlabfile osf1, DistSlabfile osf2, short coverage_height, short coverage_width){
        //hard-coded ids for each cluster
        myObjectIds.add((short)3);
        myObjectIds.add((short)4);
        myObjectIds.add((short)5);
        myObjectIds.add((short)9);
        myObjectIds.add((short)10);  //myid
        myObjectIds.add((short)11);
        myObjectIds.add((short)15);
        myObjectIds.add((short)16);
        myObjectIds.add((short)17);
        
        //ids from cluster - 0 (left)
        myObjectIds.add((short)2);
        myObjectIds.add((short)8);
        myObjectIds.add((short)14);
        myObjectIds.add((short)1);
        myObjectIds.add((short)7);  //myid
        myObjectIds.add((short)13);
        myObjectIds.add((short)0);
        myObjectIds.add((short)6);
        myObjectIds.add((short)12);
        
        //ids from cluster - 3
        myObjectIds.add((short)21);
        myObjectIds.add((short)22);
        myObjectIds.add((short)23);
        myObjectIds.add((short)27);
        myObjectIds.add((short)28);  //myid
        myObjectIds.add((short)29);
        myObjectIds.add((short)33);
        myObjectIds.add((short)34);
        myObjectIds.add((short)35);
        
        //ids from cluster - 2
        myObjectIds.add((short)20);
        myObjectIds.add((short)26);
        myObjectIds.add((short)32);
        myObjectIds.add((short)19);    
        myObjectIds.add((short)25);  //c-2 principal id 
        myObjectIds.add((short)31);
        myObjectIds.add((short)18);   
        myObjectIds.add((short)24);
        myObjectIds.add((short)30);
        
        //setting up the objects for once
        for(int i=0; i<9; i++){
            short id = myObjectIds.get(i);
            Point p = Constants.getNodeLocation(id);
            myObjects.add(new Object(p.x, p.y, currentValues.get(id)));
        }
        
        //Add adjustments, meaning new rectangles here
        //from cluster-0
        short i=9;
        short j=0;
        int limit = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
        for(int k=0; k<limit; k++){
             for(;i<(3 *(4+k));i++){
                Point obj = Constants.getNodeLocation(myObjectIds.get(i));
                myObjects.add(new Object(obj.x, obj.y, osf1.neededValues.get(j++)));               
             }
        }

        //from cluster-3
        i=18;
        j=0;
        limit=(coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
        for(int k=0; k<limit; k++){
            for(;i<(3 *(7+k));i++){
                Point obj = Constants.getNodeLocation(myObjectIds.get(i));
                myObjects.add(new Object(obj.x, obj.y, osf2.neededValues.get(j++)));              
             }
        }

        //from cluster-2
        int k=(coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
        int limit2 = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;       
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for(i=0; i<limit2; i++){
            for(short p=0;p<k; p++){
                Point obj = Constants.getNodeLocation(myObjectIds.get(27+(i*3+p)));
                System.out.println(osf2.neededValues.get(j));
                myObjects.add(new Object(obj.x, obj.y, osf2.neededValues.get(j++)));   
            }
        }    
        
        Vector<Object> myObects_sorted = (Vector<Object>) myObjects.clone();
        //sort the objects, add the rectangles
        Collections.sort(myObects_sorted);
        for(Object obj : myObects_sorted){       
               myRectangles.add(new Rectangle((short)Math.max(0, obj.x - coverage.width/2),
                                                (short)Math.max(0, obj.y - coverage.height/2),
                                                (short)Math.min(area.width,
                                                         obj.x + coverage.width/2),
                                                (short)Math.min(area.height,
                                                         obj.y + coverage.height/2),
                                                obj.weight));
        }
    }
    
    static DistSlabfile[] processingC_2(){
        Vector<Short> myObjectIds = new Vector<Short>();
        Vector<Object> myObjects = new Vector<Object>();
        Vector<Rectangle> myRectangles = new Vector<Rectangle>();
        Hashtable<Short, Window> slabFile = new Hashtable<Short, Window>();
        Meit meit = new Meit();
        initializeC_2(myObjectIds, myObjects, myRectangles);
        Vector<Short> aListOfX1 = new Vector<Short>();
        for(int i = 0; i < myRectangles.size(); i++)
        {
           aListOfX1.add(myRectangles.get(i).x1);
           aListOfX1.add(myRectangles.get(i).x2);
        }
        
        Collections.sort(aListOfX1);
        Vector<Short> aListOfX = new Vector<Short>(); // xs in python code
        for(Short d : aListOfX1)
        {
          if(!aListOfX.contains(d))
              aListOfX.add(d);
        }
        
        IntervalTree root = meit.buildIntervalTree(0, aListOfX.size()-1, aListOfX, null);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        meit.preOrderTraverse(root);
        meit.interval_tree_root = root;
        root.window = new Window(aListOfX.get(0), aListOfX.get(aListOfX.size() - 1),
                               (short) 0, (short) 0);

        slabFile= meit.maxEnclosing(myRectangles, coverage, root);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"); 
        
        Vector<Window> hintervals = new Vector<Window>();
        for(short sfk : slabFile.keySet()){
            Window sf = (Window) slabFile.get(sfk);
            hintervals.add(sf);
        }
        Collections.sort(hintervals);
        for(Window h : hintervals){
            System.out.println(h.h+"-----"+h.l+ " "+h.r+" "+h.score);
        }
        //System.out.println("optimal_window score l r h: " + optimal_window.score + " " + optimal_window.l + " " + optimal_window.r + " " + optimal_window.h);

        //meit.writeOutput("cl-2.txt", area, coverage, myObjects, optimal_window);
        Vector<Short> values = new Vector<Short>();
        int limit = (coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
        short j=0;
        for(int k=0; k<limit; k++){
            values.add(myObjects.get(j++).weight);
            values.add(myObjects.get(j++).weight);
            values.add(myObjects.get(j++).weight);
        }
        
        Vector<Short> values1 = new Vector<Short>();
        j=2;
        limit = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
        for(int k=0; k<limit; k++, j--){
            values1.add(myObjects.get(j).weight);
            values1.add(myObjects.get(j+3).weight);
            values1.add(myObjects.get(j+6).weight);
        }
        
        DistSlabfile[] result = new DistSlabfile[2];
        result[0] = new DistSlabfile(hintervals, values);
        result[1] = new DistSlabfile(hintervals, values1);
        
        return result;
    }
    
    static DistSlabfile processingC_0(DistSlabfile osf){
        Vector<Short> myObjectIds = new Vector<Short>();
        Vector<Object> myObjects = new Vector<Object>();
        Vector<Rectangle> myRectangles = new Vector<Rectangle>();
        Hashtable<Short, Window> slabFile = new Hashtable<Short, Window>();
        Meit meit = new Meit();
        short coverage_height=(short)Math.min((Constants.getNodeLocation((short)12).y+ (coverage.height/2)), area.height);
        
        initializeC_0(myObjectIds, myObjects, myRectangles, osf, coverage_height);
        
        Vector<Short> aListOfX1 = new Vector<Short>();
        for(int i = 0; i < myRectangles.size(); i++)
        {
           aListOfX1.add(myRectangles.get(i).x1);
           aListOfX1.add(myRectangles.get(i).x2);
        }
        
        Collections.sort(aListOfX1);
        Vector<Short> aListOfX = new Vector<Short>(); // xs in python code
        for(Short d : aListOfX1)
        {
          if(!aListOfX.contains(d))
              aListOfX.add(d);
        }
        
        IntervalTree root = meit.buildIntervalTree(0, aListOfX.size()-1, aListOfX, null);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        meit.preOrderTraverse(root);
        meit.interval_tree_root = root;
        root.window = new Window(aListOfX.get(0), aListOfX.get(aListOfX.size() - 1),
                               (short) 0, (short) 0);

        slabFile= meit.maxEnclosing(myRectangles, coverage, root);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        
        Vector<Window> hintervals = new Vector<Window>();
        for(short sfk : slabFile.keySet()){
            Window sf = (Window) slabFile.get(sfk);
            hintervals.add(sf);
        }
        //adding from previous slab
        for(Window sf :  osf.hintervals){
            if(sf.h>=coverage_height){
                hintervals.add(sf);
            }
        }
        
        Collections.sort(hintervals);
        for(Window h : hintervals){
            System.out.println(h.h+"-----"+h.l+ " "+h.r+" "+h.score);
        }
        
        Vector<Short> values = new Vector<Short>();
        short j=2;
        int limit = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
        for(int k=0; k<limit; k++, j--){
            values.add(myObjects.get(j).weight);
            values.add(myObjects.get(j+3).weight);
            values.add(myObjects.get(j+6).weight);
        }
        
        DistSlabfile result = new DistSlabfile(hintervals, values);
        return result;
    }

    static DistSlabfile processingC_3(DistSlabfile osf){
        Vector<Short> myObjectIds = new Vector<Short>();
        Vector<Object> myObjects = new Vector<Object>();
        Vector<Rectangle> myRectangles = new Vector<Rectangle>();
        Hashtable<Short, Window> slabFile = new Hashtable<Short, Window>();
        Meit meit = new Meit();
        short coverage_width=(short)Math.max((Constants.getNodeLocation((short)21).x-(coverage.width/2)), 0);
        
        initializeC_3(myObjectIds, myObjects, myRectangles, osf, coverage_width);

        Vector<Short> aListOfX1 = new Vector<Short>();
        for(int i = 0; i < myRectangles.size(); i++)
        {
           aListOfX1.add(myRectangles.get(i).x1);
           aListOfX1.add(myRectangles.get(i).x2);
        }
        
        Collections.sort(aListOfX1);
        Vector<Short> aListOfX = new Vector<Short>(); // xs in python code
        for(Short d : aListOfX1)
        {
          if(!aListOfX.contains(d))
              aListOfX.add(d);
        }

        IntervalTree root = meit.buildIntervalTree(0, aListOfX.size()-1, aListOfX, null);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        meit.preOrderTraverse(root);
        meit.interval_tree_root = root;
        root.window = new Window(aListOfX.get(0), aListOfX.get(aListOfX.size() - 1),
                               (short) 0, (short) 0);

        slabFile= meit.maxEnclosing(myRectangles, coverage, root);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        
        Vector<Window> hintervals = new Vector<Window>();
        for(short sfk : slabFile.keySet()){
            Window sf = (Window) slabFile.get(sfk);
            hintervals.add(sf);
        }        

        //adding from previous slab
        /*for(Window sf :  osf.hintervals){
            if(sf.h>=coverage_height){
                hintervals.add(sf);
            }
        }*/
        
        Collections.sort(hintervals);
        for(Window h : hintervals){
            System.out.println(h.h+"-----"+h.l+ " "+h.r+" "+h.score);
        }
        
        Vector<Short> values = new Vector<Short>();
        short j=0;
        int limit = (coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
        for(int k=0; k<limit; k++){
            values.add(myObjects.get(j++).weight);
            values.add(myObjects.get(j++).weight);
            values.add(myObjects.get(j++).weight);
        }
        
        int limit2 = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;       
        for(int i=0; i<limit2; i++){
            for(j=0;j<limit; j++){
                values.add(myObjects.get(9+(i*3+j)).weight);
            }
        }
        
        DistSlabfile result = new DistSlabfile(hintervals, values);
        return result;
    }
    
    static Window processingC_1(DistSlabfile osf1,DistSlabfile osf2){
        Vector<Short> myObjectIds = new Vector<Short>();
        Vector<Object> myObjects = new Vector<Object>();
        Vector<Rectangle> myRectangles = new Vector<Rectangle>();
        Hashtable<Short, Window> slabFile = new Hashtable<Short, Window>();
        Meit meit = new Meit();
        short coverage_width=(short)Math.max((Constants.getNodeLocation((short)3).x-(coverage.width/2)), 0);
        short coverage_height=(short)Math.min((Constants.getNodeLocation((short)15).y+(coverage.height/2)), area.height);
        
        initializeC_1(myObjectIds, myObjects, myRectangles, osf1, osf2, coverage_height, coverage_width);

        Vector<Short> aListOfX1 = new Vector<Short>();
        for(int i = 0; i < myRectangles.size(); i++)
        {
           aListOfX1.add(myRectangles.get(i).x1);
           aListOfX1.add(myRectangles.get(i).x2);
        }
        
        Collections.sort(aListOfX1);
        Vector<Short> aListOfX = new Vector<Short>(); // xs in python code
        for(Short d : aListOfX1)
        {
          if(!aListOfX.contains(d))
              aListOfX.add(d);
        }
        
        IntervalTree root = meit.buildIntervalTree(0, aListOfX.size()-1, aListOfX, null);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        meit.preOrderTraverse(root);
        meit.interval_tree_root = root;
        root.window = new Window(aListOfX.get(0), aListOfX.get(aListOfX.size() - 1),
                               (short) 0, (short) 0);

        slabFile= meit.maxEnclosing(myRectangles, coverage, root);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
              
        Vector<Window> hintervals = new Vector<Window>();
        for(short sfk : slabFile.keySet()){
            Window sf = (Window) slabFile.get(sfk);
            hintervals.add(sf);
            System.out.println(sf.h+"-----"+sf.l+" "+sf.r+" "+sf.score);
        }

        System.out.println("------------------------------------------");        
        //adding from previous slab (c-3)
        for(Window sf :  osf2.hintervals){
            System.out.println(sf.h+"-----"+sf.l+" "+sf.r+" "+sf.score);
            if(sf.h>=coverage_height){
                hintervals.add(sf);
            }
        }
        System.out.println("------------------------------------------");
        //adding from previous slab (c-0)
        for(Window sf :  osf1.hintervals){
            System.out.println(sf.h+"-----"+sf.l+" "+sf.r+" "+sf.score);
            if(sf.r<=coverage_width){
                hintervals.add(sf);
            }
            else{
                if(sf.l<coverage_width){
                    sf.r=coverage_width;
                    hintervals.add(sf);
                }
            }
        }
        
        Collections.sort(hintervals);
        Window opt_window = new Window((short)0, (short)0, (short)0, (short)0); 
        for(Window h : hintervals){
            //System.out.println(h.h+"-----"+h.l+" "+h.r+" "+h.score);
            if(h.score>opt_window.score){
                opt_window = h;
            }
        }
        return opt_window;
    }
 
    static Area computeCoverage(int Energy){
        //do compute the size of the rectangle from Energy threshold given by the user
        //Always make the area EVEN!!!!!!!!!!!!!!!!!!!!!!!
        return new Area((short)126, (short)126);
    } 
}
