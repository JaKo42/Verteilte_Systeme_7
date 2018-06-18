import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class Consumer {

    private static final String DESTINATION = "queue/aufgabe7Queue";
    private static final String USER = "guest";
    private static final String PASSWORD = "guest";

    private QueueConnection connection;
    private QueueSession session;
    private QueueReceiver receiver;
    private long timeout;

    public Consumer(long timeout) throws NamingException, JMSException {
        this.timeout = timeout;

        Context ctx = new InitialContext();

        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");

        Queue queue = (Queue) ctx.lookup(DESTINATION);

        connection = factory.createQueueConnection(USER, PASSWORD);

        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        receiver = session.createReceiver(queue);

        connection.start();

    }

    public void receiveMessage() throws JMSException {
        Message message;
        while ((message = receiver.receive(timeout)) != null) {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println(textMessage.getText());
            }
        }
    }

    public void close() throws JMSException {
        receiver.close();
        session.close();
        connection.close();
    }
    public static void main(String[] args) throws Exception {
        long timeout = Long.parseLong(args[0]);
        Consumer consumer = new Consumer(timeout);
        consumer.receiveMessage();
        consumer.close();
    }
}
