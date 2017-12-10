package Common;

public class Stoc {
    String cod_produs;
    int cantitate;

    public Stoc(String cod_produs, int cantitate) {
        this.cod_produs = cod_produs;
        this.cantitate = cantitate;
    }

    public String getCod_produs() {
        return cod_produs;
    }

    public void setCod_produs(String cod_produs) {
        this.cod_produs = cod_produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
    }
}
