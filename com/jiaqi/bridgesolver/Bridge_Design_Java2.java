/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

import java.util.*;

import java.nio.*;
import java.io.*;

/**
 *
 * @author jiaqi
 */
public class Bridge_Design_Java2 {

    private static Bridge bridge;
    private static WorkStation workStation;
    private static Analyzer analyzer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File f = new File("com/jiaqi/bridgesolver/sample2.txt");
        Inventory.load(f, System.out);
        //System.out.println(Inventory.types.listAll());
        //System.out.println((-3)/2);
        f = new File("com/jiaqi/bridgesolver/sample.txt");
        Scanner s=null;
        try {
            s = new Scanner(f);
        } catch (FileNotFoundException ex) {
        }
       
        s.nextLine();
        Bridge2 bridge=new Bridge2();
        bridge.read(s);
        Analyzer a=new Analyzer();
        a.load(bridge);
        a.solve();
        long[] times=new long[10];
        times[0]=System.currentTimeMillis();
        for(int t=0;t<50;t++){
         a.solve();
        }
        times[1]=System.currentTimeMillis();
        a.optimize2(8,5);
        String fs;
        fs="";
        for(int x:a.bundleIndex){
         fs+=" "+x;
        }
        System.out.println(fs.substring(1));
        writeTask(a,"optimizeTask.dat");
        fs="";
        for(int x:a.bundleIndex){
         fs+=" "+x;
        }
        System.out.println(fs.substring(1));
        times[2]=System.currentTimeMillis();
        for(int t=0;t<50;t++){
         a.solve();
         a.optimize2(8,5);
        }
        times[3]=System.currentTimeMillis();
        fs="";
        for(int x:a.bundleIndex){
         fs+=" "+x;
        }
        int tmp=0;
        int[] tmpa=new int[10000];
        times[4]=System.currentTimeMillis();
        for(int t=0;t<10000000;t++){
         tmp+=tmpa[t%10000];
         tmpa[tmp&2047]+=1;
        }
        times[5]=System.currentTimeMillis();
        System.out.println(times[1]-times[0]);
        System.out.println(times[3]-times[2]);
        System.out.println(times[5]-times[4]);
        System.out.println(fs.substring(1));
        for(int t=0;t<a.bridge.memberSize;t++){
            //System.out.println(t+":("+a.bridge.memberLink[t*2]+","+a.bridge.memberLink[t*2+1]+"):\t"+a.minForce[t]+"\t"+a.maxForce[t]);
        }
    }

    public static void printTypes() {
        for (Type type : Inventory.types.getAll()) {
            System.out.print(type.name + " ");
        }
    }
    public static void writeTask(Analyzer a,String path){
     ByteBuffer f=ByteBuffer.allocate(4896).order(ByteOrder.LITTLE_ENDIAN);
     int typeSize=a.validTypeIndex.length;
     double[] cost=new double[typeSize];
     for(int t=0;t<typeSize;t++){
      System.out.println(Long.toHexString(a.reducedTypeTest[t]));
      f.putLong(a.reducedTypeTest[t]);
     }
     f.put(new byte[(256-typeSize)*8]);
     for(int t=0;t<a.bridge.memberSize;t++){
      f.putDouble(a.length[t]);
     }
     f.put(new byte[(64-a.bridge.memberSize)*8]);

     f.putDouble(0);
     for(int t=1;t<typeSize;t++){
      f.putDouble(Inventory.types2[a.validTypeIndex[t]].cost);
     }
     f.put(new byte[(256-typeSize)*8]);
     for(int t=0;t<typeSize;t++){
      f.put((byte)a.validTypeIndex[t]);
     }
     f.put(new byte[(256-typeSize)*1]);
     f.putInt(typeSize);
     f.putInt(a.bridge.memberSize);
     f.putDouble(0);
     f.putDouble(1000);
     f.putDouble(5);
     try {
      OutputStream output = null;
      try {
        output = new BufferedOutputStream(new FileOutputStream(path));
        output.write(f.array());
      }
      finally {
        output.close();
      }
     }
     catch(FileNotFoundException ex){
     }
     catch(IOException ex){
     }
    }

/*
    public static void test1() {
        Random random = new Random();
        long[] times = new long[4];
        for (int tt = 0; tt < 30; tt++) {
            times[0] += System.currentTimeMillis();
            analyzer.load(0);
            int transformI = Inventory.binarySearch(workStation.optimizer.weight, random.nextDouble());
            analyzer.transform(transformI, random.nextBoolean() ? 1 : -1);
            times[1] += System.currentTimeMillis();
            analyzer.solve();
            times[2] += System.currentTimeMillis();
            //analyzer.optimize(6, 5);
            analyzer.simple_optimize();
            times[3] += System.currentTimeMillis();
            String tempS = "Test " + tt + ": \n";
            for (int t = 0; t < analyzer.movableSize; t += 2) {
                tempS += t + 1 + ":\t" + analyzer.XY[t + analyzer.jointLoadIndex] + " " + analyzer.XY[t + analyzer.jointLoadIndex + 1] + "\n";
            }
            System.out.print(tempS);
            String[] tempSS = new String[analyzer.bundleTypes.length];
            for (int t = 0; t < tempSS.length; t++) {
                tempSS[t] = analyzer.types[analyzer.bundleTypes[t]].name + "\t";
            }
            for (int t = 0; t < analyzer.memberSize; t++) {
                tempSS[analyzer.memberBundleIndex[t]] += t + 1 + " ";
            }
            for (int t = 0; t < tempSS.length; t++) {
                System.out.println(tempSS[t]);
            }
            System.out.println("Cost: " + analyzer.totalCost + "\n");
        }
        String tempS = "times:";
        for (int t = 1; t < times.length; t++) {
            tempS += " " + (times[t] - times[t - 1]);
        }
        System.out.println(tempS);
        String[] tempSS = new String[analyzer.bundleTypes.length];
        for (int t = 0; t < tempSS.length; t++) {
            tempSS[t] = analyzer.types[analyzer.bundleTypes[t]].name + "\t";
        }
        for (int t = 0; t < analyzer.memberSize; t++) {
            tempSS[analyzer.memberBundleIndex[t]] += t + 1 + " ";
        }
        for (int t = 0; t < tempSS.length; t++) {
            System.out.println(tempSS[t]);
        }
    }

    public static void test2() {
        double startY = analyzer.startY;
        double endY = analyzer.endY;
        double[] minX = analyzer.minX;
        double[] maxX = analyzer.maxX;
        String s = "y range: (" + startY + "," + endY + ")\n";
        for (int t = 0; t < minX.length; t++) {
            s += t * Inventory.gridStep + startY + ": " + minX[t] + "," + maxX[t] + "\n";
        }
        System.out.println(s);
    }

    public static void test3() {
        Analyzer[] as = new Analyzer[3];
        for (int t = 0; t < 3; t++) {
            as[t] = new Analyzer(workStation);
        }
        for (int t = 0; t < 3; t++) {
            Thread thread = new Thread(as[t]);
            thread.start();
        }
    }

    public static void test4() {
        String f = "";
        for (Type t : Inventory.types.getAll()) {
            char[] name = t.name.toCharArray();
            char tempC = name[0];
            name[0] = name[1];
            name[1] = tempC;

            f += "<bs:Type><name>" + String.copyValueOf(name) + "</bs:name>"
                    + "<bs:E>" + t.E + "</bs:E>"
                    + "<bs:Fy>" + t.Fy + "</bs:Fy>"
                    + "<bs:Density>" + t.density + "</bs:Density>"
                    + "<bs:Area>" + t.area + "</bs:Area>"
                    + "<bs:Moment>" + t.moment + "</bs:Moment>"
                    + "<bs:CostVol>" + t.cost_vol + "</bs:CostVol>"
                    + "</bs:Type>\n";
        }
        System.out.println(f);
    }
*/
}
