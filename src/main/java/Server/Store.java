package Server;

import Common.Factura;
import Common.Produs;
import Common.Stoc;
import Common.Vanzare;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.*;

public class Store implements iStoreInterface {

    List<Factura> facturi_emise;

    //(cod_produs, Stoc)
    HashMap<String,Stoc> stocuri_existente;

    //(cod_produs,Vanzare)
    MultiValueMap<String,Vanzare> vanzari_efectuate;

    public float sold;
    public Date last_scan_date;
    private Random rand;

    BufferedWriter bwVanzari ;

    public Store() {
        facturi_emise = new ArrayList<Factura>();
        stocuri_existente = new HashMap<String,Stoc>();
        vanzari_efectuate = new LinkedMultiValueMap<String,Vanzare>();
        rand   = new Random();
        load_data();
        try {
            bwVanzari = new BufferedWriter(new FileWriter("src\\main\\resources\\vanzari.txt"));
            generare_vanzare_interval();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load_data(){
        System.out.println("Loading data");

        try
        {
            BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\stoc.txt"));
            BufferedWriter wr = new BufferedWriter(new FileWriter("src\\main\\resources\\check.txt"));

            String line;
            while (null != (line = br.readLine())) {
                if (line.equals("")){
                    break;
                }
                wr.write(line +"\n");
                System.out.println(line);
                String[] parts = line.split(";");
                stocuri_existente.put(parts[1],
                        new Stoc(new Produs(parts[0],parts[1],Float.parseFloat(parts[2]), parts[3]), Integer.parseInt(parts[4])));
            }
            wr.close();
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    Boolean creare_vanzare(String cod_produs, Integer cantitate ){

        if (cantitate < 0){
            return false;
        }

        if( !stocuri_existente.containsKey(cod_produs)){
            return false;
        }

        if(stocuri_existente.get(cod_produs).getCantitate() < cantitate)
            return false;

        Vanzare newVanzare = new Vanzare(Calendar.getInstance().getTime(), stocuri_existente.get(cod_produs).getProdus(), cantitate);
        vanzari_efectuate.add(cod_produs, newVanzare);

        sold += cantitate * stocuri_existente.get(cod_produs).getProdus().getPret_unitar();

        System.out.println("generare vanzare noua" + newVanzare.toString());

        try {
            bwVanzari.write(cod_produs + " " + cantitate.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void generare_vanzare(){
        int index = rand.nextInt(stocuri_existente.size());
        List<String> l = new ArrayList<String>(stocuri_existente.keySet());
        creare_vanzare(l.get(index), rand.nextInt(100));
    }

    public void generare_vanzare_interval() throws InterruptedException {
        while (true){
            generare_vanzare();
            Thread.sleep(2000);
        }
    }

//    public Boolean verificare(Date data){
//
//        Date acctual_date = Calendar.getInstance().getTime();
//        for (List<Vanzare> lista_vanzari : vanzari_efectuate.values()){
//
//            for (Vanzare y : lista_vanzari){
//                if (y.getData() < )
//            }
//        }
//
//        return true;
//    }

}
