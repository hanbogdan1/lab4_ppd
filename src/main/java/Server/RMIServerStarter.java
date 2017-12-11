package Server;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RMIServerStarter {

    public static void main(String[] args) {

        //RMI Server Application Context is started...
        new ClassPathXmlApplicationContext("rmiServer.xml");
    }
}