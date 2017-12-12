package Common;

import java.io.Serializable;

public class Factura implements Serializable {
    String nume;
    Vanzare vanzare;
    long suma_totala;

    @Override
    public String toString() {
        return "Factura{" +
                "nume='" + nume + '\'' +
                ", vanzare=" + vanzare +
                ", suma_totala=" + suma_totala +
                '}';
    }
}
