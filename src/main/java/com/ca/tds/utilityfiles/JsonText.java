package com.ca.tds.utilityfiles;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class JsonText {
	
	static String filename="JsonTxtFile.txt";
	
    public static void jsonResponseWrite(String response) throws IOException {
    	
    	 File file = new File(filename);
    	 
    	if(file.exists()) {
    		
    		file.delete();
    	}
    	
        if(file.createNewFile()){
        	
            System.out.println(filename+": File Created in Project root directory");
            
        } else {
        	
        	System.out.println(filename+"File already exists in the project root directory"); }
        
        Files.write(Paths.get(filename), response.getBytes());

    }
    
    public static String jsonResponseRead() throws IOException {
    	
   	 File file = new File(filename);
   	 
   	 String response=null;
   	 
   	if(file.exists()&&file.canRead()) {
   		
   /*		FileReader fr = new FileReader(file);
   		BufferedReader br = new BufferedReader(fr);
   		while((response = br.readLine()) != null){
   		    //process the line
   		    System.out.println(response);
   		}*/
   		
   	 response = new String(Files.readAllBytes(Paths.get(filename)));

  	}
   	
	return response;

   }
    
/*    public static void main(String [] args) throws IOException {
    	
    	String response=null;
    	
    	response = jsonResponseRead();
    	
    	 System.out.println(response);
    	
    }*/
}
