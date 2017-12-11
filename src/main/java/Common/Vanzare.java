package Common;

import java.io.Serializable;
import java.util.Date;

public class Vanzare implements Serializable {

    Date data;
    Produs produs;
    int cantitate;

    public Vanzare(Date data, Produs produs, int cantitate) {
        this.data = data;
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
    }
}
