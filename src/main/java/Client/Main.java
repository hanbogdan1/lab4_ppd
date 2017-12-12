package Client;

import Server.iStoreInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("rmiAppContext.xml");
        iStoreInterface clientProst = (iStoreInterface) context.getBean("Service");

        Client clnt = new Client(clientProst.get_all_produse());
        String cod_prod;
        while ((cod_prod = clnt.getComanda()) != null) {
            System.out.println(clientProst.adaugare_produs(cod_prod));
        }
    }
}
