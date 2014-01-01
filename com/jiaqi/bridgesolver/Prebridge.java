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
public class Prebridge implements Managable {
    private String name;
    private Condition condition;
    private double cost;
    /**
     * total jointSize
     */
    private int jointSize;
    private int[] j12;

    @Override
    public String getName() {
        return name;
    }

    public Condition getCondition(){
        return condition;
    }
    public int getJointSize(){
        return jointSize;
    }
    public int getFreeJointSize(){
        return jointSize-condition.getXY().length/2;
    }
    public int[] getJ12(){
        return j12;
    }
    
    @Override
    public String getTypeName() {
        return "Prebridge";
    }

    @Override
    public String read(Scanner in) {
        String[] ss=in.nextLine().split(" ");
        name=ss[0];
        condition=Inventory.conditions.get(ss[1]);
        if(condition==null){
            return "cannot find condition: "+ss[1]+"\n";
        }
        ss=in.nextLine().split(" ");
        jointSize=Integer.parseInt(ss[0]);
        j12=new int[Integer.parseInt(ss[1])*2];
        cost=Double.parseDouble(ss[2]);
        try{
            for(int t=0;t<j12.length;t+=2){
                ss=in.nextLine().split(" ");
                j12[t]=Integer.parseInt(ss[0]);
                j12[t+1]=Integer.parseInt(ss[1]);
                if(j12[t]>=jointSize||j12[t]<0||j12[t+1]>=jointSize||j12[t+1]<0){
                    throw new Exception("joint index out of range");
                }
            }
        }catch(Exception e){
            return e.toString();
        }
        return Inventory.MESSAGE_SUCCESS;
    }

    @Override
    public void write(PrintStream out) {
        out.println(name+" "+condition.getName()+"\n"+jointSize+" "+j12.length/2+" "+cost);
        for(int t=0;t<j12.length;t+=2){
            out.println(j12[t]+" "+j12[t+1]);
        }
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
        return new Prebridge();
    }

    int getMemberSize() {
        return j12.length/2;
    }

    double getCost() {
        return cost;
    }
    
}
