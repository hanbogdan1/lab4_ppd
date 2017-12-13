package Client;

import Common.Produs;
import Common.Stoc;
import Server.iStoreInterface;

import java.util.List;
import java.util.Scanner;

public class Client {

    Scanner keyboard ;
    List<Produs> lista_prod ;

    public Client(List<Produs> lista_prod) {
        this.lista_prod = lista_prod;
        keyboard =  new Scanner(System.in);
    }

    void afisare_meniu(){

        System.out.println("\n\n\n*****************************************************************************************");
        System.out.println("Alegeti un produs pentru a fi adaugat  sau introduce 0 pt a iesii!");

        int i =1;
        for(Produs x :lista_prod){
            System.out.println("-" + i + "-- "+ x.getNume() + "-- Pret : " + x.getPret_unitar());
            i++;
        }
    }

    public String getComanda() {
        afisare_meniu();
        String opt = keyboard.next();

        while (Integer.parseInt(opt) < 1 || Integer.parseInt(opt) > lista_prod.size() ){
            if (Integer.parseInt(opt)  == 0)
                return null;
            System.out.println("Valoare incorecta... Introduce inca o data ! ...");
            opt = keyboard.next();
        }

        return lista_prod.get(Integer.parseInt(opt) - 1).getCod_produs();
    }
}
