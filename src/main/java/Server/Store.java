package Server;

import Common.Factura;
import Common.Stoc;
import Common.Vanzare;

import java.util.Date;
import java.util.List;

public class Store implements iStoreInterface {

    List<Factura> facturi_emise;
    List<Stoc> stocuri_existente;
    List<Vanzare> vanzari_efectuate;

    float sold;
    Date last_scan_date;


    public String testrm() {
        return "test passed";
    }
}
