import java.util.Iterator;
import java.io.BufferedReader;

import java.io.*;
import java.util.*;

public class FileLineIterator implements Iterator<String> {

  
    private String begin = "";
    private BufferedReader reader;
    
    public FileLineIterator(BufferedReader reader) {
        try {
            this.reader = reader;
            begin = reader.readLine();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }

    }

     
    public FileLineIterator(String filePath) {
        this(fileToReader(filePath));
    }

   
    public static BufferedReader fileToReader(String filePath) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }
        return reader;

    }

    
    @Override
    public boolean hasNext() {
        try {
            if (begin == null) {
                reader.close();
                return false;
            }
            return true;
        } catch (IOException e) {
            System.out.println("No next line");
            throw new IllegalArgumentException();
        }
    }

    
    @Override
    public String next() {
        try {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            String temp = begin;
            begin = reader.readLine();
            if (begin == null) {
                reader.close();
            }
            return temp;
        } catch (IOException e) {
            begin = null;
            throw new NoSuchElementException();
        }

    }
}
