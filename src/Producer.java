import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Producer {

    private static final String DESTINATION = "queue/aufgabe7Queue";
    private static final String USER = "guest";
    private static final String PASSWORD = "guest";

    private QueueConnection connection;
    private QueueSession session;
    private QueueSender sender;
    private String text;
    private long expiration;

    public Producer(String text, long expiration) throws NamingException, JMSException {

        this.text = text;
        this.expiration = expiration;

        Context ctx = new InitialContext();

        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");

        Queue queue = (Queue) ctx.lookup(DESTINATION);

        connection = factory.createQueueConnection(USER, PASSWORD);

        session = connection.createQueueSession(false, session.AUTO_ACKNOWLEDGE);

        sender = session.createSender(queue);

    }
    public void sendMessage() throws JMSException {
        TextMessage message = session.createTextMessage();
        message.setText(text);
        sender.setTimeToLive(expiration);
        sender.send(message);
    }


    public void close() throws JMSException {
        sender.close();
        session.close();
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        String text = args[0];
        long expiration = (args.length == 2) ? Long.parseLong(args[1]) : 0;

        Producer producer = new Producer(text, expiration);
        producer.sendMessage();
        producer.close();


    }

}
