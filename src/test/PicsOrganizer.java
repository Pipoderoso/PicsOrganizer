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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
       

        
        
public class PicsOrganizer {
    
    private List<File> filePathsList = new ArrayList<File>(); 
    private PicInfo[] pics;
    private GroupPics[] groupPics;
    private String mainPath;
    private boolean validPath;
    
    public PicsOrganizer(String path) {  //Method that produces an File array with each element on a directory
        mainPath = (path.charAt(path.length() - 1)=='/')?path:(path+"/");
        filesReader(mainPath);
        pics = new PicInfo[filePathsList.size()];
    }
    
    class PicInfo { //Class for saving each file information in one object
        private String picPath;
        private String picName; 
        private Date picDate;  
        private Calendar cal = Calendar.getInstance();
        
        
        public PicInfo(String picPath, Date picDate) {
            this.picPath = picPath;
            this.picDate = picDate; 
            picName = picPath.substring(picPath.lastIndexOf("/")+1);
            cal.setTime(picDate);
        }
        
        Date getPicDate(){
            return picDate; 
        }
        
        int getPicDateYear(){
            return cal.get(Calendar.YEAR);
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
    
    public boolean isValidPath(){
        return validPath;
    }
            
    void debPics() {  // for debugging
        for (int i = 0; i < groupPics.length; i++) {
            System.out.println(groupPics[i].year);
            for (int j = 0; j < groupPics[i].content.length; j++) {
                System.out.println(groupPics[i].content[j].getPicDate());
            }
        }

    }
    
    private void filesReader(String pathName) { //Read Files Subdirectories and Files inside Subdirectories
        File directory = new File(pathName);
        if (directory.exists()){
            validPath = true;
            File[] fList = directory.listFiles();

            for (File file : fList) {
                if (file.isFile()) {
                    filePathsList.add(file);
                } else if (file.isDirectory()) {
                    filesReader(file.getAbsolutePath());
                }
            }
        } else {
            validPath = false;
        }
    } 
    
    void getPicsInfo(){   //Method for getting and saving Date and Picture names. ADD NEW ELSEIF CASE FOR NEW METADATA TYPES
        
        for (int i = 0; i < filePathsList.size(); i++) {          
            try {             
                File file = new File(filePathsList.get(i).getPath());  
                
                InputStream is = new FileInputStream(file.getPath());
                BufferedInputStream bis = new BufferedInputStream(is);
                FileType fileType = FileTypeDetector.detectFileType(bis); //for detecting Filetype
                         
                if (fileType != FileType.Unknown){
                    
                    Metadata metadata = ImageMetadataReader.readMetadata(file);
                    Directory directory;
                    
                    if (metadata.containsDirectoryOfType(ExifSubIFDDirectory.class)) {
                        directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                        Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                        String path = file.getPath();         // !! use fiel instead of
                        PicInfo obj = new PicInfo(path, date);
                        pics[i] = obj;
                    /*    ICC Metadata does not correspond to the date the pic was taken.
                    } else if (metadata.containsDirectoryOfType(IccDirectory.class)) {
                        directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
                        Date date = directory.getDate(IccDirectory.TAG_PROFILE_DATETIME);              
                        String path = file.getPath();           
                        PicInfo obj = new PicInfo(path, date);
                        pics[i] = obj;
                    */     
                    } else {
                        Date date = new GregorianCalendar(0,0,1).getTime(); 
                        String path = file.getPath();
                        PicInfo obj = new PicInfo(path,date);
                        pics[i] = obj; 
                    }
                } else {
                    Date date = new GregorianCalendar(0,0,1).getTime(); 
                    String path = file.getPath();
                    PicInfo obj = new PicInfo(path,date);
                    pics[i] = obj; 
                }             
            } catch (ImageProcessingException e) {
                System.err.println("EXCEPTION: " + e);
                System.err.println("Not Compatible Metadata || There is no Orignal-Date on metadata");
            } catch (IOException e) {
                System.err.println("EXCEPTION: " + e);
                validPath = false; 
            }
        }
        for (int i = 0; i < pics.length; i++) { //check if there is at least 1 picture
            Date tempDate = new GregorianCalendar(0,0,1).getTime();
            if (pics[i].picDate != tempDate){  //only requieres 1 picture for the path to be valid
                validPath=true;
                break;
            }
            validPath=false;
        }
    }
         
    void organizePics(){  //This method orgnizes the PicInfo array and arrange them based on the year       
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
        
        // Iteration to get number of different years for separating pics 
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
    
    void dirCreator(){ //creates directories and move pics
        boolean dirCreated;
        
        for (int i = 0; i < groupPics.length; i++) {
            String tempPath = (groupPics[i].year!=1)?mainPath+groupPics[i].year:mainPath+"unknown"; //not crossplataform friendly FIX ! usar Paths
            dirCreated = new File(tempPath).mkdirs();
            if (dirCreated == true || new File(tempPath).exists()) {
                for (int j = 0; j < groupPics[i].content.length; j++) {
                    try{
                        Files.move(Paths.get(groupPics[i].content[j].picPath), Paths.get(tempPath + "/" + groupPics[i].content[j].picName));
                    }
                    catch(IOException ex){
                        System.out.println(ex.toString());
                    }
                }
            }          
        }
        validPath = false;
    }
            
    /*
    public static void main(String[] args) {
        
        String path = "/home/pipe/Documents/FH/git-repos/PicsOrganizer/testPics/";
        PicsOrganizer foo = new PicsOrganizer(path);   
        foo.getPicsInfo();  //class methods have to be called in this order!
        foo.organizePics();
        foo.debPics();
        foo.dirCreator();    
        
    }    
    */
}
