/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 *
 * @author jiaqi
 */
public class Condition implements Managable {
    /**
     * 
     */
    String name="";
    double[] boundaryY;
    double[] boundaryXmin;
    double[] boundaryXmax;
    /**
     * xy's of the polygon
     */
    double[] boundary=null;
    /**
     * xy's of joints
     */
    double[] xy=null;
    int[] fixedIndex=null;
    int deckSize=0;
    int boundarySize;
    double slenderness=0;
    public Condition(){
    }
    @Override
    public String getName(){
        return name;
    }
    public double[] getXY(){
        return xy;
    }
    public double getSlenderness(){
        return slenderness;
    }
    public int getDeckSize(){
        return deckSize;
    }
    public int[] getFixedIndex(){
        return fixedIndex;
    }

    @Override
    public String read(Scanner in) {
        String[] ss=in.nextLine().split(" ");
        name=ss[0];
        xy=new double[Integer.parseInt(ss[1])*2];
        deckSize=Integer.parseInt(ss[2]);
        boundarySize=Integer.parseInt(ss[3]);
        slenderness=Double.parseDouble(ss[4]);
        ss=in.nextLine().split(" ");
        fixedIndex=new int[ss.length];
        for(int t=0;t<ss.length;t++){
            fixedIndex[t]=Integer.parseInt(ss[t]);
        }
        ss=in.nextLine().split(" ");
        boundaryY=new double[ss.length];
        boundaryXmin=new double[ss.length];
        boundaryXmax=new double[ss.length];
        for(int t=0;t<ss.length;t++){
            boundaryY[t]=Double.parseDouble(ss[t]);
        }
        ss=in.nextLine().split(" ");
        for(int t=0;t<ss.length;t++){
            boundaryXmin[t]=Double.parseDouble(ss[t]);
        }
        ss=in.nextLine().split(" ");
        for(int t=0;t<ss.length;t++){
            boundaryXmax[t]=Double.parseDouble(ss[t]);
        }
        for(int t=0;t<xy.length;t+=2){
            ss=in.nextLine().split(" ");
            xy[t]=Double.parseDouble(ss[0]);
            xy[t+1]=Double.parseDouble(ss[1]);
        }
        return Inventory.MESSAGE_SUCCESS;
    }

    @Override
    public void write(PrintStream out) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String read(InputStream in) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void write(OutputStream out) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTypeName() {
        return "Condition";
    }
    @Override
    public Object newSample(){
        return new Condition();
    }
    
}
