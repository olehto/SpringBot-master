package me.aboullaite;

import java.util.Date;
import java.util.TimerTask;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.Recipient;

public class MyTimerTask extends TimerTask {

    @Override
    public void run() {
        System.out.println("Timer task started at:"+new Date());
        completeTask();
        System.out.println("Timer task finished at:"+new Date());
    }

    private void completeTask() {
    	MessengerSendClient msc = MessengerPlatform.newSendClientBuilder("EAAEb4X44nC4BAL5g7FehO4RO5dCjtCryA8C01813xWPnNBtK4yJbsCVNFtN5qjBPZCaGiYb8JYDo4Th99MOmMEgm11fCg6H0ycS54cJ74XjA2VZBw3AHOP3cDCIYMIUT9oN8nzTgMZBSIrOyOtBWJbxi7GgIZCPvY1oFXkgSz59elSmmnHAe").build();
    	
    	try {
            final Recipient recipient = Recipient.newBuilder().recipientId("1218787008222363").build();
            final NotificationType notificationType = NotificationType.REGULAR;
            final String metadata = "DEVELOPER_DEFINED_METADATA";

            msc.sendTextMessage(recipient, notificationType, "op", metadata);
        } catch (MessengerApiException | MessengerIOException e) {
            //handleSendException(e);
        }
    	
    	
    	
//        try {
//            //assuming it takes 20 secs to complete the task
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
