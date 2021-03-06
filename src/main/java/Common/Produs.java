package Common;

import java.io.Serializable;

public class Produs implements Serializable{
    String nume;
    String cod_produs;
    Float pret_unitar;
    String unit_masura;

    @Override
    public String toString() {
        return "Produs{" +
                "nume='" + nume + '\'' +
                ", cod_produs='" + cod_produs + '\'' +
                ", pret_unitar=" + pret_unitar +
                ", unit_masura='" + unit_masura + '\'' +
                '}';
    }

    public Produs(String nume, String cod_produs, Float pret_unitar, String unit_masura) {
        this.nume = nume;
        this.cod_produs = cod_produs;
        this.pret_unitar = pret_unitar;
        this.unit_masura = unit_masura;
    }


    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getCod_produs() {
        return cod_produs;
    }

    public void setCod_produs(String cod_produs) {
        this.cod_produs = cod_produs;
    }

    public float getPret_unitar() {
        return pret_unitar;
    }

    public void setPret_unitar(float pret_unitar) {
        this.pret_unitar = pret_unitar;
    }

    public String getUnit_masura() {
        return unit_masura;
    }

    public void setUnit_masura(String unit_masura) {
        this.unit_masura = unit_masura;
    }

}
