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
    public float sold_initial;
    public Date last_scan_date;
    private Random rand;

    BufferedWriter bwVanzari ;

    public Store() {
        sold = sold_initial = 10;
        facturi_emise = new ArrayList<Factura>();
        stocuri_existente = new HashMap<String,Stoc>();
        vanzari_efectuate = new LinkedMultiValueMap<String,Vanzare>();

        rand   = new Random();
        load_data();

        try {
            bwVanzari = new BufferedWriter(new FileWriter("src\\main\\resources\\vanzari.txt"));
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
                wr.flush();
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


    String creare_vanzare(String cod_produs, Integer cantitate ){

        //validare
        {
            if (cantitate <= 0) {
                return "Cantitatea introdusa este incodecta";
            }

            if (!stocuri_existente.containsKey(cod_produs)) {
                return "Produsul ales nu exista";
            }

            if (stocuri_existente.get(cod_produs).getCantitate() < cantitate)
                return "Stoc insuficient!";
        }

        stocuri_existente.get(cod_produs).setCantitate(stocuri_existente.get(cod_produs).getCantitate() - cantitate);
        Vanzare newVanzare = new Vanzare(Calendar.getInstance().getTime(), stocuri_existente.get(cod_produs).getProdus(), cantitate);
        vanzari_efectuate.add(cod_produs, newVanzare);

        sold += cantitate * stocuri_existente.get(cod_produs).getProdus().getPret_unitar();

        System.out.println("generare vanzare noua" + newVanzare.toString() + "stoc ramas : " + stocuri_existente.get(cod_produs).getCantitate().toString());

        try {
            bwVanzari.write(cod_produs + ";" + cantitate.toString() + "\n");
            bwVanzari.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Adaugare efectuata cu succes!";
    }

    private void generare_vanzare(){
        int index = rand.nextInt(stocuri_existente.size());
        List<String> l = new ArrayList<String>(stocuri_existente.keySet());
        creare_vanzare(l.get(index), rand.nextInt(10));
    }

    public void generare_vanzare_interval() throws InterruptedException {
        while (true){
            generare_vanzare();
            Thread.sleep(2000);
        }
    }

    @Override
    public String  adaugare_produs(String cod_produs) {
        return creare_vanzare(cod_produs, 1 );
    }

    @Override
    public List<Produs> get_all_produse() {
        List<Produs> prod = new ArrayList<Produs>();

        for(Stoc st :stocuri_existente.values()){
            prod.add(st.getProdus());
        }
        return  prod;
    }

    public Boolean verificare(){

        //(cod_produs, Stoc)
        HashMap<String,Stoc> map_stoc = new HashMap<String,Stoc>();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\check.txt"));
            String line;

            while (null != (line = br.readLine())) {
                if (line.equals("")){
                    break;
                }

                String[] parts = line.split(";");
                map_stoc.put(parts[1],
                        new Stoc(new Produs(parts[0],parts[1],Float.parseFloat(parts[2]), parts[3]), Integer.parseInt(parts[4])));
            }

            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try
        {
            BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\vanzari.txt"));
            String line;

            while (null != (line = br.readLine())) {
                if (line.equals("")){
                    break;
                }

                String[] parts = line.split(";");

                sold_initial += stocuri_existente.get(parts[0]).getProdus().getPret_unitar() * Integer.parseInt(parts[1]);
                map_stoc.get(parts[0]).setCantitate( map_stoc.get(parts[0]).getCantitate() - Integer.parseInt(parts[1]));
            }
            br.close();
            bwVanzari = new BufferedWriter(new FileWriter("src\\main\\resources\\vanzari.txt"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (sold_initial != sold){
            System.out.println("Solduri diferite !");
            return false ;
        }

        try
        {
            BufferedWriter wrCheck = new BufferedWriter(new FileWriter("src\\main\\resources\\check.txt"));

            for (Stoc st :map_stoc.values()){

                if(! stocuri_existente.get(st.getProdus().getCod_produs()).getCantitate().equals( st.getCantitate())){
                    System.out.println("Diferenta gasita pt podusul " + st.getProdus().getCod_produs());
                    return false ;
                }

                wrCheck.write(st.getProdus().getNume() + ";" + st.getProdus().getCod_produs() + ";" +st.getProdus().getPret_unitar()
                                    + ";" +st.getProdus().getUnit_masura()+ ";"+ st.getCantitate() +"\n");
                wrCheck.flush();
            }

        }catch (Exception   x){
            x.printStackTrace();
        }
        sold_initial = sold;

        System.out.println("Verificare efectuata cu succes!");

        return true;
    }


}
