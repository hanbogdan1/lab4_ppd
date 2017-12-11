package Client;

import Server.iStoreInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {
    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("rmiAppContext.xml");
        iStoreInterface clientProst = (iStoreInterface) context.getBean("Service");
        System.out.println(clientProst.testrm());
    }
}
