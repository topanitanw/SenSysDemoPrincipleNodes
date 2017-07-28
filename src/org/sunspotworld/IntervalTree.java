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
public class IntervalTree
{
  double discriminant;
  IntervalTree left_child;
  IntervalTree right_child;
  Window window;
  short maxscore; // not sure
  Window target;
  short excess;   // not sure
  IntervalTree father;
  
  public IntervalTree(double discriminant, IntervalTree father)
  {
    this.discriminant = discriminant;
    this.left_child = null;
    this.right_child = null;
    this.window = null;
    this.maxscore = 0;
    this.target = null;
    this.excess = 0;
    this.father = father;
  }
  
  public String toString() {
    String txt = "|dis: " + this.discriminant;
    if(this.left_child != null)
      txt += " left_child: " + this.left_child.discriminant;
    else 
      txt += " left_child: null";
            
    if(this.right_child != null)
      txt += " right_child: " + this.right_child.discriminant + "|\n";
    else 
      txt += " right_child: null|\n";
    return txt;
  }  
}