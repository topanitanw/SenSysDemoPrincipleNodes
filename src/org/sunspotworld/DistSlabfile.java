/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;

import java.util.Vector;

/**
 *
 * @author Muhammed
 */
public class DistSlabfile {
    Vector hintervals = new Vector();  //Window
    Vector neededValues = new Vector();  //Short
    
    DistSlabfile(Vector hi, Vector nv){
        hintervals = hi;
        neededValues = nv;
    }
}
