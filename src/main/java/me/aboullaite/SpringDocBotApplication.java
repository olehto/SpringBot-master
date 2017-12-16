package me.aboullaite;

import com.github.messenger4j.Messenger;
//import com.github.messenger4j.MessengerPlatform;
//import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.spi.MessengerHttpClient;

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
	public Messenger messengerSendClient(@Value("${messenger4j.pageAccessToken}") String pageAccessToken) {
		logger.debug("Initializing MessengerSendClient - pageAccessToken: {}", pageAccessToken);
		return Messenger.create(pageAccessToken, "2749048215", "28115df6638c239afacd1a3d1c2981f6");
	}

	public static void main(String[] args) {
        System.out.println("TimerTask started");
		
        
        
		SpringApplication.run(SpringDocBotApplication.class, args);
	}
}
