/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

/**
 *
 * @author jiaqi
 */
public class Seed {
    final Object lock;
    //TODO nessecary?
    Optimizer optimizer;
    double[] xy;
    int[] memberTypes;
    int[] memberBundleIndex;
    int[] bundleTypes;
    int delay;
    double cost;
    public Seed(Bridge b, WorkStation workStation){
        lock=new Object();
        optimizer=workStation.optimizer;
        xy=b.getXY().clone();
        memberBundleIndex=b.bundleIndex.clone();
        
        //match type to index for bundleTypes
        //TODO inefficient matching method...
        Type[] _bundleTypes=b.bundleTypes;
        bundleTypes=new int[_bundleTypes.length];
        Type[] types=optimizer.types;
        for(int t1=0;t1<_bundleTypes.length;t1++){
            int a=0;
            for(;a<types.length;a++){
                if(_bundleTypes[t1]==types[a]){
                    bundleTypes[t1]=a;
                    break;
                }
            }
            if(a==types.length){
                bundleTypes[t1]=-1;
                //TODO ERROR: bundleType not found!
            }
        }
        memberTypes=new int[memberBundleIndex.length];
        //set memberTypes
        for(int t=0;t<memberTypes.length;t++){
            memberTypes[t]=bundleTypes[memberBundleIndex[t]];
        }
        delay=0;
        cost=1000000;
    }
    public Seed(Optimizer _optimizer, double[] _xy,
            int[] _memberTypes,int[] _memberBundleIndex,int[] _bundleTypes,
            double _cost,int _delay){
        lock=new Object();
        optimizer=_optimizer;
        xy=_xy.clone();
        memberTypes=_memberTypes.clone();
        memberBundleIndex=_memberBundleIndex.clone();
        bundleTypes=_bundleTypes.clone();
        cost=_cost;
        delay=_delay;
     }
    public Bridge getBridge(String name){
        Bridge f;
        synchronized(lock){
            Type[] _bundleType=new Type[bundleTypes.length];
            for(int t=0;t<bundleTypes.length;t++){
                _bundleType[t]=optimizer.types[bundleTypes[t]];
            }
            f=new Bridge(name,optimizer.prebridge,xy,memberBundleIndex,_bundleType,optimizer.deckType,optimizer.loadType);
        }
        return f;
    }
    public void replace(Seed seed){
        synchronized(lock){
            System.arraycopy(seed.xy,0,xy,0,xy.length);
            System.arraycopy(seed.memberTypes,0,memberTypes,0,memberTypes.length);
            System.arraycopy(seed.memberBundleIndex,0,memberBundleIndex,0,memberBundleIndex.length);
            bundleTypes=seed.bundleTypes.clone();
            cost=seed.cost;
            delay=seed.delay;
        }
    }
}
