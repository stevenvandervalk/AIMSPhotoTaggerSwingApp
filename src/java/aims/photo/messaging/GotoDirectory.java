package aims.photo.messaging;

import aims.app.generic.logger.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: gcoleman
 * Date: 7/08/2008
 * Time: 11:38:38
 * To change this template use File | Settings | File Templates.
 */

public class GotoDirectory {

    QueueInfo queueInfo;
    GotoDirectoryListener gotoDirectoryListener;

    public GotoDirectory() {
    }

    public GotoDirectory(GotoDirectoryListener listener) {
        this.gotoDirectoryListener = listener;
    }



//    public void receiveMessage() {
//        if (queueInfo.getQueueConnection() != null) {
//            try {
//                QueueReceiver receiver = queueInfo.getSession().createReceiver(queueInfo.getQueue());
//                TextMessage message = (TextMessage) receiver.receive();
//                if (message != null ) {
//                    LoggerFactory.LogInfo("Message  " + message.getText());
//                    gotoDirectoryListener.messageReceived(message.getText());
//                }
//            } catch (JMSException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//
//    }

    public void sendMessage(String message) {
        try {

            if (queueInfo.getQueueConnection() != null) {

                QueueSender sender = queueInfo.getSession().createSender(queueInfo.getQueue());
                TextMessage tMessage = queueInfo.getSession().createTextMessage( message);

                sender.setTimeToLive(3000);
                sender.send( tMessage);
            }
        } catch (JMSException e) {
            LoggerFactory.LogSevereException(e);
        }
    }




    public void getConnection() {

        File f1 = new File("c:/aims/phototagger/incoming/error");
        File f2 = new File("c:/aims/phototagger/incoming/expired");
        File f3 = new File("c:/aims/phototagger/incoming/processed");
        File f4 = new File("c:/aims/phototagger/incoming/processing");
        File f5 = new File("c:/aims/phototagger/incoming/target");
        File f6 = new File("c:/aims/phototagger/incoming/working");

        f1.mkdirs();
        f2.mkdirs();
        f3.mkdirs();
        f4.mkdirs();
        f5.mkdirs();
        f6.mkdirs();


        Properties properties = new Properties();
// properties.load() would probably be more suitable
        properties.setProperty(
                Context.INITIAL_CONTEXT_FACTORY,
                "net.sf.dropboxmq.jndi.InitialContextFactoryImpl" );
        properties.setProperty( "net.sf.dropboxmq.root", "c:/aims" );
        properties.setProperty(
                "net.sf.dropboxmq.queueConnectionFactories", "qcf" );
        try {
            InitialContext initialContext = new InitialContext( properties );
            Queue queue = (Queue)initialContext.lookup( "phototagger" );

            QueueConnectionFactory connectionFactory
                    = (QueueConnectionFactory)initialContext.lookup( "qcf" );

            QueueConnection connection = connectionFactory.createQueueConnection();

            connection.start();


            QueueSession session = connection.createQueueSession(
                false, QueueSession.AUTO_ACKNOWLEDGE );

            queueInfo = new QueueInfo(queue, connection, session);
        } catch (NamingException e) {
            LoggerFactory.LogSevereException(e);
        } catch (JMSException e) {
            LoggerFactory.LogSevereException(e);
        }

    }

    public void startPolling() {
        getConnection();
        try {
            QueueReceiver receiver = queueInfo.getSession().createReceiver(queueInfo.getQueue());
            
            receiver.setMessageListener(new ThisMessageListener());

        } catch (JMSException e) {
            LoggerFactory.LogSevereException(e);
        }

    }






    private class ThisMessageListener implements MessageListener {

        public void onMessage(Message message) {
            if (message instanceof TextMessage) {
                try {
                    LoggerFactory.LogInfo(((TextMessage)message).getText());
                    gotoDirectoryListener.messageReceived(((TextMessage)message).getText());
                } catch (JMSException e) {
                    LoggerFactory.LogSevereException(e);
                }

            } else {
                LoggerFactory.LogWarning("Not a Text Message");

            }
        }
    }

}
