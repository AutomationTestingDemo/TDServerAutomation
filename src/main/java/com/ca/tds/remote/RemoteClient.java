package com.ca.tds.remote;

import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
 
public class RemoteClient{
  public static void main(String[] arg){
	  	String host = "rasra04-I11626";
		String user = "root";
		String password = "interOP@11626";
		String command = "cd /opt/RS;source env-remote-service.sh;export LD_LIBRARY_PATH=/opt/arcot/lib;./java-app-run.sh start";
		
    try{
    	Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications","publickey,keyboard-interactive,password");
      JSch jsch=new JSch();  
      Session session=jsch.getSession(user, host, 22);
     
      // username and password will be given via UserInfo interface.
      session.setPassword(password);
	  session.setConfig(config);
      session.connect();
      
      System.out.println("Connected...");
      
      Channel channel=session.openChannel("exec");
      ((ChannelExec)channel).setCommand(command);
 
      
      //channel.setInputStream(null);
 
      ((ChannelExec)channel).setErrStream(System.err);
 
      InputStream in=channel.getInputStream();
 
      channel.connect();
 
      byte[] tmp=new byte[1024];
      while(true){
        while(in.available()>0){
          int i=in.read(tmp, 0, 1024);
          if(i<0)break;
          System.out.print(new String(tmp, 0, i));
        }
        if(channel.isClosed()){
          System.out.println("exit-status: "+channel.getExitStatus());
          break;
        }
        try{Thread.sleep(1000);}catch(Exception ee){}
      }
      channel.disconnect();
      session.disconnect();
    }
    catch(Exception e){
      System.out.println(e);
    }
  }
 
}
