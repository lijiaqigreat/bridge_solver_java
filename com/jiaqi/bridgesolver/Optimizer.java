/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author jiaqi
 */
public class Optimizer implements Managable {
    String name;
    /**
     * prebridge that it's based on.
     */
    Prebridge prebridge;
    int transformSize;
    int seedSize;
    double gamma;
    int maxBundle;
    double[] operationWeight;
    /**
     * allowed deckType to choose
     */
    int deckType;
    /**
     * allow loadType to choose
     */
    int loadType;
    /**
     * index of load to measure
     */
    int[] loadIndexes;
    Type[] types;
    
    /**
     * statistical weight for each variable
     */
    double[] weight;
    /**
     * transform operations to bridge
     */
    double[][] transform;
    
    public Optimizer(){}
    public Optimizer(Optimizer opt){
        name=opt.name;
        prebridge=opt.prebridge;
        transformSize=opt.transformSize;
        seedSize=opt.seedSize;
        gamma=opt.gamma;
        operationWeight=opt.operationWeight;
        deckType=opt.deckType;
        loadType=opt.loadType;
        loadIndexes=opt.loadIndexes;
        types=opt.types;
        weight=opt.weight;
        transform=opt.transform;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTypeName() {
        return "Optimizer";
        
    }

    @Override
    public String read(Scanner in) {
        String[] ss=in.nextLine().split(" ");
        name=ss[0];
        prebridge=Inventory.prebridges.get(ss[1]);
        transformSize=Integer.parseInt(ss[2]);
        seedSize=Integer.parseInt(ss[3]);
        gamma=Double.parseDouble(ss[4]);
        maxBundle=Integer.parseInt(ss[5]);
        deckType=Integer.parseInt(ss[6]);
        loadType=Integer.parseInt(ss[7]);
        
        ss=in.nextLine().split(" ");
        operationWeight=new double[2];
        double temp=0;
        for(int t=0;t<operationWeight.length;t++){
            operationWeight[t]=temp;
            temp+=Double.parseDouble(ss[t]);
        }
        for(int t=0;t<operationWeight.length;t++){
            operationWeight[t]/=temp;
        }
        
        ss=in.nextLine().split(" ");
        loadIndexes=new int[ss.length];
        for(int t=0;t<loadIndexes.length;t++){
            loadIndexes[t]=Integer.parseInt(ss[t]);
        }
        
        ss=in.nextLine().split(" ");
        types=new Type[ss.length];
        for(int t=0;t<types.length;t++){
            types[t]=Inventory.types.get(ss[t]);
        }
        Arrays.sort(types,new Comparator<Type>(){

            @Override
            public int compare(Type o1, Type o2) {
                return Double.compare(o1.cost, o2.cost);
            }
            
        });
        
        int variableSize=prebridge.getFreeJointSize()*2;
        transform=new double[transformSize][variableSize];
        weight=new double[transformSize];
        temp=0;
        for(int t1=0;t1<transformSize;t1++){
            ss=in.nextLine().split(" ");
            weight[t1]=temp;
            temp+=Double.parseDouble(ss[0]);
            for(int t2=0;t2<variableSize;t2++){
                transform[t1][t2]=Double.parseDouble(ss[t2+1]);
            }
        }
        for(int t1=0;t1<transformSize;t1++){
            weight[t1]/=temp;
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
    public Object newSample() {
        return new Optimizer();
    }
    
}
