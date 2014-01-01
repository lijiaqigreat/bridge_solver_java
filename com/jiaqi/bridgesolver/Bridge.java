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
public class Bridge implements Managable {
    private String name;
    private Prebridge prebridge;
    private double[] xy;
    int[] bundleIndex;
    Type[] bundleTypes;
    //private Type[] types;
    private int deckType;
    private int loadType;
    
    public Bridge(){}
    public Bridge(String _name,Prebridge _prebridge, double[] _xy,
            int[] _bundleIndex, Type[] _bundleType, int _deckType, int _loadType){
        name=_name;
        prebridge=_prebridge;
        xy=_xy.clone();
        bundleIndex=_bundleIndex.clone();
        bundleTypes=_bundleType.clone();
        deckType=_deckType;
        loadType=_loadType;
    }
    public Bridge(Bridge b){
        name=b.name;
        prebridge=b.prebridge;
        xy=b.xy.clone();
        bundleIndex=b.bundleIndex.clone();
        bundleTypes=b.bundleTypes.clone();
        //types=b.types.clone();
        deckType=b.deckType;
        loadType=b.loadType;
    }
    
    
    @Override
    public String getName() {
        return name;
    }
    
    public Prebridge getPrebridge(){
        return prebridge;
    }
    public double[] getXY(){
        return xy;
    }
    public int getDeckType(){
        return deckType;
    }
    public int getLoadType(){
        return loadType;
    }

    @Override
    public String getTypeName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String read(Scanner in) {
        String[] ss=in.nextLine().split(" ");
        name=ss[0];
        prebridge=Inventory.prebridges.get(ss[1]);
        deckType=Integer.parseInt(ss[2]);
        loadType=Integer.parseInt(ss[3]);
        bundleTypes=new Type[Integer.parseInt(ss[4])];
        bundleIndex=new int[prebridge.getMemberSize()];
        for(int t=0;t<bundleTypes.length;t++){
            ss=in.nextLine().split(" ");
            bundleTypes[t]=Inventory.types.get(ss[0]);
            for(int tt=1;tt<ss.length;tt++){
                int ttt=Integer.parseInt(ss[tt]);
                bundleIndex[ttt]=t;
            }
        }
        xy=new double[prebridge.getFreeJointSize()*2];
        
        for(int t=0;t<xy.length;t+=2){
            ss=in.nextLine().split(" ");
            xy[t]=Double.parseDouble(ss[0]);
            xy[t+1]=Double.parseDouble(ss[1]);
        }
        return Inventory.MESSAGE_SUCCESS;
    }
    @Override
    public void write(PrintStream out) {
        out.println(name+" "+prebridge.getName()+" "+deckType+" "+loadType+" "+bundleTypes.length);
        String[] tempSS=new String[bundleTypes.length];
        for(int t=0;t<bundleTypes.length;t++){
            tempSS[t]=bundleTypes[t].name;
        }
        for(int t=0;t<bundleIndex.length;t++){
            tempSS[bundleIndex[t]]+=" "+t;
        }
        for(int t=0;t<bundleTypes.length;t++){
            out.println(tempSS[t]);
        }
        String s="";
        for(int t=0;t<xy.length;t+=2){
            s+=xy[t]+" "+xy[t+1]+"\n";
        }
        System.out.print(s);
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
    public Object newSample() {
        return new Bridge();
    }
    public void printCost(PrintStream out){
        out.print("Cost report:\n"
                + "prebridge: "+prebridge.getCost()+"\n"
                + "deck: "+prebridge.getCondition().deckSize*Inventory.DeckCost[deckType]+"\n"
                + "");
        
    }
    
}
