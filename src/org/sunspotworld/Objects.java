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
public class Objects
{
  short x;
  short y;
  short weight;

  public Objects(short x, short y, short weight)
  {
    this.x = x;
    this.y = y;
    this.weight = weight;
  }

  public int compareTo(Objects o)
  { // used in the collections.sort()
    if(this.y > o.y)
      return 1;  // more than the one we are checking 
    else if(this.y == o.y){
        if(this.x>o.x){
            return 1;
        }
        else if(this.x<o.x){
            return -1;
        }
        return 0;  // equal to the one we are checking 
    }
    else
      return -1; // less then the one we are checking 
  }
}
