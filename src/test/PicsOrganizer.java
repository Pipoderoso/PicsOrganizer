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
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

        
        
public class PicsOrganizer {
    
    private File[] filePaths; //IMPORTRANTE se puede meter dentro constructor
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
            cal.setTime(picDate);
            return cal.get(Calendar.YEAR);
        }
        
        void setPicName(String picName){
            this.picName = picName; 
        }
        
        void setPicDate(Date picDate){
            this.picDate = picDate;
        }
    }  // Class for saving various information from pics into one place
    
    class GroupPics {
        int year;
        PicInfo[] content; 
        public GroupPics(int year, PicInfo[] content){
            this.year = year;
            this.content = content; 
        }
    }  // Class for saving arrays of PicInfo for different years/months
    
      
    void debPics() {  // for debugging
        for (int i = 0; i < groupPics.length; i++) {
            System.out.println(groupPics[i].year);
            System.out.println("-----------------------------");
            for (int j = 0; j < groupPics[i].content.length; j++) {
                System.out.println(groupPics[i].content[j].picDate);
            }
        }
    }
    
    void getPicsInfo(){   //Method for getting and saving Date and Picture name info. 
        for (int i = 0; i < filePaths.length; i++) {
            try {
                File file = new File(filePaths[i].getPath());
                
                Metadata metadata = ImageMetadataReader.readMetadata(file);

                Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                String name = filePaths[i].getName();
                
                PicInfo obj = new PicInfo(name, date);
                pics[i] = obj; 
               
            
            } catch (ImageProcessingException e) {
                System.err.println("EXCEPTION: " + e);
            } catch (IOException e) {
                System.err.println("EXCEPTION: " + e);
            }
        }
    }
    
        
    void organizePics(){  //This methor orgnizes the PicInfo array and divide themn depending of the year
        
        //Loop for ordering from recent dates to old dates
        for (int i = 0; i < pics.length - 1 ; i++){
				
            for (int j = i + 1; j < pics.length; j++){
 
		if (pics[i].getPicDate().before(pics[j].getPicDate())){
				
                    Date tempDate = pics[i].getPicDate();
                    pics[i].setPicDate(pics[j].getPicDate());
                    pics[j].setPicDate(tempDate);
					
		}
            }		
	}
        
        for (int j = 0; j < pics.length; j++) {
            System.out.println(pics[j].picDate);
        }
        
        List<Integer> years = new ArrayList<Integer>(); // Comment
        years.add(pics[0].getPicDateYear()); //starting year
        
        for (int i = 0; i < pics.length - 1; i++) { 
            
            if (pics[i].getPicDateYear() != pics[i+1].getPicDateYear()) { 
                years.add(pics[i+1].getPicDateYear());
            }
        }
        
        groupPics = new GroupPics[years.size()];
        
        int limit = 1;
        int lastLimit = 0;
        
        for (int i = 0; i < years.size(); i++) {   
            for (int j = limit; j < pics.length ; j++) {
                if (pics[j].getPicDateYear() != pics[j-1].getPicDateYear() || j == pics.length -1){
                    lastLimit = (j == 1)?0:limit;
                    limit = j;  
                    System.out.println("hi");  
                    break;
                }        
            }    
            if (lastLimit == limit) {
                limit +=1;
            }
            GroupPics obj = new GroupPics(years.get(i), Arrays.copyOfRange(pics, lastLimit  , limit)); //.copyOfRange method that works like thi [startIndex, endIndex) 
            groupPics[i] = obj;                                                                                                       // if works only when there is 1 picture
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
