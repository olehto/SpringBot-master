package me.aboullaite;


import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.exceptions.MessengerVerificationException;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.events.AccountLinkingEvent;
import com.github.messenger4j.receive.handlers.*;
import com.github.messenger4j.send.*;
import com.github.messenger4j.send.buttons.Button;
import com.github.messenger4j.send.templates.GenericTemplate;

import domain.User;
import me.aboullaite.domain.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.*;

import java.sql.*;
import java.util.*;
import java.net.URISyntaxException;
import java.net.URI;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by aboullaite on 2017-02-26.
 */

@RestController
@RequestMapping("/callback")
public class CallBackHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallBackHandler.class);

    private static final String RESOURCE_URL =
            "https://raw.githubusercontent.com/fbsamples/messenger-platform-samples/master/node/public";
    public static final String GOOD_ACTION0 = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_ACTION0";
    public static final String GOOD_ACTION1 = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_ACTION1";
    public static final String GOOD_ACTION2 = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_ACTION2";
    public static final String GOOD_ACTION3 = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_ACTION3";
    
    public static final String GOD_ACTION0 = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOD_ACTION0";
    public static final String GOD_ACTION1 = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOD_ACTION1";
    public static final String GOD_ACTION2 = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOD_ACTION2";
    public static final String GOD_ACTION3 = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOD_ACTION3";

    private final MessengerReceiveClient receiveClient;
    private final MessengerSendClient sendClient;

    /**
     * Constructs the {@code CallBackHandler} and initializes the {@code MessengerReceiveClient}.
     *
     * @param appSecret   the {@code Application Secret}
     * @param verifyToken the {@code Verification Token} that has been provided by you during the setup of the {@code
     *                    Webhook}
     * @param sendClient  the initialized {@code MessengerSendClient}
     */
    @Autowired
    public CallBackHandler(@Value("28115df6638c239afacd1a3d1c2981f6") final String appSecret,
                                            @Value("2749048215") final String verifyToken,
                                            final MessengerSendClient sendClient) {

        logger.debug("Initializing MessengerReceiveClient - appSecret: {} | verifyToken: {}", appSecret, verifyToken);
        this.receiveClient = MessengerPlatform.newReceiveClientBuilder(appSecret, verifyToken)
                .onTextMessageEvent(newTextMessageEventHandler())
                .onQuickReplyMessageEvent(newQuickReplyMessageEventHandler())
                .onPostbackEvent(newPostbackEventHandler())
                .onAccountLinkingEvent(newAccountLinkingEventHandler())
                .onOptInEvent(newOptInEventHandler())
                .onEchoMessageEvent(newEchoMessageEventHandler())
                .onMessageDeliveredEvent(newMessageDeliveredEventHandler())
                .onMessageReadEvent(newMessageReadEventHandler())
                .fallbackEventHandler(newFallbackEventHandler())
                .build();
        this.sendClient = sendClient;
    }

    /**
     * Webhook verification endpoint.
     *
     * The passed verification token (as query parameter) must match the configured verification token.
     * In case this is true, the passed challenge string must be returned by this endpoint.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebhook(@RequestParam("hub.mode") final String mode,
                                                @RequestParam("hub.verify_token") final String verifyToken,
                                                @RequestParam("hub.challenge") final String challenge) {

        logger.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode,
                verifyToken, challenge);
        try {
            return ResponseEntity.ok(this.receiveClient.verifyWebhook(mode, verifyToken, challenge));
        } catch (MessengerVerificationException e) {
            logger.warn("Webhook verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleCallback(@RequestBody final String payload,
                                               @RequestHeader("X-Hub-Signature") final String signature) {

        logger.debug("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
        try {
            this.receiveClient.processCallbackPayload(payload, signature);
            logger.debug("Processed callback payload successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MessengerVerificationException e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private TextMessageEventHandler newTextMessageEventHandler() {
        return event -> {
            logger.debug("Received TextMessageEvent: {}", event);

            final String messageId = event.getMid();
            final String messageText = event.getText();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            User user = get_user_by_id(senderId);
            
            
            
            logger.info("Received message '{}' with text '{}' from user '{}' at '{}'",
                    messageId, messageText, senderId, timestamp);
            Boolean b = false, c = false;
            try {
            	
            	if(messageText.toLowerCase().contains("my name is")){
            		String name = messageText.substring(11, (messageText.length()));
            		b = true;
            		c = true;
            		createUser(new User(senderId,name, "", "", ""));
            		sendTextMessage(senderId, "Ok, "+name+", I live in Kiev and you? (It will be easier for me if you'll answer like 'I live in Kiev'");
            	}
            	
            	if(messageText.toLowerCase().contains("i live in") && null!=user){
            		String city = messageText.substring(10, (messageText.length()));
            		updateUserCity(user,city);
            		b = true;
            		c = true;
            		sendTextMessage(senderId, "Fine, and the most interesting question.");
            		sendQuickReply(senderId);
            	}
            	
                switch (messageText.toLowerCase()) {


                    case "hi":
                    	if(null!=user){
                    		System.out.println("Ooneeeeeeeeeee " + messageText.toLowerCase() +" :" + user.getUser_id());
                        	sendTextMessage(senderId, "Hello, " + user.getUser_name() + ", how is your doing? Whats new in "+ user.getUser_city()+"?" );
                        	c = true;
                    	} else if(!b){
                        	sendTextMessage(senderId, "Hello! My name is YBot and I am a young reminder :). Some information abot myself: I can ask you about your name and where did you live.");
                        	sendTextMessage(senderId, "Later I will ask you about how mush cups of watter did you ussualy drink, than I will propose you to make a reminders.");
                        	sendTextMessage(senderId, "And finaly I will send you several messages per day (not more than 3) to kindly remind you about water. Lets start. :) Whats is your name? (It will be easier for me if you'll answer like 'My name is YBot'");
                        	
                        	c = true;
                    	}
                        break;

                    case "great":
                        sendTextMessage(senderId, "You're welcome :) keep rocking");
                        c = true;
                        break;


                    default:
                        sendReadReceipt(senderId);
                        sendTypingOn(senderId);
                        if(!c)
                        sendTextMessage(senderId,"Sorry, but I didn't get it. I am a young YBot and still learning.");
                       //sendSpringDoc(senderId, messageText);
                        //sendQuickReply(senderId);
                        sendTypingOff(senderId);
                }
            } catch (MessengerApiException | MessengerIOException e) {
                handleSendException(e);
            }
//            } catch (IOException e) {
//                handleIOException(e);
//            }
        };
         
    }
    
    private void addMenu(){
    	
    }

    private void sendSpringDoc(String recipientId, String keyword) throws MessengerApiException, MessengerIOException, IOException {

        Document doc = Jsoup.connect(("https://spring.io/search?q=").concat(keyword)).get();
        String countResult = doc.select("div.search-results--count").first().ownText();
        Elements searchResult = doc.select("section.search-result");
        List<SearchResult> searchResults = searchResult.stream().map(element ->
                        new SearchResult(element.select("a").first().ownText(),
                element.select("a").first().absUrl("href"),
                element.select("div.search-result--subtitle").first().ownText(),
                element.select("div.search-result--summary").first().ownText())
                ).limit(3).collect(Collectors.toList());

        final List<Button> firstLink = Button.newListBuilder()
                .addUrlButton("Open Link", searchResults.get(0).getLink()).toList()
                .build();
final List<Button> secondLink = Button.newListBuilder()
                .addUrlButton("Open Link", searchResults.get(1).getLink()).toList()
                .build();
final List<Button> thirdtLink = Button.newListBuilder()
                .addUrlButton("Open Link", searchResults.get(2).getLink()).toList()
                .build();
final List<Button> searchLink = Button.newListBuilder()
                .addUrlButton("Open Link", ("https://spring.io/search?q=").concat(keyword)).toList()
                .build();



        final GenericTemplate genericTemplate = GenericTemplate.newBuilder()
                .addElements()
                .addElement(searchResults.get(0).getTitle())
                .subtitle(searchResults.get(0).getSubtitle())
                .itemUrl(searchResults.get(0).getLink())
                .imageUrl("https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png")
                .buttons(firstLink)
                .toList()
                .addElement(searchResults.get(1).getTitle())
                .subtitle(searchResults.get(1).getSubtitle())
                .itemUrl(searchResults.get(1).getLink())
                .imageUrl("https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png")
                .buttons(secondLink)
                .toList()
                .addElement(searchResults.get(2).getTitle())
                .subtitle(searchResults.get(2).getSubtitle())
                .itemUrl(searchResults.get(2).getLink())
                .imageUrl("https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png")
                .buttons(thirdtLink)
                .toList()
                .addElement("All results " + countResult)
                .subtitle("Spring Search Result")
                .itemUrl(("https://spring.io/search?q=").concat(keyword))
                .imageUrl("https://upload.wikimedia.org/wikipedia/en/2/20/Pivotal_Java_Spring_Logo.png")
                .buttons(searchLink)
                .toList()
                .done()
                .build();

        this.sendClient.sendTemplate(recipientId, genericTemplate);
    }

    private void sendGifMessage(String recipientId, String gif) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendImageAttachment(recipientId, gif);
    }



    private void sendQuickReply(String recipientId) throws MessengerApiException, MessengerIOException {
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("1-2 cups", GOOD_ACTION0).toList()
                .addTextQuickReply("3-5 cups", GOOD_ACTION1).toList()
                .addTextQuickReply("6 and more", GOOD_ACTION2).toList()
                .addTextQuickReply("I don't count", GOOD_ACTION3).toList()
                .build();

        this.sendClient.sendTextMessage(recipientId, "How much water did you drink ussaly?", quickReplies);
    }
    
    private void sendQuickReply2(String recipientId) throws MessengerApiException, MessengerIOException {
        final List<QuickReply> quickReplies1 = QuickReply.newListBuilder()
                .addTextQuickReply("3 times a day", GOD_ACTION0).toList()
                .addTextQuickReply("Twice a day", GOD_ACTION1).toList()
                .addTextQuickReply("Once a day", GOD_ACTION2).toList()
                .addTextQuickReply("Stop Reminders", GOD_ACTION3).toList()
                .build();

        this.sendClient.sendTextMessage(recipientId, "Please select a reminder frequency:", quickReplies1);
    }

    private void sendReadReceipt(String recipientId) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendSenderAction(recipientId, SenderAction.MARK_SEEN);
    }

    private void sendTypingOn(String recipientId) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendSenderAction(recipientId, SenderAction.TYPING_ON);
    }

    private void sendTypingOff(String recipientId) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendSenderAction(recipientId, SenderAction.TYPING_OFF);
    }

    private QuickReplyMessageEventHandler newQuickReplyMessageEventHandler() {
        return event -> {
            logger.debug("Received QuickReplyMessageEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String messageId = event.getMid();
            final String quickReplyPayload = event.getQuickReply().getPayload();

            logger.info("Received quick reply for message '{}' with payload '{}'", messageId, quickReplyPayload);


                try {
				if (quickReplyPayload.equals(GOOD_ACTION0)) {
					//sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
					sendQuickReply2(senderId);
				}
				if (quickReplyPayload.equals(GOOD_ACTION1)) {
					//sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
					sendQuickReply2(senderId);
				}
				if (quickReplyPayload.equals(GOOD_ACTION2)) {
					//sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
					sendQuickReply2(senderId);
				}
				if (quickReplyPayload.equals(GOOD_ACTION3)) {
					//sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
					sendQuickReply2(senderId);
				}
                    
				if (quickReplyPayload.equals(GOD_ACTION0)) {
					updateUserRemCount(senderId,"1");
					sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
					
				}
				if (quickReplyPayload.equals(GOD_ACTION1)) {
					updateUserRemCount(senderId,"2");
					sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
					
				}
				if (quickReplyPayload.equals(GOD_ACTION2)) {
					updateUserRemCount(senderId,"3");
					sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
					
				}
				if (quickReplyPayload.equals(GOD_ACTION3)) {
					updateUserRemCount(senderId,"0");
					sendGifMessage(senderId, "https://media.giphy.com/media/3oz8xPxTUeebQ8pL1e/giphy.gif");
					
				}
                    
                } catch (MessengerApiException e) {
                    handleSendException(e);
                } catch (MessengerIOException e) {
                    handleIOException(e);
                }

            //sendTextMessage(senderId, "Let's try another one :D!");
        };
    }

    private PostbackEventHandler newPostbackEventHandler() {
        return event -> {
            logger.debug("Received PostbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String payload = event.getPayload();
            final Date timestamp = event.getTimestamp();

            logger.info("Received postback for user '{}' and page '{}' with payload '{}' at '{}'",
                    senderId, recipientId, payload, timestamp);

            sendTextMessage(senderId, "Postback called");
        };
    }

    private AccountLinkingEventHandler newAccountLinkingEventHandler() {
        return event -> {
            logger.debug("Received AccountLinkingEvent: {}", event);

            final String senderId = event.getSender().getId();
            final AccountLinkingEvent.AccountLinkingStatus accountLinkingStatus = event.getStatus();
            final String authorizationCode = event.getAuthorizationCode();

            logger.info("Received account linking event for user '{}' with status '{}' and auth code '{}'",
                    senderId, accountLinkingStatus, authorizationCode);
        };
    }

    private OptInEventHandler newOptInEventHandler() {
        return event -> {
            logger.debug("Received OptInEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String passThroughParam = event.getRef();
            final Date timestamp = event.getTimestamp();

            logger.info("Received authentication for user '{}' and page '{}' with pass through param '{}' at '{}'",
                    senderId, recipientId, passThroughParam, timestamp);

            sendTextMessage(senderId, "Authentication successful");
        };
    }

    private EchoMessageEventHandler newEchoMessageEventHandler() {
        return event -> {
            logger.debug("Received EchoMessageEvent: {}", event);

            final String messageId = event.getMid();
            final String recipientId = event.getRecipient().getId();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received echo for message '{}' that has been sent to recipient '{}' by sender '{}' at '{}'",
                    messageId, recipientId, senderId, timestamp);
        };
    }

    private MessageDeliveredEventHandler newMessageDeliveredEventHandler() {
        return event -> {
            logger.debug("Received MessageDeliveredEvent: {}", event);

            final List<String> messageIds = event.getMids();
            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            if (messageIds != null) {
                messageIds.forEach(messageId -> {
                    logger.info("Received delivery confirmation for message '{}'", messageId);
                });
            }

            logger.info("All messages before '{}' were delivered to user '{}'", watermark, senderId);
        };
    }

    private MessageReadEventHandler newMessageReadEventHandler() {
        return event -> {
            logger.debug("Received MessageReadEvent: {}", event);

            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            logger.info("All messages before '{}' were read by user '{}'", watermark, senderId);
        };
    }

    /**
     * This handler is called when either the message is unsupported or when the event handler for the actual event type
     * is not registered. In this showcase all event handlers are registered. Hence only in case of an
     * unsupported message the fallback event handler is called.
     */
    private FallbackEventHandler newFallbackEventHandler() {
        return event -> {
            logger.debug("Received FallbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            logger.info("Received unsupported message from user '{}'", senderId);
        };
    }

    private void sendTextMessage(String recipientId, String text) {
        try {
            final Recipient recipient = Recipient.newBuilder().recipientId(recipientId).build();
            final NotificationType notificationType = NotificationType.REGULAR;
            final String metadata = "DEVELOPER_DEFINED_METADATA";

            this.sendClient.sendTextMessage(recipient, notificationType, text, metadata);
        } catch (MessengerApiException | MessengerIOException e) {
            handleSendException(e);
        }
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }

    private void handleIOException(Exception e) {
        logger.error("Could not open Spring.io page. An unexpected error occurred.", e);
    }
    
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
    	 return (container -> {
    	        container.setContextPath("");
    	        if(System.getenv("PORT")!=null) {
    	            container.setPort(Integer.valueOf(System.getenv("PORT"))); 

    	        }
    	    });
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
    
    public String createUser(User u) {
        try {
        	
        	if(get_user_by_id(u.getUser_id())==null){
        	
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "insert into user_info(user_id, user_name, user_city, user_hobby) values " +
                    "('" + u.getUser_id()  + "', '" + u.getUser_name() + " ',' " + u.getUser_city() +  "', ' " +
                    u.getUser_hobby() + "');";
            ResultSet rs = stmt.executeQuery(sql);
        } 
        }catch(Exception e){
            //e.printStackTrace();
        }
        return "result";
    }
    
    public String updateUserRemCount(String uid, String rc) {
        try {
        	if(get_user_by_id(uid)!=null){
        	
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "UPDATE user_info SET user_reminders_cout = '" + rc +"' WHERE user_id='"+uid+"'";
            ResultSet rs = stmt.executeQuery(sql);
        } 
        }catch(Exception e){
            //e.printStackTrace();
        }
        return "result";
    }
    
    public String updateUserCity(User u, String city) {
        try {
        	if(get_user_by_id(u.getUser_id())!=null){
        	
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "UPDATE user_info SET user_city = '" + city +"' WHERE user_id='"+u.getUser_id()+"'";
            ResultSet rs = stmt.executeQuery(sql);
        } 
        }catch(Exception e){
            //e.printStackTrace();
        }
        return "result";
    }
    
    public User get_user_by_id(String user_id) {
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "SELECT user_id, user_city, user_name, user_hobby, user_reminders_cout FROM user_info WHERE user_id='"+user_id+"'";
            ResultSet rs = stmt.executeQuery(sql);
            User user = null;
            
            System.out.println(rs);
            
            while (rs.next()) {
                String id = rs.getString("user_id");
                String city = rs.getString("user_city");
                String name = rs.getString("user_name");
                String hb = rs.getString("user_hobby");
                String rmc = rs.getString("user_reminders_cout");
               return (new User(id,name, city, hb, rmc));
            }
           
            return user;
        } catch (Exception e) {
        	System.out.println(e);
            return null;
    }
    }
    
}
