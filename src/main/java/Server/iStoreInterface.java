package Server;

import Common.Factura;
import Common.Produs;
import Common.Stoc;
import Common.Vanzare;

import java.util.Date;
import java.util.List;

public interface iStoreInterface {
    public String adaugare_produs(String cod_produs);
    List<Produs> get_all_produse();
}
