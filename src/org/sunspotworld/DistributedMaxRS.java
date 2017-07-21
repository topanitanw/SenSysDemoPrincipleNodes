/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 * Demo1_Principle_Nodes
 * @author Muhammed
 */
public class DistributedMaxRS {

  public static Vector currentValues = new Vector(Constants.TOTAL_MOTES);
  public static Vector currentObjects2 = new Vector();
  public static short current_phenomena = Constants.LIGHT_PHENOMENA;
  public static Area area;
  public static Area coverage =  new Area((short)0, (short)0);

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
      currentObjects2.addElement(new Objects(p.x, p.y, val.shortValue()));
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
    //meit.writeOutput("dist-output.txt", area, coverage, currentObjects2, opt_window);
  }

  static void initializeC_2(Vector myObjectIds, Vector myObjects, Vector myRectangles){
    //hard-coded ids for each cluster
    myObjectIds.addElement(new Short((short)18));
    // myObjectIds.addElement(new Short((short)19));
    myObjectIds.addElement(new Short((short)20));
    // myObjectIds.addElement(new Short((short)24));
    myObjectIds.addElement(new Short((short)25));  //myid
    // myObjectIds.addElement(new Short((short)26));
    myObjectIds.addElement(new Short((short)30));
    // myObjectIds.addElement(new Short((short)31));
    myObjectIds.addElement(new Short((short)32));

    //setting up the objects for once
    for(int i=0; i< myObjectIds.size(); i++) {
      Short id = (Short)myObjectIds.elementAt(i);
      Point p = Constants.getNodeLocation(id.shortValue());
      Short weight = (Short)currentValues.elementAt(id.shortValue());
      myObjects.addElement(new Objects(p.x, p.y, weight.shortValue()));
    }

    Vector myObects_sorted = new Vector();
    for(int c=0; c<myObjects.size(); c++) {
      myObects_sorted.addElement(myObjects.elementAt(c));
    }
    //sort the objects, add the rectangles
    //Collections.sort(myObects_sorted);

    myObects_sorted=doSelectionSortvobj(myObects_sorted);
    for(int c=0; c<myObjects.size(); c++){
      Objects obj = (Objects) myObects_sorted.elementAt(c);
      myRectangles.addElement(new Rectangle((short)Math.max(0, obj.x - coverage.width/2),
                                            (short)Math.max(0, obj.y - coverage.height/2),
                                            (short)Math.min(area.width,
                                                            obj.x + coverage.width/2),
                                            (short)Math.min(area.height,
                                                            obj.y + coverage.height/2),
                                            obj.weight));
    }
  }

  static void initializeC_0(Vector myObjectIds, Vector myObjects, Vector myRectangles, DistSlabfile osf, short coverage_height){
    //hard-coded ids for each cluster
    myObjectIds.addElement(new Short((short)0));
    // myObjectIds.addElement(new Short((short)1));
    myObjectIds.addElement(new Short((short)2));
    // myObjectIds.addElement(new Short((short)6));
    myObjectIds.addElement(new Short((short)7));  //myid
    // myObjectIds.addElement(new Short((short)8));
    myObjectIds.addElement(new Short((short)12));
    // myObjectIds.addElement(new Short((short)13));
    myObjectIds.addElement(new Short((short)14));

    //object id-s from cluster-2
    myObjectIds.addElement(new Short((short)18));
    // myObjectIds.addElement(new Short((short)19));
    myObjectIds.addElement(new Short((short)20));
    // myObjectIds.addElement(new Short((short)24));
    myObjectIds.addElement(new Short((short)25));  //c-2 principal id
    // myObjectIds.addElement(new Short((short)26));
    myObjectIds.addElement(new Short((short)30));
    // myObjectIds.addElement(new Short((short)31));
    myObjectIds.addElement(new Short((short)32));

    //setting up the objects for once
    for(int i=0; i<5; i++){
      Short id = (Short)myObjectIds.elementAt(i);
      Point p = Constants.getNodeLocation(id.shortValue());
      Short value = (Short)currentValues.elementAt(id.shortValue());
      myObjects.addElement(new Objects(p.x, p.y, value.shortValue()));
    }
    //Add adjustments, meaning newobjects here
    //from cluster-2
    short i=9;
    short j=0;
    int limit = (coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
    for(int k=0; k<limit; k++){
      for(;i<(3 *(4+k));i++){
        Short id=(Short) myObjectIds.elementAt(i);
        Point obj = Constants.getNodeLocation(id.shortValue());
        Short value = (Short)osf.neededValues.elementAt(j++);
        myObjects.addElement(new Objects(obj.x, obj.y, value.shortValue()));
      }
    }

    Vector myObects_sorted = new Vector();
    for(int c=0; c<myObjects.size(); c++){
      myObects_sorted.addElement(myObjects.elementAt(c));
    }
    //sort the objects, add the rectangles
    //Collections.sort(myObects_sorted);
    myObects_sorted=doSelectionSortvobj(myObects_sorted);
    for(int c=0; c<myObjects.size(); c++){
      Objects obj = (Objects) myObects_sorted.elementAt(c);
      myRectangles.addElement(new Rectangle((short)Math.max(0,
                                                            obj.x - coverage.width/2),
                                            (short)Math.max(0,
                                                            obj.y - coverage.height/2),
                                            (short)Math.min(area.width,
                                                            obj.x + coverage.width/2),
                                            (short)Math.min(area.height,
                                                            obj.y + coverage.height/2),
                                            obj.weight));
    }
  }

  static void initializeC_3(Vector myObjectIds, Vector myObjects, Vector myRectangles, DistSlabfile osf, short coverage_width){
    //hard-coded ids for each cluster
    myObjectIds.addElement(new Short((short)21));
    // myObjectIds.addElement(new Short((short)22));
    myObjectIds.addElement(new Short((short)23));
    // myObjectIds.addElement(new Short((short)27));
    myObjectIds.addElement(new Short((short)28));  //myid
    // myObjectIds.addElement(new Short((short)29));
    myObjectIds.addElement(new Short((short)33));
    // myObjectIds.addElement(new Short((short)34));
    myObjectIds.addElement(new Short((short)35));

    //object id-s from cluster-2

    myObjectIds.addElement(new Short((short)20));
    // myObjectIds.addElement(new Short((short)26));
    myObjectIds.addElement(new Short((short)32));
    // myObjectIds.addElement(new Short((short)19));
    myObjectIds.addElement(new Short((short)25));  //c-2 principal id
    // myObjectIds.addElement(new Short((short)31));
    myObjectIds.addElement(new Short((short)18));
    // myObjectIds.addElement(new Short((short)24));
    myObjectIds.addElement(new Short((short)30));



    //setting up the objects for once
    for(int i=0; i<9; i++){
      Short id = (Short)myObjectIds.elementAt(i);
      Point p = Constants.getNodeLocation(id.shortValue());
      Short val = (Short)currentValues.elementAt(id.shortValue());
      myObjects.addElement(new Objects(p.x, p.y, val.shortValue()));
    }
    //Add adjustments, meaning new objects here
    //from cluster-2
    short i=9;
    short j=0;
    int limit = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
    for(int k=0; k<limit; k++){
      for(;i<(3 *(4+k));i++){
        Short id = (Short)myObjectIds.elementAt(i);
        Point p = Constants.getNodeLocation(id.shortValue());
        Short val = (Short)osf.neededValues.elementAt(j++);
        myObjects.addElement(new Objects(p.x, p.y, val.shortValue()));
      }
    }


    Vector myObects_sorted = new Vector();
    for(int c=0; c<myObjects.size(); c++){
      myObects_sorted.addElement(myObjects.elementAt(c));
    }
    //sort the objects, add the rectangles
    //Collections.sort(myObects_sorted);
    myObects_sorted=doSelectionSortvobj(myObects_sorted);
    for(int c=0; c<myObjects.size(); c++){
      Objects obj = (Objects) myObects_sorted.elementAt(c);
      myRectangles.addElement(new Rectangle((short)Math.max(0, obj.x - coverage.width/2),
                                            (short)Math.max(0, obj.y - coverage.height/2),
                                            (short)Math.min(area.width,
                                                            obj.x + coverage.width/2),
                                            (short)Math.min(area.height,
                                                            obj.y + coverage.height/2),
                                            obj.weight));
    }

  }

  static void initializeC_1(Vector myObjectIds, Vector myObjects, Vector myRectangles, DistSlabfile osf1, DistSlabfile osf2, short coverage_height, short coverage_width){
    // osf1 := sensor values of cluster 0,
    // osf2 := sensor values of cluster 3, 2
    // initialize myRectangles, myObjects
    // id = the node index to access the sensor values stored in the currentValues
    // hard-coded ids for each cluster
    // cluster 1
    // |---+---|
    // | 0 | 1 |
    // |---+---|
    // | 2 | 3 |
    // |---+---|
    //
    // Node id in the network
    // |----+----+----+---+----+----+----|
    // |  0 |  1 |  2 |   |  3 |  4 |  5 |
    // |  6 |  7 |  8 |   |  9 | 10 | 11 |
    // | 12 | 13 | 14 |   | 15 | 16 | 17 |
    // |----+----+----+---+----+----+----|
    // | 18 | 19 | 20 |   | 21 | 22 | 23 |
    // | 24 | 25 | 26 |   | 27 | 28 | 29 |
    // | 30 | 31 | 32 |   | 33 | 34 | 35 |
    // |----+----+----+---+----+----+----|

    myObjectIds.addElement(new Short((short)3));
    // myObjectIds.addElement(new Short((short)4));
    myObjectIds.addElement(new Short((short)5));
    // myObjectIds.addElement(new Short((short)9));
    myObjectIds.addElement(new Short((short)10));  //myid
    // myObjectIds.addElement(new Short((short)11));
    myObjectIds.addElement(new Short((short)15));
    // myObjectIds.addElement(new Short((short)16));
    myObjectIds.addElement(new Short((short)17));

    //ids from cluster - 0 (left)
    myObjectIds.addElement(new Short((short)2));
    // myObjectIds.addElement(new Short((short)8));
    myObjectIds.addElement(new Short((short)14));
    // myObjectIds.addElement(new Short((short)1));
    myObjectIds.addElement(new Short((short)7));  //myid
    // myObjectIds.addElement(new Short((short)13));
    myObjectIds.addElement(new Short((short)0));
    // myObjectIds.addElement(new Short((short)6));
    myObjectIds.addElement(new Short((short)12));

    //ids from cluster - 3
    myObjectIds.addElement(new Short((short)21));
    // myObjectIds.addElement(new Short((short)22));
    myObjectIds.addElement(new Short((short)23));
    // myObjectIds.addElement(new Short((short)27));
    myObjectIds.addElement(new Short((short)28));  //myid
    // myObjectIds.addElement(new Short((short)29));
    myObjectIds.addElement(new Short((short)33));
    // myObjectIds.addElement(new Short((short)34));
    myObjectIds.addElement(new Short((short)35));

    //ids from cluster - 2
    myObjectIds.addElement(new Short((short)20));
    // myObjectIds.addElement(new Short((short)26));
    myObjectIds.addElement(new Short((short)32));
    // myObjectIds.addElement(new Short((short)19));
    myObjectIds.addElement(new Short((short)25));  //c-2 principal id
    // myObjectIds.addElement(new Short((short)31));
    myObjectIds.addElement(new Short((short)18));
    // myObjectIds.addElement(new Short((short)24));
    myObjectIds.addElement(new Short((short)30));

    // setting up the objects for once
    // get obj location and sensor values to set up objects
    for(int i=0; i<9; i++){
      Short id = (Short)myObjectIds.elementAt(i);
      Point p = Constants.getNodeLocation(id.shortValue());
      Short val = (Short)currentValues.elementAt(id.shortValue());
      myObjects.addElement(new Objects(p.x, p.y, val.shortValue()));
    }

    //Add adjustments, meaning new rectangles here
    //from cluster-0
    short i=9;
    short j=0;
    int limit = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
    for(int k=0; k<limit; k++){
      for(;i<(3 *(4+k));i++){
        Short id = (Short)myObjectIds.elementAt(i);
        Point obj = Constants.getNodeLocation(id.shortValue());
        Short val = (Short)osf1.neededValues.elementAt(j++);
        myObjects.addElement(new Objects(obj.x, obj.y, val.shortValue()));
      }
    }

    //from cluster-3
    i=18;
    j=0;
    limit=(coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
    for(int k=0; k<limit; k++){
      for(;i<(3 *(7+k));i++){
        Short id = (Short)myObjectIds.elementAt(i);
        Point obj = Constants.getNodeLocation(id.shortValue());
        Short val = (Short)osf2.neededValues.elementAt(j++);
        myObjects.addElement(new Objects(obj.x, obj.y, val.shortValue()));
      }
    }

    //from cluster-2
    int k=(coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
    int limit2 = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    for(i=0; i<limit2; i++){
      for(short p=0;p<k; p++){
        Short id = (Short)myObjectIds.elementAt(27+(i*3+p));
        Point obj = Constants.getNodeLocation(id.shortValue());
        Short val = (Short)osf2.neededValues.elementAt(j++);
        myObjects.addElement(new Objects(obj.x, obj.y, val.shortValue()));
      }
    }

    Vector myObects_sorted = new Vector();
    for(int c=0; c<myObjects.size(); c++){
      myObects_sorted.addElement(myObjects.elementAt(c));
    }
    //sort the objects, add the rectangles
    //Collections.sort(myObects_sorted);
    myObects_sorted=doSelectionSortvobj(myObects_sorted);
    for(int c=0; c<myObjects.size(); c++){
      Objects obj = (Objects) myObects_sorted.elementAt(c);
      myRectangles.addElement(new Rectangle((short)Math.max(0, obj.x - coverage.width/2),
                                            (short)Math.max(0, obj.y - coverage.height/2),
                                            (short)Math.min(area.width,
                                                            obj.x + coverage.width/2),
                                            (short)Math.min(area.height,
                                                            obj.y + coverage.height/2),
                                            obj.weight));
    }
  }

  static void processingAll(){
    System.out.println("Copying the processing data..");
    for(int c=0;c<Demo1_Principle_Nodes.current_values.size(); c++){
      currentValues.setElementAt(Demo1_Principle_Nodes.current_values.elementAt(c), c);
    }
  }

  public static DistSlabfile[] processingC_2(){
    Vector myObjectIds = new Vector();
    Vector myObjects = new Vector();
    Vector myRectangles = new Vector();
    Hashtable slabFile = new Hashtable();
    Meit meit = new Meit();
    processingAll();
    initializeC_2(myObjectIds, myObjects, myRectangles);
    Vector aListOfX1 = new Vector();
    for(int i = 0; i < myRectangles.size(); i++)
    {
      Rectangle rect = (Rectangle)myRectangles.elementAt(i);
      aListOfX1.addElement(new Short(rect.x1));
      aListOfX1.addElement(new Short(rect.x2));
    }

    //Collections.sort(aListOfX1);
    aListOfX1=doSelectionSortvshort(aListOfX1);
    Vector aListOfX = new Vector(); // xs in python code
    for(int c=0; c<aListOfX1.size(); c++){
      Short d = (Short)aListOfX1.elementAt(c);
      if(!aListOfX.contains(d))
        aListOfX.addElement(d);
    }

    IntervalTree root = meit.buildIntervalTree(0, aListOfX.size()-1, aListOfX, null);
    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    meit.preOrderTraverse(root);
    meit.interval_tree_root = root;
    Short first = (Short)aListOfX.elementAt(0);
    Short last = (Short)aListOfX.elementAt(aListOfX.size() - 1);
    root.window = new Window(first.shortValue(), last.shortValue(),
                             (short) 0, (short) 0);

    slabFile=meit.maxEnclosing(myRectangles, coverage, root);
    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

    Vector hintervals = new Vector();
    //Short sfk = new Short((short)0);
    Enumeration en = slabFile.keys();
    while(en.hasMoreElements()){
      Short sfk = (Short)en.nextElement();
      Window sf = (Window) slabFile.get(sfk);
      hintervals.addElement(sf);
    }

    //Collections.sort(hintervals);
    hintervals=doSelectionSortvwind(hintervals);
    /*for(Window h : hintervals){
      System.out.println(h.h+"-----"+h.l+ " "+h.r+" "+h.score);
      }*/
    //System.out.println("optimal_window score l r h: " + optimal_window.score + " " + optimal_window.l + " " + optimal_window.r + " " + optimal_window.h);

    //meit.writeOutput("cl-2.txt", area, coverage, myObjects, optimal_window);
    Vector values = new Vector();
    int limit = (coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
    short j=0;
    for(int k=0; k<limit; k++){
      Objects obj = (Objects)myObjects.elementAt(j++);
      values.addElement(new Short(obj.weight));
      obj = (Objects)myObjects.elementAt(j++);
      values.addElement(new Short(obj.weight));
      obj = (Objects)myObjects.elementAt(j++);
      values.addElement(new Short(obj.weight));
    }

    Vector values1 = new Vector();
    j=2;
    limit = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
    for(int k=0; k<limit; k++, j--){
      Objects obj = (Objects)myObjects.elementAt(j);
      values1.addElement(new Short(obj.weight));
      obj = (Objects)myObjects.elementAt(j+3);
      values1.addElement(new Short(obj.weight));
      obj = (Objects)myObjects.elementAt(j+6);
      values1.addElement(new Short(obj.weight));
    }

    DistSlabfile[] result = new DistSlabfile[2];
    result[0] = new DistSlabfile(hintervals, values);  /// 0 = c-0
    result[1] = new DistSlabfile(hintervals, values1);  /// 1 - c-3

    return result;
  }

  public static DistSlabfile processingC_0(DistSlabfile osf){
    Vector myObjectIds = new Vector();
    Vector myObjects = new Vector();
    Vector  myRectangles = new Vector();
    Hashtable slabFile = new Hashtable();
    Meit meit = new Meit();
    processingAll();
    short coverage_height=(short)Math.min((Constants.getNodeLocation((short)12).y+ (coverage.height/2)), area.height);

    initializeC_0(myObjectIds, myObjects, myRectangles, osf, coverage_height);

    Vector aListOfX1 = new Vector();
    for(int i = 0; i < myRectangles.size(); i++)
    {
      Rectangle rect = (Rectangle)myRectangles.elementAt(i);
      aListOfX1.addElement(new Short(rect.x1));
      aListOfX1.addElement(new Short(rect.x2));
    }

    //Collections.sort(aListOfX1);
    aListOfX1=doSelectionSortvshort(aListOfX1);
    Vector aListOfX = new Vector(); // xs in python code
    for(int c=0; c<aListOfX1.size(); c++){
      Short d = (Short)aListOfX1.elementAt(c);
      if(!aListOfX.contains(d))
        aListOfX.addElement(d);
    }

    IntervalTree root = meit.buildIntervalTree(0, aListOfX.size()-1, aListOfX, null);
    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    meit.preOrderTraverse(root);
    meit.interval_tree_root = root;
    Short first = (Short)aListOfX.elementAt(0);
    Short last = (Short)aListOfX.elementAt(aListOfX.size() - 1);
    root.window = new Window(first.shortValue(), last.shortValue(),
                             (short) 0, (short) 0);

    slabFile= meit.maxEnclosing(myRectangles, coverage, root);
    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

    Vector hintervals = new Vector();
    Enumeration en = slabFile.keys();
    while(en.hasMoreElements()){
      Short sfk = (Short)en.nextElement();
      Window sf = (Window) slabFile.get(sfk);
      hintervals.addElement(sf);
    }
    //adding from previous slab
    for(int c=0; c<osf.hintervals.size(); c++){
      Window sf = (Window) osf.hintervals.elementAt(c);
      if(sf.h>=coverage_height){
        hintervals.addElement(sf);
      }
    }
    //Collections.sort(hintervals);
    hintervals=doSelectionSortvwind(hintervals);
    /* for(Window h : hintervals){
       System.out.println(h.h+"-----"+h.l+ " "+h.r+" "+h.score);
       }*/


    Vector values = new Vector();
    short j=2;
    int limit = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
    for(int k=0; k<limit; k++, j--){
      Objects obj = (Objects)myObjects.elementAt(j);
      values.addElement(new Short(obj.weight));
      obj = (Objects)myObjects.elementAt(j+3);
      values.addElement(new Short(obj.weight));
      obj = (Objects)myObjects.elementAt(j+6);
      values.addElement(new Short(obj.weight));
    }

    DistSlabfile result = new DistSlabfile(hintervals, values);
    return result;
  }

  public static DistSlabfile processingC_3(DistSlabfile osf){
    Vector myObjectIds = new Vector();
    Vector myObjects = new Vector();
    Vector myRectangles = new Vector();
    Hashtable slabFile = new Hashtable();
    Meit meit = new Meit();
    processingAll();
    short coverage_width=(short)Math.max((Constants.getNodeLocation((short)21).x-(coverage.width/2)), 0);

    initializeC_3(myObjectIds, myObjects, myRectangles, osf, coverage_width);


    Vector aListOfX1 = new Vector();
    for(int i = 0; i < myRectangles.size(); i++)
    {
      Rectangle rect = (Rectangle)myRectangles.elementAt(i);
      aListOfX1.addElement(new Short(rect.x1));
      aListOfX1.addElement(new Short(rect.x2));
    }

    //Collections.sort(aListOfX1);
    aListOfX1=doSelectionSortvshort(aListOfX1);
    Vector aListOfX = new Vector(); // xs in python code
    for(int c=0; c<aListOfX1.size(); c++){
      Short d = (Short)aListOfX1.elementAt(c);
      if(!aListOfX.contains(d))
        aListOfX.addElement(d);
    }

    IntervalTree root = meit.buildIntervalTree(0, aListOfX.size()-1, aListOfX, null);
    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    meit.preOrderTraverse(root);
    meit.interval_tree_root = root;
    Short first = (Short)aListOfX.elementAt(0);
    Short last = (Short)aListOfX.elementAt(aListOfX.size() - 1);
    root.window = new Window(first.shortValue(), last.shortValue(),
                             (short) 0, (short) 0);

    slabFile= meit.maxEnclosing(myRectangles, coverage, root);
    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

    Vector hintervals = new Vector();
    Enumeration en = slabFile.keys();
    while(en.hasMoreElements()){
      Short sfk = (Short)en.nextElement();
      Window sf = (Window) slabFile.get(sfk);
      hintervals.addElement(sf);
    }

    //Collections.sort(hintervals);
    hintervals=doSelectionSortvwind(hintervals);
    /* for(Window h : hintervals){
       System.out.println(h.h+"-----"+h.l+ " "+h.r+" "+h.score);
       }*/

    Vector values = new Vector();
    short j=0;
    int limit = (coverage.height/Constants.GAP_HEIGHT)<=3?(coverage.height/Constants.GAP_HEIGHT):3;
    for(int k=0; k<limit; k++){
      Objects obj = (Objects)myObjects.elementAt(j++);
      values.addElement(new Short(obj.weight));
      obj = (Objects)myObjects.elementAt(j++);
      values.addElement(new Short(obj.weight));
      obj = (Objects)myObjects.elementAt(j++);
      values.addElement(new Short(obj.weight));
    }

    int limit2 = (coverage.width/Constants.GAP_WIDTH)<=3?(coverage.width/Constants.GAP_WIDTH):3;
    for(int i=0; i<limit2; i++){
      for(j=0;j<limit; j++){
        Objects obj = (Objects)myObjects.elementAt(9+(i*3+j));
        values.addElement(new Short(obj.weight));
      }
    }

    DistSlabfile result = new DistSlabfile(hintervals, values);
    return result;
  }

  public static Window processingC_1(DistSlabfile osf1,DistSlabfile osf2){
    Vector myObjectIds = new Vector();
    Vector myObjects = new Vector();
    Vector myRectangles = new Vector();
    Hashtable slabFile = new Hashtable();
    Meit meit = new Meit();
    processingAll();
    short coverage_width=(short)Math.max((Constants.getNodeLocation((short)3).x-(coverage.width/2)), 0);
    short coverage_height=(short)Math.min((Constants.getNodeLocation((short)15).y+(coverage.height/2)), area.height);

    initializeC_1(myObjectIds, myObjects, myRectangles, osf1, osf2, coverage_height, coverage_width);


    Vector aListOfX1 = new Vector();
    for(int i = 0; i < myRectangles.size(); i++)
    {
      Rectangle rect = (Rectangle)myRectangles.elementAt(i);
      aListOfX1.addElement(new Short(rect.x1));
      aListOfX1.addElement(new Short(rect.x2));
    }

    //Collections.sort(aListOfX1);
    aListOfX1=doSelectionSortvshort(aListOfX1);
    Vector aListOfX = new Vector(); // xs in python code
    for(int c=0; c<aListOfX1.size(); c++){
      Short d = (Short)aListOfX1.elementAt(c);
      if(!aListOfX.contains(d))
        aListOfX.addElement(d);
    }

    IntervalTree root = meit.buildIntervalTree(0, aListOfX.size()-1, aListOfX, null);
    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    meit.preOrderTraverse(root);
    meit.interval_tree_root = root;
    Short first = (Short)aListOfX.elementAt(0);
    Short last = (Short)aListOfX.elementAt(aListOfX.size() - 1);
    root.window = new Window(first.shortValue(), last.shortValue(),
                             (short) 0, (short) 0);

    slabFile= meit.maxEnclosing(myRectangles, coverage, root);
    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

    Vector hintervals = new Vector();
    Enumeration en = slabFile.keys();
    while(en.hasMoreElements()){
      Short sfk = (Short)en.nextElement();
      Window sf = (Window) slabFile.get(sfk);
      hintervals.addElement(sf);
    }

    System.out.println("------------------------------------------");
    //adding from previous slab (c-3) (c-2)
    for(int c=0; c<osf2.hintervals.size(); c++){
      Window sf = (Window) osf2.hintervals.elementAt(c);
      System.out.println(sf.h+"-----"+sf.l+" "+sf.r+" "+sf.score);
      if(sf.h>=coverage_height){
        hintervals.addElement(sf);
      }
    }
    System.out.println("------------------------------------------");
    //adding from previous slab (c-0)
    for(int c=0; c<osf1.hintervals.size(); c++){
      Window sf = (Window) osf1.hintervals.elementAt(c);
      System.out.println(sf.h+"-----"+sf.l+" "+sf.r+" "+sf.score);
      if(sf.r<=coverage_width){
        hintervals.addElement(sf);
      }
      else{
        if(sf.l<coverage_width){
          sf.r=coverage_width;
          hintervals.addElement(sf);
        }
      }
    }

    //Collections.sort(hintervals);
    hintervals=doSelectionSortvwind(hintervals);
    /* for(Window h : hintervals){
       System.out.println(h.h+"-----"+h.l+ " "+h.r+" "+h.score);
       }*/
    Window opt_window = new Window((short)0, (short)0, (short)0, (short)0);
    for(int c=0; c<hintervals.size(); c++){
      Window h = (Window) hintervals.elementAt(c);
      if(h.score>opt_window.score){
        opt_window = h;
      }
    }
    return opt_window;
  }

  static Area computeCoverage(int Energy){
    //do compute the size of the rectangle from Energy threshold given by the user
    //Always make the area EVEN!!!!!!!!!!!!!!!!!!!!!!!
    return new Area((short)300, (short)300);
  }
  //// sorting objects based on its type
  public static Vector doSelectionSortvobj(Vector arr){

    for (int i = 0; i < arr.size() - 1; i++)
    {
      int index = i;
      for (int j = i + 1; j < arr.size(); j++)
      {
        Objects o_j = (Objects) arr.elementAt(j);
        Objects o_index = (Objects) arr.elementAt(index);
        if (o_j.compareTo(o_index) < 0)
          index = j;
      }
      Objects smallerNumber = (Objects) arr.elementAt(index);
      // setElementAt(Objects obj, int index) Sets the component at the specified index of this vector to be the specified object.
      arr.setElementAt(arr.elementAt(i), index); // arr[index] = arr[i];
      arr.setElementAt(smallerNumber, i); // arr[i] = smallerNumber;
    }

    return arr;
  }

  public static Vector doSelectionSortvwind(Vector arr){

    for (int i = 0; i < arr.size() - 1; i++)
    {
      int index = i;
      for (int j = i + 1; j < arr.size(); j++)
      {
        Window o_j = (Window) arr.elementAt(j);
        Window o_index = (Window) arr.elementAt(index);
        if (o_j.compareTo(o_index) < 0)
          index = j;
      }
      Window smallerNumber = (Window) arr.elementAt(index);
      // setElementAt(Objects obj, int index) Sets the component at the specified index of this vector to be the specified object.
      arr.setElementAt(arr.elementAt(i), index); // arr[index] = arr[i];
      arr.setElementAt(smallerNumber, i); // arr[i] = smallerNumber;
    }

    return arr;
  }

  public static Vector doSelectionSortvshort(Vector arr){

    for (int i = 0; i < arr.size() - 1; i++)
    {
      int index = i;
      for (int j = i + 1; j < arr.size(); j++)
      {
        Short o_j = (Short) arr.elementAt(j);
        Short o_index = (Short) arr.elementAt(index);
        if (o_j.shortValue() <  o_index.shortValue())
          index = j;
      }
      Short smallerNumber = (Short) arr.elementAt(index);
      // setElementAt(Objects obj, int index) Sets the component at the specified index of this vector to be the specified object.
      arr.setElementAt(arr.elementAt(i), index); // arr[index] = arr[i];
      arr.setElementAt(smallerNumber, i); // arr[i] = smallerNumber;
    }

    return arr;
  }
}
