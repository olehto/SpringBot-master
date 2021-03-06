package me.aboullaite;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.send.MessengerSendClient;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringDocBotApplication {

	private static final Logger logger = LoggerFactory.getLogger(SpringDocBotApplication.class);

	/**
	 * Initializes the {@code MessengerSendClient}.
	 *
	 * @param pageAccessToken the generated {@code Page Access Token}
	 */
	@Bean
	public MessengerSendClient messengerSendClient(@Value("${messenger4j.pageAccessToken}") String pageAccessToken) {
		logger.debug("Initializing MessengerSendClient - pageAccessToken: {}", pageAccessToken);
		return MessengerPlatform.newSendClientBuilder(pageAccessToken).build();
	}

	public static void main(String[] args) {
		
//		MyTimerTask timerTask = new MyTimerTask(1);
//        //running timer task as daemon thread
//        Timer timer = new Timer(true);
//        timer.scheduleAtFixedRate(timerTask, 60*1000, 24*3600*1000);
//        timerTask = new MyTimerTask(2);
//        timer.scheduleAtFixedRate(timerTask, 3*360*1000, 24*3600*1000);//8*3600*1000, 24*3600*1000
//        timerTask = new MyTimerTask(3);
//        timer.scheduleAtFixedRate(timerTask, 10*360*1000, 24*3600*1000);
//        System.out.println("TimerTask started");
		
        
        
		SpringApplication.run(SpringDocBotApplication.class, args);
	}
}
