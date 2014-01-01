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
public interface Managable {
    public String getName();
    public String getTypeName();
    public String read(Scanner in);
    public void write(PrintStream out);
    public String read(InputStream in);
    public void write(OutputStream out);
    public Object newSample();
}
