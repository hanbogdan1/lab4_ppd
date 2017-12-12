package Common;

import java.io.Serializable;

public class Stoc implements Serializable {

    Produs produs;
    Integer cantitate;

    public Stoc(Produs produs, Integer cantitate) {
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public Integer getCantitate() {
        return cantitate;
    }

    public void setCantitate(Integer cantitate) {
        this.cantitate = cantitate;
    }

}
