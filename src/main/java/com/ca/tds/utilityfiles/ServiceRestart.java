package com.ca.tds.utilityfiles;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@SuppressWarnings("unused")
public class ServiceRestart {

	public static void server3DSrestart() {
		String host = "ibnbld002532.bpc.broadcom.net";
		String user = "root";
		String password = "interOP@002532";
		String command = "cd /root/scripts;./startService.sh";
		
		int counter = 0;
		try {

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected");

			
			  Channel channel=session.openChannel("exec");
			  ((ChannelExec)channel).setCommand(command); channel.setInputStream(null);
			  ((ChannelExec)channel).setErrStream(System.err);
			  
			  InputStream in=channel.getInputStream();
			  
			  channel.connect();

			  byte[] tmp = new byte[1024];
				   
				  while(true){
				  
				  while(in.available()>0){
				  
				  int i = in.read(tmp, 0, 1024);
				  
				  if(i<0) {
				  
				  break;
				  
				  }
			
				  System.out.print(new String(tmp, 0, i));
				  
				  counter++;
				  
				  System.out.println("CounterValue "+counter);
				  
				  }
				  
				  
				  if(counter!=0) {
					  
					  channel.disconnect();
					  
				  }
				  	 
				  if(channel.isClosed()){
				  
				  System.out.println("exit-status: "+channel.getExitStatus());
				  
				  break;
				  
				  }    
				  
				  }
			  
			  int exitStatus = channel.getExitStatus();
			  
			  channel.disconnect();
			  
			  session.disconnect();
			  
			  if(exitStatus < 0){
			  
			  System.out.println("Done, but exit status not set!");
			  
			  } else if(exitStatus > 0){
			  
			  System.out.println("Done, but with error!");
			  
			  } else{
			  
			  System.out.println("Done!");
			  
			  }

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * public static void main(String args[]) { server3DSrestart(); }
	 */
	
 

}