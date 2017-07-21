/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;

/**
 *
 * @author Muhammed
 */
public class Window
{ // root.window = Window(xs[0],xs[-1],-5,0)
  short l;
  short r;
  short h;
  short score;

  public Window(short l, short r, short h, short score)
  {
    this.l = l;
    this.r = r;
    this.h = h;
    this.score = score;
  }
  
  public Window clone()
  { //Deep copy
    Window clone = new Window(this.l, this.r, this.h, this.score);
    return clone;
  }

  
  public int compareTo(Window o)
  { // used in the collections.sort()
    if(this.h < o.h)
      return 1;  // more than the one we are checking 
    else if(this.h == o.h)
      return 0;  // equal to the one we are checking
    else
      return -1; // less then the one we are checking 
  }
}
