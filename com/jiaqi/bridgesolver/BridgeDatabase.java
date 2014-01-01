package com.jiaqi.bridgesolver;

class BridgeDatabase{
 Bridge2 bridge;
 double globalMin;
 public BridgeDatabase(Bridge2 b){
  bridge=new Bridge2(b);
 }
 public Seed[] pull(int n){

 }
 public void push(PositionHint,TypeHint,cost,status){
  //remove from unfinished task
  //update neighbors and self to queue
   //update frequency
   //update priority
  //update status of self
  
 }
}
