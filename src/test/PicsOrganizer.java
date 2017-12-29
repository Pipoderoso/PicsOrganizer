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
import com.drew.metadata.icc.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

        
        
public class PicsOrganizer {
    
    private File[] filePaths; 
    private PicInfo[] pics;
    private GroupPics[] groupPics;
    
    
    public PicsOrganizer(String path) {  //Method that produces an File array with each element on a directory
        File picsDir = new File(path);
        filePaths = picsDir.listFiles();   
        pics = new PicInfo[filePaths.length];
    }
    
    class PicInfo { //Class for saving each file information in one object
        private String picName; 
        private Date picDate;  
        private Calendar cal = Calendar.getInstance();
        
        
        public PicInfo(String picName, Date picDate) {
            this.picName = picName;
            this.picDate = picDate; 
        }

        String getPicName(){
            return picName; 
        }
        
        Date getPicDate(){
            return picDate; 
        }
        
        int getPicDateYear(){
            cal.setTime(picDate);  // MEJORA - Poner al principio de la clase
            return cal.get(Calendar.YEAR);
        }
        
        void setPicName(String picName){
            this.picName = picName; 
        }
        
        void setPicDate(Date picDate){
            this.picDate = picDate;
        }
    }  
    
    class GroupPics {  // Class for saving arrays of PicInfo for different years
        int year;
        PicInfo[] content; 
        public GroupPics(int year, PicInfo[] content){
            this.year = year;
            this.content = content; 
        }
    } 
    
      
    void debPics() {  // for debugging
        for (int i = 0; i < groupPics.length; i++) {
            System.out.println(groupPics[i].year);
            for (int j = 0; j < groupPics[i].content.length; j++) {
                System.out.println(groupPics[i].content[j].getPicDate());
            }
        }

    }
    
    void getPicsInfo(){   //Method for getting and saving Date and Picture name info. 
        for (int i = 0; i < filePaths.length; i++) {
            try {
                File file = new File(filePaths[i].getPath());
                
                Metadata metadata = ImageMetadataReader.readMetadata(file);
               
                
                Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                
                if(directory == null){ // QUICK FIX for metadata not being of type exif but of type icc (Think of a better solution for more types compatibility)
                    directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
                    Date date = directory.getDate(IccDirectory.TAG_PROFILE_DATETIME);              
                    String name = filePaths[i].getName();           
                    PicInfo obj = new PicInfo(name, date);
                    pics[i] = obj;
                    continue;
                }
   
                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
               
                String name = filePaths[i].getName();
                
                PicInfo obj = new PicInfo(name, date);
                pics[i] = obj;
         
            
            } catch (ImageProcessingException e) {
                System.err.println("EXCEPTION: " + e);
                System.err.println("Not Compatible Metadata || There is no Orignal-Date on metadata");
            } catch (IOException e) {
                System.err.println("EXCEPTION: " + e);
            }
        }
    }
    
        
    void organizePics(){  //This method orgnizes the PicInfo array and divide them based on the year
        
        //Loop for ordering from recent dates to old dates
        for (int i = 0; i < pics.length - 1 ; i++){
				
            for (int j = i + 1; j < pics.length; j++){
 
		if (pics[i].getPicDate().before(pics[j].getPicDate())){
				
                    PicInfo tempPic = pics[i];
                    pics[i] = pics[j];
                    pics[j] = tempPic;
					
		}
            }		
	}
        // ------------------------------
        
        // Iteration to get number of years for separating pics 
        Set<Integer> sYears = new LinkedHashSet<Integer>();
        for (int i = 0; i < pics.length; i++) {
            sYears.add(pics[i].getPicDateYear());
        }
        int[] years = new int[sYears.size()];
        int index = 0;
        for(Integer i : sYears){
            years[index++] = i;
        }
        // -----------------
        
        groupPics = new GroupPics[years.length];
        
        for (int i = 0; i < years.length; i++) { //A better option would be to loop pics[] and look for a change of year in date. This way the program will run through every pic just once
            
            List<PicInfo> tempPics = new ArrayList<PicInfo>();
            
            for (int j = 0; j < pics.length; j++) {
                if(pics[j].getPicDateYear() == years[i]){
                    tempPics.add(pics[j]);
                }
            }

            GroupPics obj = new GroupPics(years[i],tempPics.toArray(new PicInfo[tempPics.size()]));
            groupPics[i] = obj; 
        }
        
        
        
    }
    
    public static void main(String[] args) {
        
        String path = "/home/pipe/Documents/FH/git-repos/PicsOrganizer/testPics/";
        PicsOrganizer foo = new PicsOrganizer(path); 
      
        foo.getPicsInfo();
        foo.organizePics();
        foo.debPics();
        
        
        
    }
      
}
