package site.felixkimmerle.test;


import site.felixkimmerle.lox.Lox;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class jLoxTest {
    static boolean failed = false;
    static String stripExtension(String str) {
        if (str == null) {
            return null;
        }
        int pos = str.lastIndexOf(".");
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static void main(String[] args) {
        File dir = new File(".");
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isDirectory()) {
                TestDir(file.getName());
            }
        }

        if(failed){
            System.out.println("Result: FAILED");
        } else {
            System.out.println("Result: SUCCESS");
        }
    }

    public static void TestDir(String name) {
        File dir = new File(name);
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile() && file.getName().endsWith(".lox")) {
                Test(file.getAbsolutePath(),name);
            }
        }
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void Test(String name,String dir) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        Lox lox = new Lox();
        try {
            lox.runFile(name);
        } catch (IOException e) {
            System.out.println("File: " + name + " does not exist.");
        }

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        try {
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String output = baos.toString();
        String expected = "";

        try {
            expected = readFile(stripExtension(name)+".expected",Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println("File: " + stripExtension(name)+".expected" + " does not exist.");
        }

        if(output.equals(expected)){
            System.out.println("Test: " + stripExtension(name) + " PASSED");
        }
        else {
            System.out.println("Test: " + stripExtension(name) + " FAILED");
            System.out.print("Expected: " + expected);
            System.out.print("But got: "+ output);
            failed = true;
        }

    }
}
