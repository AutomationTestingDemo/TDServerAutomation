package com.ca.tds.utilityfiles;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportsBackup {
	
	static String source = System.getProperty("user.dir");
	
	static String dest = System.getProperty("user.dir")+"\\"+"Reportsbackup"+"\\";
	public static void zippingReports() throws IOException {
		
		
		File directoryToZip = new File(source+"\\"+"TestResultReport");

		List<File> fileList = new ArrayList<File>();
		System.out.println("---Getting references to all files in: " + directoryToZip.getCanonicalPath());
		getAllFiles(directoryToZip, fileList);
		System.out.println("---Creating zip file");
		String finalFile = writeZipFile(directoryToZip, fileList);
		System.out.println("---Done");
		moveFile(finalFile);
		
	}

	public static void getAllFiles(File dir, List<File> fileList) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					System.out.println("directory: " + file.getCanonicalPath());
					getAllFiles(file, fileList);
				} else {
					//System.out.println("file: " + file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String writeZipFile(File directoryToZip, List<File> fileList) {
		
		Date d = new Date();
		Timestamp t = new Timestamp(d.getTime());
		String timeStamp = t.toString();
		timeStamp = timeStamp.replace(' ', '_');
		timeStamp = timeStamp.replace(':', '_');

		String finalFile =directoryToZip.getName()+timeStamp+".zip";
		try {
			FileOutputStream fos = new FileOutputStream(finalFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return finalFile;
	}

	public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
			IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		//System.out.println("Writing '" + zipFilePath + "to zip file");
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
	
	public static void moveFile(String filename){
		
		try{
    		
	    	   File afile =new File(source+"\\"+filename);
	    		
	    	   if(afile.renameTo(new File(dest+afile.getName()))){
	    		   
	    		System.out.println("TestResult ZipFile Moved Succesfully To Reportsbackup Folder");
	    	   }
	    	   else{
	    		  
	    		   System.out.println("File failed to move successful!");
	    		   
	    	   }
	    	    
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
		
	}

}
