import javax.jms.*;
import javax.naming.*;
import java.util.Properties;
import java.util.Queue;

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

        /*Properties p = new Properties();
        p.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
        p.put("java.naming.provider.url", "jnp://ammann2.fh-reutlingen.de:1099");
        p.put("java.naming.factory.url.pkgs", "org.jnp.interfaces");
*/

        Context ctx = new InitialContext();
        ctx.addToEnvironment("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        ctx.addToEnvironment("java.naming.provider.url", "jnp://ammann2.fh-reutlingen.de:1099");
        //ctx.addToEnvironment("java.naming.factory.url.pkgs", "org.jnp.interfaces");


        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");

        Queue queue = (Queue) ctx.lookup(DESTINATION);

        connection = factory.createQueueConnection(USER, PASSWORD);

        session = connection.createQueueSession(false, session.AUTO_ACKNOWLEDGE);

        sender = session.createSender((javax.jms.Queue) queue);

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
