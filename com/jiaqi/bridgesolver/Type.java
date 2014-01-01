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
public class Type implements Managable {
    String name;
    /**
     * Modulus of elasticity
     * Unit: kN/m^2
     */
    double E;
    /**
     * Yield Stress
     * Unit: kN/m^2
     */
    double Fy;
    /**
     * density of material
     * Unit: kg/m^3
     */
    double density;
    /**
     * cross section area
     * Unit: m^2
     */
    double area;
    /**
     * Unit: m^4
     */
    double moment;
    /**
     * Unit: dollar/m^3
     */
    double cost_vol;
    /**
     * Unit: kN
     */
    double FyArea;
    /**
     * Unit: 1/m^2
     */
    double FyArea_d_CEMoment;
    /**
     * Unit: kN
     */
    double tensionStrength;
    /**
     * Unit: m^6
     */
    double AE;
    /**
     * Unit: 1/m
     */
    double inverseRadiusOfGyration;
    /**
     * counting TWICE
     * Unit: dollar/m
     */
    double cost;
    /**
     * weight per joint
     * Unit: kN/m
     */
    double weight;
    
    public double getTensionStrength(){
        return tensionStrength;
    }
    
    public double getCompressionStrength(double length){
        double lambda=length*length*FyArea_d_CEMoment;
        return (lambda <= 2.25) ? 
                Inventory.CompressionResistanceFactor * Math.pow(0.66, lambda) * FyArea : 
                Inventory.CompressionResistanceFactor * 0.88 * FyArea / lambda;
    }
    public boolean ifPass(double compression,double tension,double length,double slenderness){
        return tension<tensionStrength&&length*inverseRadiusOfGyration<slenderness&&(-compression<getCompressionStrength(length));
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTypeName() {
        return "Type";
    }
    
    @Override
    public String read(Scanner in) {
        String[] ss=in.nextLine().split(" ");
        if(ss.length!=7){
            return "input size isn't 7\n";
        }
        name=ss[0];
        try{
            E=Double.parseDouble(ss[1]);
            Fy=Double.parseDouble(ss[2]);
            density=Double.parseDouble(ss[3]);
            area=Double.parseDouble(ss[4]);
            moment=Double.parseDouble(ss[5]);
            cost_vol=Double.parseDouble(ss[6]);
        }catch(Exception e){
            return "can't parse into double\n";
        }
        FyArea=Fy*area;
        FyArea_d_CEMoment=FyArea/(Inventory.C*E*moment);
        tensionStrength=Inventory.TensionResistanceFactor*FyArea;
        AE=area*E;
        inverseRadiusOfGyration=Math.sqrt(area/moment);
        cost=cost_vol*density*area*2;
        weight=Inventory.DeadLoadFactor*density*area*Inventory.Gravity / 2.0 / 1000.0;
        return Inventory.MESSAGE_SUCCESS;
    }

    @Override
    public void write(PrintStream out) {
        out.printf("%s %f %f %f %f %f %f\n", name,E,Fy,density,area,moment,cost_vol);
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
        return new Type();
    }
    
}
