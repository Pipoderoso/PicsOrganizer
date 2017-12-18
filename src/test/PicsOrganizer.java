/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author pipe
 */


import com.drew.imaging.*;
import com.drew.metadata.*;
import com.drew.tools.*;
import com.drew.metadata.exif.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
        
        
public class PicsOrganizer {
    
    private File[] filePaths;
    private picInfo[] pics;
    
    public PicsOrganizer(String path) {  //Method that produces an File array with each element on a directory
        File picsDir = new File(path);
        filePaths = picsDir.listFiles();   
        pics = new picInfo[filePaths.length];
    }
    
    class picInfo { //Class for saving each file information in one object
        String picName; 
        Date picDate;  
        public picInfo(String picName, Date picDate) {
            this.picName = picName;
            this.picDate = picDate; 
        }        
    }
      
    void getPicsLength() {  // for debugging
        for (int i = 0; i < pics.length; i++) {
            System.out.println(pics[i].picDate);
        }
    }
    
    void getPicInfo(){
        for (int i = 0; i < filePaths.length; i++) {
            try {
                File file = new File(filePaths[i].getPath());
                
                Metadata metadata = ImageMetadataReader.readMetadata(file);

                Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                String name = filePaths[i].getName();
                
                picInfo obj = new picInfo(name, date);
                pics[i] = obj; 
               
            
            } catch (ImageProcessingException e) {
                System.err.println("EXCEPTION: " + e);
            } catch (IOException e) {
                System.err.println("EXCEPTION: " + e);
            }
        }
    }
    
    public static void main(String[] args) {
        
        String path = "/home/pipe/Documents/FH/git-repos/PicsOrganizer/testPics/";
        PicsOrganizer foo = new PicsOrganizer(path); 
      
        foo.getPicInfo();
        foo.getPicsLength();
        
    }
      
}
