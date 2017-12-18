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

public class MetadataReader {
    /*
    public static void main(String[] args) {
        
        File file = new File("/home/pipe/Documents/FH/git-repos/PicsOrganizer/testPics/firstImage.jpg"); 
        
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);

            Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            
            System.out.println(date);
            
        } catch (ImageProcessingException e) {
            System.err.println("EXCEPTION: " + e);
        } catch (IOException e) {
            System.err.println("EXCEPTION: " + e);
        }
    } 
*/
}
