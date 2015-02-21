package com.mengcraft.protect.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils
{
    
    private IOUtils() {
        throw new AssertionError("This is a util class");
    }
    
    public static void copyFileFromStream(InputStream in, File dest)
            throws IOException {
        
        try {
            if (!dest.exists()) {
                dest.getParentFile().mkdirs();
                dest.createNewFile();
            }
        } catch (IOException e) {
            throw e;
        }
        
        OutputStream out = null;
        BufferedInputStream bin = new BufferedInputStream(in);
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        try {
            out = new FileOutputStream(dest);
        } catch (FileNotFoundException e1) {
            throw new RuntimeException("Impossible! File not found?!", e1);
        }
        
        try {
            while ((bytesRead = bin.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            
            try {
                bin.close();
            } catch (IOException e) {
                throw new RuntimeException("Cannot close input stream!", e);
            }
            
            try {
                out.close();
            } catch (IOException e) {
                throw new RuntimeException("Cannot close output stream!", e);
            }
            
        }
        
    }
    
}
