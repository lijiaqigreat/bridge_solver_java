/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

/**
 * i need: xy, bundleIndex, bundleType, cost, transform without GO
 *
 * @author jiaqi
 */
public class WorkStation {
    
    Optimizer optimizer;
    int threadn;
    Random random;
    TreeMap<String, Integer> map;
    Seed[] seeds;
    Analyzer[] analyzers;
    /**
     * 0:wait for analyze 1:assigned 3:analyzing 4:finished
     */
    int[] status;
    int nextSeed;
    final Object nextSeedLock;
    int mainStatus;
    double globalMinTotalCost=1000000;
    
    Bridge minBridge=null;

    public WorkStation(Optimizer opt, int threadn, Bridge[] source) {
        optimizer = opt;
        random = new Random();
        map = new TreeMap<String,Integer>();
        Type[] types = opt.types;
        for (int t = 0; t < types.length; t++) {
            map.put(types[t].name, t);
        }
        int seedSize = opt.seedSize;
        seeds = new Seed[seedSize];
        status=new int[seedSize];
        //xy1=new double[seedSize][opt.prebridge.getFreeJointSize()*2];
        //xy2=new double[seedSize][opt.prebridge.getFreeJointSize()*2];
        //hintType=new int[seedSize][opt.prebridge.getMemberSize()];
        //costs=new double[seedSize];
        for (int t1 = 0; t1 < seedSize; t1++) {
            seeds[t1] = new Seed(source[t1 % source.length], this);
        }
        nextSeed = 0;
        nextSeedLock = new Object();
    }

    public int getNextSeed() {
        synchronized (nextSeedLock) {
            while (true) {
                nextSeed++;
                nextSeed%=seeds.length;
                if (status[nextSeed] == 0) {
                    status[nextSeed] = 1;
                    return nextSeed;
                }
            }
        }
    }

    public Seed getRandomSeed() {
        return seeds[random.nextInt(seeds.length)];
    }
    public void recieve(Seed save,int index){
        Seed seed=seeds[index];
        seed.replace(save);
        if(seed.cost<globalMinTotalCost){
            globalMinTotalCost=seed.cost;
            
            minBridge=seed.getBridge(optimizer.name+"_"+(int)(seed.cost));
            
            minBridge.write(System.out);
            System.out.println("Cost: "+seed.cost);
        }
        status[index]=0;
    }
}
