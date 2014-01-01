/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author jiaqi
 */
public class Bridge2 implements Managable {
 /**
  * only for display purpose
  */
 public String name;
 //condition
 public double[] boundingY;
 public double[] boundingminX;
 public double[] boundingmaxX;
 public Rectangle2D.Double boundingBox;
 public int fixedJointSize;
 public int deckSize;
 public int deckType;
 public int truckType;
 public double slenderness;
 /**
  * everything but bundleCost,jointCost,typeCost
  */
 public double baseCost;
 public int[] fixedIndex;

 public int totalJointSize;
 public int memberSize;
 public int[] memberLink;
 public double[] xy;
 public Type[] bundleType;
 public int[] bundleIndex;
 
 public Bridge2(){}
 //TODO
 public Bridge2(Bridge2 b){
  name=b.name;
  boundingY=b.boundingY.clone();
  boundingminX=b.boundingminX.clone();
  boundingmaxX=b.boundingmaxX.clone();
  boundingBox=(Rectangle2D.Double)b.boundingBox.clone();
  fixedJointSize=b.fixedJointSize;
  deckSize=b.deckSize;
  deckType=b.deckType;
  truckType=b.truckType;
  slenderness=b.slenderness;
  baseCost=b.baseCost;
  fixedIndex=b.fixedIndex.clone();
  totalJointSize=b.totalJointSize;
  memberSize=b.memberSize;
  memberLink=b.memberLink.clone();
  xy=b.xy.clone();
  bundleType=b.bundleType.clone();
  bundleIndex=b.bundleIndex.clone();
 }
 @Override
 public String getTypeName(){
  return "Bridge";
 }
 public String getName(){
  return this.name;
 }

 @Override
 public String read(Scanner in) {
  String tmpS;
  tmpS=in.nextLine();
  this.name=Inventory.getAfterColon(tmpS);
  tmpS=in.nextLine();
  this.fixedJointSize=Integer.parseInt(Inventory.getAfterColon(tmpS));
  tmpS=in.nextLine();
  this.deckSize=Integer.parseInt(Inventory.getAfterColon(tmpS));
  tmpS=in.nextLine();
  this.deckType=Integer.parseInt(Inventory.getAfterColon(tmpS));
  tmpS=in.nextLine();
  this.truckType=Integer.parseInt(Inventory.getAfterColon(tmpS));
  tmpS=in.nextLine();
  this.slenderness=Double.parseDouble(Inventory.getAfterColon(tmpS));
  tmpS=in.nextLine();
  this.baseCost=Double.parseDouble(Inventory.getAfterColon(tmpS));
  tmpS=in.nextLine();
  this.totalJointSize=Integer.parseInt(Inventory.getAfterColon(tmpS));
  tmpS=in.nextLine();
  this.memberSize=Integer.parseInt(Inventory.getAfterColon(tmpS));
  //boundingBox:
  tmpS=in.nextLine();

  String[] tmpSs1=in.nextLine().split(" ");
  String[] tmpSs2=in.nextLine().split(" ");
  String[] tmpSs3=in.nextLine().split(" ");
  int tmpSize=tmpSs1.length;
  this.boundingY=new double[tmpSize];
  this.boundingminX=new double[tmpSize];
  this.boundingmaxX=new double[tmpSize];
  for(int t=0;t<tmpSize;++t){
   this.boundingY[t]=Double.parseDouble(tmpSs1[t]);
   this.boundingminX[t]=Double.parseDouble(tmpSs2[t]);
   this.boundingmaxX[t]=Double.parseDouble(tmpSs3[t]);
  }
  tmpSs1=in.nextLine().split(" ");
  double[] tmpD=new double[4];
  for(int t=0;t<4;++t){
   tmpD[t]=Double.parseDouble(tmpSs1[t]);
  }
  this.boundingBox=new Rectangle2D.Double(tmpD[0],tmpD[1],tmpD[2],tmpD[3]);

  //fixedIndex:
  tmpS=in.nextLine();

  tmpSs1=in.nextLine().split(" ");
  tmpSize=tmpSs1.length;
  this.fixedIndex=new int[tmpSize];
  for(int t=0;t<tmpSize;++t){
   this.fixedIndex[t]=Integer.parseInt(tmpSs1[t]);
  }

  //memberLink:
  tmpS=in.nextLine();
  
  this.memberLink=new int[this.memberSize*2];
  for(int t=0;t<this.memberSize;++t){
   tmpSs1=in.nextLine().split(" ");
   this.memberLink[t*2  ]=Integer.parseInt(tmpSs1[0]);
   this.memberLink[t*2+1]=Integer.parseInt(tmpSs1[1]);
  }

  //jointXY:
  tmpS=in.nextLine();

  this.xy=new double[this.totalJointSize*2];
  for(int t=0;t<this.totalJointSize;++t){
   tmpSs1=in.nextLine().split(" ");
   this.xy[t*2  ]=Double.parseDouble(tmpSs1[0]);
   this.xy[t*2+1]=Double.parseDouble(tmpSs1[1]);
  }

  //bundle:
  tmpS=in.nextLine();

  tmpSs1=in.nextLine().split(" ");
  tmpSize=tmpSs1.length;
  bundleType=new Type[tmpSize];
  for(int t=0;t<tmpSize;++t){
   bundleType[t]=Inventory.getTypeByName(tmpSs1[t]);
  }
  tmpS=in.nextLine();
  tmpSs1=tmpS.split(" ");
  bundleIndex=new int[this.memberSize];
  for(int t=0;t<this.memberSize;++t){
   bundleIndex[t]=Integer.parseInt(tmpSs1[t]);
  }
  
  return Inventory.MESSAGE_SUCCESS;
 }
 @Override
 public void write(PrintStream out) {
  String f=Inventory.MESSAGE_HEADER+"Bridge\n"+
  "name:"+name+"\n"+
  "fixedJointSize:"+fixedJointSize+"\n"+
  "deckSize:"+deckSize+"\n"+
  "deckType:"+deckType+"\n"+
  "truckType:"+truckType+"\n"+
  "slenderness:"+slenderness+"\n"+
  "baseCost:"+baseCost+"\n"+
  "totalJointSize:"+totalJointSize+"\n"+
  "memberSize:"+memberSize+"\n"+
  "boundingBox:\n";
  String tmp1="";
  String tmp2="";
  String tmp3="";
  int tmpSize=this.boundingY.length;
  for(int t=0;t<tmpSize;++t){
   tmp1+=" "+this.boundingY[t];
   tmp2+=" "+this.boundingminX[t];
   tmp3+=" "+this.boundingmaxX[t];
  }
  f+=tmp1.substring(1)+"\n";
  f+=tmp2.substring(1)+"\n";
  f+=tmp3.substring(1)+"\n";
  f+=this.boundingBox.x+" "+this.boundingBox.y+" "+this.boundingBox.width+" "+this.boundingBox.height+"\n";

  f+="fixedIndex:\n"; 
  tmp1="";
  for(int index:this.fixedIndex){
   tmp1+=" "+index;
  }
  f+=tmp1.substring(1)+"\n";

  f+="memberLink:\n";
  for(int t=0;t<this.memberSize;++t){
   f+=this.memberLink[t*2]+" "+this.memberLink[t*2+1]+"\n";
  }

  f+="jointXY:\n";
  for(int t=0;t<this.totalJointSize;++t){
   f+=this.xy[t*2]+" "+this.xy[t*2+1]+"\n";
  }

  f+="bundle:\n";
  tmp1="";
  for(Type type:this.bundleType){
   tmp1+=" "+type.name;
  }
  f+=tmp1.substring(1)+"\n";
  tmp1="";
  for(int t=0;t<this.memberSize;++t){
   tmp1+=" "+this.bundleIndex[t];
  }
  f+=tmp1.substring(1)+"\n";
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
}
