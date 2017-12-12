package me.aboullaite;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.Recipient;

import domain.User;

public class MyTimerTask extends TimerTask {
	
	public int remNum;
	
	public MyTimerTask(int remNum){
		this.remNum = remNum;
	}
	
    @Override
    public void run() {
        System.out.println("Timer task started at:"+new Date());
        completeTask();
        System.out.println("Timer task finished at:"+new Date());
    }

    private void completeTask() {
    	List<User> users = get_users();
    	for (User u : users) {
    		System.out.println("DoneeeeeeeeeeeUser");
    	    if(null!=u.getUser_reminders_cout()){
    	    	System.out.println("Doneeeeeeeeeee");
    	    	if(Integer.parseInt(u.getUser_reminders_cout())<=remNum){
    	    		System.out.println("Ooneeeeeeeeeee :" + u.getUser_id());
    	    		sendMessage(u.getUser_id(),"Hi!");
    	    	}
    	    }
    	}
    }
    
    private void sendMessage(String mess, String userID){
    	MessengerSendClient msc = MessengerPlatform.newSendClientBuilder("EAAEb4X44nC4BAL5g7FehO4RO5dCjtCryA8C01813xWPnNBtK4yJbsCVNFtN5qjBPZCaGiYb8JYDo4Th99MOmMEgm11fCg6H0ycS54cJ74XjA2VZBw3AHOP3cDCIYMIUT9oN8nzTgMZBSIrOyOtBWJbxi7GgIZCPvY1oFXkgSz59elSmmnHAe").build();
    	
    	try {//"1218787008222363"
            final Recipient recipient = Recipient.newBuilder().recipientId(userID).build();
            final NotificationType notificationType = NotificationType.REGULAR;
            final String metadata = "DEVELOPER_DEFINED_METADATA";

            msc.sendTextMessage(recipient, notificationType, mess, metadata);
        } catch (MessengerApiException | MessengerIOException e) {
            //handleSendException(e);
        }
    }
    
    public List<User> get_users() {
    	 List<User> users = new LinkedList<User>();
    	try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "SELECT user_id, user_city, user_name, user_hobby, user_reminders_cout FROM user_info";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String id = rs.getString("user_id");
                String city = rs.getString("user_city");
                String name = rs.getString("user_name");
                String hb = rs.getString("user_hobby");
                String rmc = rs.getString("user_reminders_cout");
                users.add(new User(id,name, city, hb, rmc));
            }
        } catch (Exception e) {
        	System.out.println(e);
    }
        return users;
    }
    
    private static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = null;
        if(System.getenv("DATABASE_URL") != null) {
            dbUri = new URI(System.getenv("DATABASE_URL"));
        }else {
            String DATABASE_URL = "postgres://mtulpfvjumewip:cd9faec32bdc124df7078f8a91ba8025535d62a63a1bf3e2f728b0657b298f59@ec2-54-247-187-134.eu-west-1.compute.amazonaws.com:5432/d7ickck3i1lifd";
            dbUri = new URI(DATABASE_URL);
        }

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
                + dbUri.getPort() + dbUri.getPath()
                + "?sslmode=require";
        /*Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/userdb?sslmode=require",
                "ubuntu",
                "ubuntu");*/
		return DriverManager.getConnection(dbUrl, username, password);
	}
}
