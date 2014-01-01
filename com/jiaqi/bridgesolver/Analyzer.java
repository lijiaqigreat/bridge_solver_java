/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

import java.util.Random;

/**
 *
 * @author jiaqi
 */
public class Analyzer {

 Bridge2 bridge;
 int equationSize;
 int memberSize;
 /**
  * coordinate of all joints
  */
 double[] XY;
 /**
  * types of each bundle
  */
 Type[] bundleType;
 /**
  * bundle index of each member
  */
 int[] bundleIndex;
 /**
  * main matrix to be calculated
  */
 double[][] matrix=new double[0][];
 /**
  * length of each memeber
  */
 double[] length;
 /**
  * weight on each joints
  */
 double[] loads;
 /**
  * displacement of each variable in each load cases
  */
 double[][] jdisplacement;
 /**
  * force of each member in each load cases + means tension - means
  * compression
  */
 double[][] memberForce;
 /**
  * maxForce is always positive minForce is always negative
  */
 double[] maxForce, minForce;
 double baseCost;
 double totalCost;
 double startY;
 double endY;
 double[] minX;
 double[] maxX;
 double globalMinTotalCost;
 /**
  * //TODO define modes wait load transform solve optimize (override) save
  * 0: good
  * 1: unstable
  */
 int status;
 

 long[] reducedTypeTest;
 //index of each validType
 int[] validTypeIndex;

 public Analyzer(){}
 public void load(Bridge2 b) {
  bridge=new Bridge2(b);
  double[] tempDs;
  equationSize=b.totalJointSize*2;
  memberSize=b.memberSize;
  XY=b.xy.clone();
  bundleType = b.bundleType.clone();
  bundleIndex= b.bundleIndex.clone();
  if(matrix.length!=equationSize){
   matrix = new double[equationSize][equationSize];
  }
  length = new double[memberSize];
  loads = new double[b.totalJointSize];
  jdisplacement = new double[b.deckSize + 1][equationSize];
  memberForce = new double[b.deckSize+ 1][memberSize];
  maxForce = new double[memberSize];
  minForce = new double[memberSize];


/*
  Condition condition = w.optimizer.prebridge.getCondition();
  double[] y = condition.boundaryY;
  double[] x1 = condition.boundaryXmin;
  double[] x2 = condition.boundaryXmax;
  startY = y[0];
  endY = y[y.length - 1] + 0.01;
  minX = new double[(int) ((endY - startY) / Inventory.gridStep) + 1];
  maxX = new double[minX.length];
  minX[0] = x1[0];
  maxX[0] = x2[0];
  int t2 = 0;
  for (int t1 = 0; t1 < minX.length; t1++) {
      double _y = t1 * Inventory.gridStep + startY;
      if (y[t2 + 1] < _y) {
          t2++;
      }
      minX[t1] = x1[t2] + (x1[t2 + 1] - x1[t2]) * (_y - y[t2]) / (y[t2 + 1] - y[t2]) - 0.01;
      maxX[t1] = x2[t2] + (x2[t2 + 1] - x2[t2]) * (_y - y[t2]) / (y[t2 + 1] - y[t2]) + 0.01;
  }
*/

 }
 public void loadSeed(double[] _positionHint,Type[] _bundleType,int[] _bundleIndex){
  //TODO delete this line? faster but less safe
  System.arraycopy(bridge.xy,0,XY,0,XY.length);
  int offset=bridge.fixedJointSize*2;
  System.arraycopy(_positionHint,0,XY,offset,equationSize-offset);
  bundleType=_bundleType.clone();
  System.arraycopy(_bundleIndex,0,bundleIndex,0,memberSize);
 }

/*
 @Override
 public void run() {
     int count = 0;
     while (count < 200000) {
         status=0;
         load(workStation.getNextSeed());
         int transformI;
         boolean transformFail = true;
         while (transformFail) {
             transformI = Inventory.binarySearch(optimizer.weight, random.nextDouble());
             transformFail = !transform(transformI, random.nextBoolean() ? 1 : -1);
         }

         solve();
         simple_optimize();
         if (delay > 30||totalCost<globalMinTotalCost+2000){
             optimize(6, 5);
             delay = 0;
         }
         Seed save;
         if (status==1||(totalCost > globalMinTotalCost && Inventory.f1(totalCost, globalMinTotalCost) > random.nextDouble())) {
             save = workStation.getRandomSeed();
         } else {
             save = getSeed();
         }
         
         workStation.recieve(save, index);
         count++;
     }
 }
*/
 public void solve() {
     //System.arraycopy(workStation.hintType[index],0,memberTypes,0,memberSize);

     //reset matrix
     for (int t1 = 0; t1 < equationSize; t1++) {
         for (int t2 = 0; t2 < equationSize; t2++) {
             matrix[t1][t2] = 0;
         }
     }

     //reset loads[0]
     for (int t1 = 0; t1 < bridge.totalJointSize; t1++) {
         loads[t1] = 0;
     }

     //add deckWeight to loads[0]
     double deckWeight=Inventory.DeckWeight[bridge.deckType];
     for (int t1 = 1; t1 < bridge.deckSize; t1++) {
         loads[t1] -= deckWeight;
     }
     loads[0] -= deckWeight / 2;
     loads[bridge.deckSize] -= deckWeight / 2;

     //iterate members
     int[] j12=bridge.memberLink;
     for (int t = 0; t <memberSize; t++) {
         double mx = XY[j12[t * 2 + 1] * 2] - XY[j12[t * 2] * 2];
         double my = XY[j12[t * 2 + 1] * 2 + 1] - XY[j12[t * 2] * 2 + 1];
         length[t] = Math.sqrt(mx * mx + my * my);
         int j1x = j12[2 * t] * 2;
         int j2x = j12[2 * t + 1] * 2;
         int j1y = j1x + 1;
         int j2y = j2x + 1;
         //add member weight
         Type type = bundleType[bundleIndex[t]];
         loads[j1x / 2] -= type.weight * length[t];
         loads[j2x / 2] -= type.weight * length[t];
         //set matrix
         double xx = mx * mx * type.AE / (length[t] * length[t] * length[t]);
         double xy = mx * my * type.AE / (length[t] * length[t] * length[t]);
         double yy = my * my * type.AE / (length[t] * length[t] * length[t]);
         matrix[j1x][j1x] += xx;
         matrix[j1x][j1y] += xy;
         matrix[j1x][j2x] -= xx;
         matrix[j1x][j2y] -= xy;
         matrix[j1y][j1x] += xy;
         matrix[j1y][j1y] += yy;
         matrix[j1y][j2x] -= xy;
         matrix[j1y][j2y] -= yy;
         matrix[j2x][j1x] -= xx;
         matrix[j2x][j1y] -= xy;
         matrix[j2x][j2x] += xx;
         matrix[j2x][j2y] += xy;
         matrix[j2y][j1x] -= xy;
         matrix[j2y][j1y] -= yy;
         matrix[j2y][j2x] += xy;
         matrix[j2y][j2y] += yy;
     }

     //set constraints on matrix
     for (int t1 : bridge.fixedIndex) {
         for (int t2 = 0; t2 < equationSize; t2++) {
             matrix[t1][t2] = 0;
             matrix[t2][t1] = 0;
         }
         matrix[t1][t1] = 1;
     }

     //solve
     for (int ie = 0; ie < equationSize; ie++) {
         double pivot = matrix[ie][ie];
         if (-0.99 < pivot && pivot < 0.99) {
             totalCost=1000000;
             status = 1;
             return;
         }
         double pivr = 1.0 / pivot;
         for (int k = 0; k < equationSize; k++) {
             matrix[ie][k] /= pivot;
         }
         for (int k = 0; k < equationSize; k++) {
             if (k != ie) {
                 pivot = matrix[k][ie];
                 for (int j = 0; j < equationSize; j++) {
                     matrix[k][j] -= matrix[ie][j] * pivot;
                 }
                 matrix[k][ie] = -pivot * pivr;
             }
         }
         matrix[ie][ie] = pivr;
     }

     //initialize jd (joint displacement)
     for (int t2 = 0; t2 < equationSize; t2++) {
         jdisplacement[0][t2] = 0;
     }
     //set jd[0]
     for (int t1 = 0; t1 < equationSize; t1++) {
         double temp = 0;
         for (int t2 = 0; t2 < equationSize/2; t2++) {
             temp += matrix[t1][t2 * 2 + 1] * loads[t2];
         }
         jdisplacement[0][t1] = temp;
     }
     //set jd[t]
     double backWeight=Inventory.BackLoad[bridge.truckType];
     double frontWeight=Inventory.FrontLoad[bridge.truckType];
     for (int t1 = 0; t1 < bridge.deckSize; t1++) {
         for (int t2 = 0; t2 < equationSize; t2++) {
             jdisplacement[t1 + 1][t2] = jdisplacement[0][t2]
                     - backWeight * matrix[t2][t1 * 2 + 1]
                     - frontWeight * matrix[t2][t1 * 2 + 3];
         }
     }
     //set constraints on jd
     for (int t1 : bridge.fixedIndex) {
         for (int t2 = 0; t2 <= bridge.deckSize; t2++) {
             jdisplacement[t2][t1] = 0;
         }
     }
     //set member force
     for (int t1 = 0; t1 < memberSize; t1++) {
         double aeOverLL = bundleType[bundleIndex[t1]].AE / (length[t1] * length[t1]);
         double mx = XY[j12[t1 * 2 + 1] * 2] - XY[j12[t1 * 2] * 2];
         double my = XY[j12[t1 * 2 + 1] * 2 + 1] - XY[j12[t1 * 2] * 2 + 1];
         double cosX = aeOverLL * mx;
         double cosY = aeOverLL * my;
         double _max = 0;
         double _min = 0;
         for (int t2 = 0; t2 <= bridge.deckSize; t2++) {
             double f = cosX * (jdisplacement[t2][j12[t1 * 2 + 1] * 2] - jdisplacement[t2][j12[t1 * 2] * 2])
                     + cosY * (jdisplacement[t2][j12[t1 * 2 + 1] * 2 + 1] - jdisplacement[t2][j12[t1 * 2] * 2 + 1]);
             memberForce[t2][t1] = f;
             if (f > _max) {
                 _max = f;
             } else if (f < _min) {
                 _min = f;
             }
         }
         maxForce[t1] = _max;
         minForce[t1] = _min;
     }
 }


 public boolean isLegalPosition(double x, double y) {
     if (startY < y && y < endY) {
         int i = (int) ((y - startY) / Inventory.gridStep);
         return minX[i] < x && x < maxX[i];
     }
     return false;
 }

 public void optimize(int maxBundle, int minLength) {
     //initialize
     Type[] types=Inventory.types2;
     double slenderness=bridge.slenderness;
     //typeTest[t1][t2] mean whether member t2 passes type t1
     boolean[][] typeTest = new boolean[types.length][memberSize];
     //[t] means whether types[t] worth analyzing
     boolean[] typeValid = new boolean[types.length];
     //number of validTypes
     int validTypeSize;
     //index of each validType
     //int[] validTypeIndex;
     //minCost for each member
     double[] minCost = new double[memberSize];
     //typeTest where t1 means the validType index instead
     boolean[][] reducedTypeTest;
     long[] times = new long[3];
     double totalMinCost = 0;
     times[0] = System.currentTimeMillis();

     //set typeTest, minCost and totalMinCost
     for (int t1 = 0; t1 < types.length; t1++) {
         for (int t2 = 0; t2 < memberSize; t2++) {
             boolean tempB =
                     types[t1].ifPass(minForce[t2], maxForce[t2], length[t2], slenderness);

             typeTest[t1][t2] = tempB;
             if (minCost[t2] == 0 && tempB) {
                 minCost[t2] = types[t1].cost * length[t2];
                 totalMinCost += minCost[t2];
             }
         }
     }

     //set typeValid
     validTypeSize = 1;
     for (int t1 = 0; t1 < types.length; t1++) {
         boolean valid = true;
         for (int t2 = 0; t2 < t1; t2++) {
             if (!typeValid[t2]) {
                 continue;
             }
             boolean valid2 = false;
             for (int t3 = 0; t3 < memberSize; t3++) {
                 if ((!typeTest[t2][t3]) && typeTest[t1][t3]) {
                     valid2 = true;
                     break;
                 }
             }
             if (valid2 == false) {
                 valid = false;
                 break;
             }
         }
         if (valid) {
             validTypeSize++;
         }
         typeValid[t1] = valid;
     }

     //set validTypeIndex
     validTypeIndex = new int[validTypeSize];
     reducedTypeTest = new boolean[validTypeSize][];
     double[] reducedTypeCost = new double[validTypeSize];
     int tempI1 = 1;
     int tempI2 = 0;
     while (tempI1 < validTypeSize) {
         if (typeValid[tempI2]) {
             validTypeIndex[tempI1] = tempI2;
             reducedTypeTest[tempI1] = typeTest[tempI2];
             reducedTypeCost[tempI1] = types[tempI2].cost;
             tempI1++;
         }
         tempI2++;
     }
     validTypeIndex[0] = -1;
     reducedTypeTest[0] = null;
     reducedTypeCost[0] = 0;

     //set totalLength
     double totalLength = -0.001;
     for (int t = 0; t < memberSize; t++) {
         totalLength += length[t];
     }
     //reduced index of type for each bundle
     int[] bundles = new int[maxBundle + 2];
     //bundle[i]=remaining length of members after i bundle been considered
     double[] bundleLength = new double[maxBundle + 1];
     //bundleCost[i]=total cost after i bundles
     double[] bundleCost = new double[maxBundle + 1];
     double[] bundleMinCost = new double[maxBundle + 1];
     //level of bundle the member is at
     int[] memberLevel = new int[memberSize];
     for (int t = 0; t < memberSize; t++) {
         memberLevel[t] = maxBundle + 1;
     }
     bundles[0] = 0;
     bundles[1] = 0;
     bundleLength[0] = totalLength;
     bundleCost[0] = baseCost;
     bundleMinCost[0] = totalMinCost + Inventory.BundleCost;
     totalCost = 1000000;
     int level = 0;
     int tempC = 0;
     

     times[1] = System.currentTimeMillis();
     while (level > -1) {
         tempC++;
         //if done
         if (bundleLength[level] < 0) {
             //if new record is found
             if (bundleCost[level] < totalCost) {
                 totalCost = bundleCost[level];
                 bundleType = new Type[level];
                 for (int t = 0; t < level; t++) {
                     bundleType[t] = types[validTypeIndex[bundles[t + 1]]];
                 }
                 for (int t = 0; t < memberSize; t++) {
                     bundleIndex[t] = memberLevel[t] - 1;
                 }
             }
             level -= 2;
             continue;
         }
         //reduced loop by 50%
         if (bundleCost[level] + bundleMinCost[level] > totalCost) {
             level--;
             continue;
         }
         if (level > 0 && (bundleLength[level - 1] - bundleLength[level]) < minLength) {
             level--;
             continue;
         }
         //reach maxBundle
         if (level >= maxBundle) {
             level--;
             continue;
         }
         if (bundles[level + 1] == validTypeSize - 1) {
             level--;
             continue;
         }

         //load bundles[level]
         level++;
         bundles[level]++;
         bundleLength[level] = bundleLength[level - 1];
         bundleCost[level] = bundleCost[level - 1] + Inventory.BundleCost;
         bundleMinCost[level] = bundleMinCost[level - 1];
         bundles[level + 1] = bundles[level];
         for (int t = 0; t < memberSize; t++) {
             if (memberLevel[t] >= level) {
                 if (reducedTypeTest[bundles[level]][t]) {
                     memberLevel[t] = level;
                     bundleLength[level] -= length[t];
                     bundleCost[level] += reducedTypeCost[bundles[level]] * length[t];
                     bundleMinCost[level] -= minCost[t];
                 } else {
                     memberLevel[t] = maxBundle + 1;
                 }
             }
         }
     }

     times[2] = System.currentTimeMillis();
     
     /*
     String tempS = "";
     tempS += "reducedTypeTest:" + validTypeSize + "\n";
     for (int t = 1; t < validTypeSize; t++) {
         tempS += types[validTypeIndex[t]].name + "\t";
         for (int tt = 0; tt < memberSize; tt++) {
             tempS += reducedTypeTest[t][tt] ? 1 : 0;
         }
         tempS += "\n";
     }
     System.out.println(tempS + totalCost);
     
     printTypes();
     * 
     */
     System.out.println("GO loop: " + tempC + " " + (times[1] - times[0]) + " " + (times[2] - times[1]));

 }
 public void optimize2(int maxBundle, int minLength) {
     //initialize
     Type[] types=Inventory.types2;
     double slenderness=bridge.slenderness;
     //typeTest[t1][t2] mean whether member t2 passes type t1
     long[] typeTest = new long[types.length];
     //[t] means whether types[t] worth analyzing
     boolean[] typeValid = new boolean[types.length];
     //number of validTypes
     int validTypeSize;
     //minCost for each member
     double[] minCost = new double[memberSize];
     //typeTest where t1 means the validType index instead
     long[] times = new long[3];
     double totalMinCost = 0;
     times[0] = System.currentTimeMillis();

     //set typeTest, minCost and totalMinCost
     for (int t1 = 0; t1 < types.length; t1++) {
         for (int t2 = 0; t2 < memberSize; t2++) {
             boolean tempB =
                     types[t1].ifPass(minForce[t2], maxForce[t2], length[t2], slenderness);

             typeTest[t1] |= tempB?1<<t2:0;
             if (minCost[t2] == 0 && tempB) {
                 minCost[t2] = types[t1].cost * length[t2];
                 totalMinCost += minCost[t2];
             }
         }
     }

     //set typeValid
     validTypeSize = 1;
     for (int t1 = 0; t1 < types.length; t1++) {
         boolean valid = true;
         for (int t2 = 0; t2 < t1; t2++) {
             if (!typeValid[t2]) {
                 continue;
             }
             if ((typeTest[t2]&typeTest[t1])==typeTest[t1]) {
                 valid = false;
                 break;
             }
         }
         if (valid) {
             validTypeSize++;
         }
         typeValid[t1] = valid;
     }

     //set validTypeIndex
     validTypeIndex = new int[validTypeSize];
     reducedTypeTest = new long[validTypeSize];
     double[] reducedTypeCost = new double[validTypeSize];
     int tempI1 = 1;
     int tempI2 = 0;
     while (tempI1 < validTypeSize) {
         if (typeValid[tempI2]) {
             validTypeIndex[tempI1] = tempI2;
             reducedTypeTest[tempI1] = typeTest[tempI2];
             reducedTypeCost[tempI1] = types[tempI2].cost;
             tempI1++;
         }
         tempI2++;
     }
     validTypeIndex[0] = -1;
     reducedTypeTest[0] = 0;
     reducedTypeCost[0] = 0;

     //set totalLength
     double totalLength = -0.001;
     for (int t = 0; t < memberSize; t++) {
         totalLength += length[t];
     }
     //reduced index of type for each bundle
     int[] bundles = new int[maxBundle + 2];
     //bundle[i]=remaining length of members after i bundle been considered
     double[] bundleLength = new double[maxBundle + 1];
     //bundleCost[i]=total cost after i bundles
     double[] bundleCost = new double[maxBundle + 1];
     double[] bundleMinCost = new double[maxBundle + 1];
     //level of bundle the member is at
     long[] bundleRemain = new long[maxBundle+1];
     bundles[0] = 0;
     bundles[1] = 0;
     bundleRemain[0]=(1L<<memberSize)-1;
     bundleLength[0] = totalLength;
     bundleCost[0] = baseCost+bridge.totalJointSize*Inventory.JointCost;
     bundleMinCost[0] = totalMinCost + Inventory.BundleCost;
     totalCost = 1000000;
     int level = 0;
     int tempC = 0;
     int tempC2=0;


     times[1] = System.currentTimeMillis();
     while (level > -1) {
         tempC++;
         //if done
         if (bundleRemain[level] ==0) {
             //if new record is found
             if (bundleCost[level] < totalCost) {
                 tempC2++;
                 totalCost = bundleCost[level];
                 bundleType = new Type[level];
                 for (int t = 0; t < level; t++) {
                     bundleType[t] = types[validTypeIndex[bundles[t + 1]]];
                 }
                 for (int t = 0; t < memberSize; t++) {
                     long mask=1L<<t;
                     int tmp=level-1;
                     while(true){
                      if((reducedTypeTest[bundles[tmp+1]]&bundleRemain[tmp]&mask)!=0){
                       bundleIndex[t]=tmp;
                       break;
                      }
                      --tmp;
                     }
                 }
             }
             level -= 2;
             continue;
         }
         //reduced loop by 50%
         if (bundleCost[level] + bundleMinCost[level] > totalCost) {
             level--;
             continue;
         }
         if (bundleCost[level] + reducedTypeCost[bundles[level]]*bundleLength[level] + Inventory.BundleCost>totalCost){
             level--;
             continue;
         }
         //lenght to short
         if (level > 0 && (bundleLength[level - 1] - bundleLength[level]) < minLength) {
             level--;
             continue;
         }
         //reach maxBundle
         if (level >= maxBundle) {
             level--;
             continue;
         }
         //finish a loop
         if (bundles[level + 1] == validTypeSize - 1) {
             level--;
             continue;
         }

         //load bundles[level]
         level++;
         bundles[level]++;
         bundleLength[level] = bundleLength[level - 1];
         bundleCost[level] = bundleCost[level - 1] + Inventory.BundleCost;
         bundleMinCost[level] = bundleMinCost[level - 1];
         long valid=reducedTypeTest[bundles[level]]&bundleRemain[level-1];
         if(valid==0){
          level--;
          continue;
         }
         bundleRemain[level]=bundleRemain[level-1]^valid;
         bundles[level + 1] = bundles[level];
         while(valid!=0){
          int t=Long.numberOfTrailingZeros(valid);
          valid^=1<<t;
          bundleLength[level] -= length[t];
          bundleCost[level] += reducedTypeCost[bundles[level]] * length[t];
          bundleMinCost[level] -= minCost[t];
         }
     }

     times[2] = System.currentTimeMillis();
     System.out.println("GO loop: " + tempC + " "+tempC2+ " " + (times[1] - times[0]) + " " + (times[2] - times[1]));

 }
}
