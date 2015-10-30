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
    Vector hintervals = new Vector();
    Vector neededValues = new Vector();
    
    DistSlabfile(Vector hi, Vector nv){
        hintervals = hi;
        neededValues = nv;
    }
}
