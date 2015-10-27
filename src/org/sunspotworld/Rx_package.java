/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld;

/**
 *
 * @author PanitanW
 */
public class Rx_package {
  private int pck_type = -1;
  private int node_index = -1;
  private String dst_addr = null;
  private int[] payload = null;

  public Rx_package(int pck_type, int node_index,
                    String dest_addr, int[] data)
  {
    this.pck_type = pck_type;
    this.node_index = node_index;
    
    if(dest_addr != null)
      this.dst_addr = new String(dest_addr);
    
    // DO NOT WORK this.payload = (int[]) data.clone();
    this.payload = new int[data.length];
    System.arraycopy(data, 0, this.payload, 0, data.length);    
  }

  public int get_pck_type() {
    return this.pck_type;
  }

  public int get_node_index() {
    return this.node_index;
  }

  public String get_dst_addr() {
    return this.dst_addr;
  }

  public int[] get_payload() {
    return this.payload;
  }

  public void set_payload(int i, int val) {
    payload[i] = val;
  }
}

