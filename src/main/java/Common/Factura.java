package Common;

import java.io.Serializable;

public class Factura implements Serializable {
    String nume;
    Vanzare vanzare;
    long suma_totala;
}
