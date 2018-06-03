package simpleproducer;

import javax.jms.*;

import jndi.JndiUtil; 

import java.util.logging.Logger;



/**
 * Un SimpleProducer e' in grado di inviare una sequenza di messaggi
 * ad una generica destinazione JMS (coda o argomento),
 * intervallati tra loro da un ritardo.
 *
 * I messaggi sono ricevuti da oggetti SynchConsumer (in modo sincrono)
 * oppure da AsynchConsumer (in modo asincrono).
 *
 * Variante di un esempio nel tutorial per Java EE.
 *
 * @author Luca Cabibbo
 */
public class SimpleProducer {

	/* logger */
	private static Logger logger = Logger.getLogger("asw.jms.simpleproducer");

	/* nome di questo producer */
    private String name;
    
    /* destinazione di questo producer */
    private Destination destination;
   
    /* connection factory di questo producer */
    private ConnectionFactory connectionFactory;

    /* contesto jms */
//    private JMSContext context = null;
    /* per l'invio di messaggi a destination */
//  private JMSProducer messageProducer = null;
    
    private MessageProducer messageProducer = null;

    /* numero di messaggi inviati */
    private int messagesSent;
    
    private Session s=null;
    private Connection c= null;

    /** Crea un nuovo SimpleProducer, di nome n, per una destinazione destinationName. */
    public SimpleProducer(String n, String destinationName, String connectionFactoryName) {

        this.name = n;
        this.destination = (Destination) JndiUtil.getInstance().jndiLookup(destinationName);
        this.connectionFactory = (ConnectionFactory) JndiUtil.getInstance().jndiLookup(connectionFactoryName);
        this.messagesSent = 0;
    }
	
    /** Crea un nuovo SimpleProducer, di nome n, per una destinazione d. */
    public SimpleProducer(String n, Destination d, ConnectionFactory cf) {

        this.name = n;
        this.destination = d;
        this.connectionFactory = cf;
        this.messagesSent = 0;
    }

    /** Apre la connessione alla destinazione JMS. */
    public void connect() {
    	logger.info(this.toString() + ": Connecting...");
    	try {
			c = connectionFactory.createConnection();
			c.start();
			s=c.createSession(false, Session.AUTO_ACKNOWLEDGE);
			messageProducer=s.createProducer(destination);
		} catch (JMSException e) {
			logger.info("Error while connection message: "+e.toString());
			
		}
//        context = connectionFactory.createContext();
//    	messageProducer = context.createProducer();
    	logger.info(this.toString() + ": Connected");
    }

    /** Si disconnette dalla destinazione JMS. */
    public void disconnect() {
        if (s != null && c!= null) {
        	logger.info(this.toString() + ": Disconnecting...");
        	
        	try {
				s.close();
				c.close();
			} catch (JMSException e) {
				logger.info(": Error while disconneting message: "+e.toString());
			}   
        	c=null;
            s = null;
            
        	logger.info(this.toString() + ": Disconnected (" + messagesSent + " message(s) sent)");
        }
    }

    /** Invia un messaggio di testo text alla destinazione */
    public void sendMessage(String text) {
        try {
        	TextMessage message = s.createTextMessage();
            message.setText(text);
            logger.info(this.name + ": Sending message: " + message.getText());
            messageProducer.send(destination, message);
            this.messagesSent++;
        } catch (JMSException e) {
        	logger.info(this.name + ": Error while sending message: " + e.toString());
        }
    }

    /** Restituisce una descrizione di questo producer. */
    public String toString() {
        return "SimpleProducer[" +
                "name=" + name +
//                ", destination=" + destination.toString() +
                "]";

    }
	
}
