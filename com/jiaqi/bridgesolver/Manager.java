/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiaqi.bridgesolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author jiaqi
 */
public class Manager<T extends Managable> {
    private TreeMap<String,T> map;
    T sample;
    public Manager(T _sample){
        sample=_sample;
        map=new TreeMap<String,T>();
        
    }
    public String load(Scanner scanner){
        String temp;
        T tt=(T) sample.newSample();
        if(!(temp=tt.read(scanner)).startsWith(Inventory.MESSAGE_SUCCESS)){
            return temp;
        }
        return load(tt);
    }
    private String load(T t){
        if(map.put(t.getName(), t)==null){
            return Inventory.MESSAGE_SUCCESS+t.getName()+" has been added.\n";
        }else{
            return Inventory.MESSAGE_SUCCESS+t.getName()+" has been replaced.\n";
        }
    }
    public String remove(String name){
        if(map.remove(name)==null){
            return name+" does not exist, so nothing is done.\n";
        }else{
            return name+" has been removed.\n";
        }
    }
    public String listAll(){
        String f="listing "+sample.getTypeName()+"s:\n";
        Set<String> ss=map.keySet();
        for(String s:ss){
            f+=s+"\n";
        }
        return f+ss.size()+" listed in total.\n";
    }
    public T get(String name){
        return map.get(name);
    }
    public Collection<T> getAll(){
        return map.values();
    }
}
