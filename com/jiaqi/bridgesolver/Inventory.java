/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author jiaqi
 */
public class Inventory {
 public static final String MESSAGE_SUCCESS="Seccess!";
 public static final String VERSION="v0.2";
 public static final String MESSAGE_HEADER="Bridge Solver "+VERSION+" ";
 public static final int MESSAGE_HEADER_LENGTH=MESSAGE_HEADER.length();
 public static Type[] types2;
 public static Manager<Type> types=new Manager<Type>(new Type());
 public static Manager<Bridge> bridges=new Manager<Bridge>(new Bridge());
 public static Manager<Prebridge> prebridges=new Manager<Prebridge>(new Prebridge());
 public static Manager<Condition> conditions=new Manager<Condition>(new Condition());
 public static Manager<Optimizer> optimizers=new Manager<Optimizer>(new Optimizer());
 //TODO nessecary?
 public static int MaxBundleSize=8;
 /**
  * some constant used in strength formula
  * =pi^2
  */
 public static  double C=9.8696044;
 /**
  * little g
  */
 public static  double Gravity=9.8066;
 /**
  * minimum increment of position.
  */
 public static double gridStep=0.25;
 
 public static double TensionResistanceFactor=0.95;
 public static double CompressionResistanceFactor=0.9;
 public static double DeadLoadFactor=1.25;
 public static double LiveLoadFactor=1.75*1.33;
 /**
  * after considering all factors
  */
 public static double[] DeckWeight={
     DeadLoadFactor * 120.265 + 33.097,
     DeadLoadFactor * 82.608 + 33.097};
 /**
  * Unit: dollar/deck
  */
 public static double[] DeckCost={5150,5300};
 /**
  * after considering all factors
  */
 public static double[] FrontLoad={44*LiveLoadFactor,120*LiveLoadFactor};
 public static double[] BackLoad={181*LiveLoadFactor,120*LiveLoadFactor};
 public static double BundleCost=1000;
 public static double JointCost=600;
 
 public static void load(File file,PrintStream out){
  Scanner scanner;
  try{
      scanner=new Scanner(new FileInputStream(file));
  }catch(FileNotFoundException e){
      e.printStackTrace();
      return;
  }
  String f="loading from file:\n";
  int count=0;
  b1:
  while(scanner.hasNext()){
      String temp=scanner.nextLine();
      if(!temp.startsWith(MESSAGE_HEADER)){
          f+="Unable to resolve exceeding information.\n"
                  + "Header format does not match:\n"
                  + temp+"\n";
          break b1;
      }
      String tmpS=temp.substring(MESSAGE_HEADER_LENGTH);
      if(tmpS.equals("Type")){
          temp=types.load(scanner);
      }else if(tmpS.equals("Bridge")){
          temp=bridges.load(scanner);
      }else if(tmpS.equals("Prebridge")){
          temp=prebridges.load(scanner);
      }else if(tmpS.equals("Condition")){
          temp=conditions.load(scanner);
      }else if(tmpS.equals("Optimizer")){
          temp=optimizers.load(scanner);
      }else{
          f+="Cannot regonize the type\n";
          break b1;
      }
      /*
      switch(temp.substring(MESSAGE_HEADER_LENGTH)){
          case "Type":
              temp=types.load(scanner);
              break;
          case "Bridge":
              temp=bridges.load(scanner);
              break;
          case "Prebridge":
              temp=prebridges.load(scanner);
              break;
          case "Condition":
              temp=conditions.load(scanner);
              break;
          case "Optimizer":
              temp=optimizers.load(scanner);
              break;
          default:
              f+="Cannot regonize the type\n";
              break b1;
      }
      */
      
      if(temp.startsWith(MESSAGE_SUCCESS)){
          f+=temp;
      }else{
          f+="Unable to resolve exceeding information.\n"+temp;
          break b1;
      }
      count++;
  }
  Collection<Type> collection=types.getAll();
  types2=new Type[collection.size()];
  collection.toArray(types2);
  Arrays.sort(types2,new Comparator<Type>(){
   public int compare(Type t1,Type t2){
    return Double.compare(t1.cost,t2.cost);
   }
  });
  if(out==null){
      return;
  }
  out.println(f+"Loaded "+count+" items in total.\n");
 }
 
 public static int binarySearch(double[] array,double key){
     int min=0;
     int max=array.length;
     int mid;
     while(min<max-1){
         mid=(min+max)/2;
         if(key>=array[mid]){
             min=mid;
         }else{
             max=mid;
         }
     }
     return min;
 }
 public static double f1(double cost, double min){
     return 200*(cost-min)*(cost-min)/(min*min);
 }
 public static String getAfterColon(String input){
     return input.substring(input.indexOf(':')+1);
 }
 public static Type getTypeByName(String name){
  for(Type type:Inventory.types2){
   if(type.name.equals(name)){
    return type;
   }
  }
  return null;
 }
 
}
