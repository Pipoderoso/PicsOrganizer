# PicsOrganizer
Java Program for organizing pictures depending on the date the picture was taken. The library [metadata-extractor](https://github.com/drewnoakes/metadata-extractor) is used for reading exif data from each image. The program move the images into a new directory corresponding to the year/month it was taken. 

![UI Idea](https://user-images.githubusercontent.com/30522200/34437791-9593622a-eca1-11e7-80cb-d8c2522357f9.png)

## Info

- Images that don't have any date information on it's exif-data will be moved into a directory named 'unknown'
- Program will read and organize pictures inside directories.
- PS: back up images before using program

## Progress

- [x] Method for reading exif-data from images 
- [x] Method for organizing images 
- [x] Method for creating directories and relocating images 
- [x] Polish code
- [x] GUI for the program

##### (Optional) 

- [ ] Arrange pictures by month too
- [x] Rename pictures and name them by its relative order inside each directory
- [ ] GUI option for choosing subDirLevel

##### Issues
