package Server;

import Common.Factura;
import Common.Produs;
import Common.Stoc;
import Common.Vanzare;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.xml.crypto.Data;
import java.beans.FeatureDescriptor;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Store implements iStoreInterface {

    public float sold;
    public float sold_initial;
    public Date last_scan_date;
    //(cod_produs, Stoc)
    private List<Factura> facturi_emise;
    private HashMap<String, Stoc> stocuri_existente;
    //(cod_produs,Vanzare)
    private MultiValueMap<String, Vanzare> vanzari_efectuate;
    private BufferedWriter bwVanzari;
    private BufferedWriter bwLog;
    private Random rand;

    private ThreadPoolExecutor executor ;
    private ReentrantReadWriteLock lock;

    public Store() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        sold = sold_initial = 0;
        facturi_emise = new ArrayList<Factura>();
        stocuri_existente = new HashMap<String, Stoc>();
        vanzari_efectuate = new LinkedMultiValueMap<String, Vanzare>();
        rand = new Random();
        lock = new ReentrantReadWriteLock();

        try {

            bwVanzari = new BufferedWriter(new FileWriter("src\\main\\resources\\vanzari.txt")) {
                @Override
                public void write(String c) throws IOException {
                    super.write(c);
                    super.flush();
                }
            };
            bwLog = new BufferedWriter(new FileWriter("src\\main\\resources\\log.txt")) {
                @Override
                public void write(String str) throws IOException {
                    super.write(str);
                    super.flush();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        load_data();
        create_thread_vanzari();
//
//        int i = 1;
//        while (true) {
//            i--;
//            if (i==0) {
//                try {
//                    verificare();
//                    i = rand.nextInt(1000);
//                    while(i<=0){
//                        i = rand.nextInt(1000);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            generare_vanzare_interval();
//        }

    }

    private void create_thread_vanzari()  {

        Thread t = new Thread(new Runnable() {
            public void run() {
                int i=0;
                while(true){
                        generare_vanzare();
                        i ++;

                        if ( i > 100){
                            i = 0;
                            verificare();
                        }
                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                }
        });
        t.start();
    }

    private void load_data() {

        try {
            bwLog.write("Loading data \n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\stoc.txt"));
            BufferedWriter wr = new BufferedWriter(new FileWriter("src\\main\\resources\\check.txt"));

            String line;
            while (null != (line = br.readLine())) {
                if (line.equals("")) {
                    break;
                }

                wr.write(line + "\n");
                wr.flush();
                String[] parts = line.split(";");
                stocuri_existente.put(parts[1],
                        new Stoc(new Produs(parts[0], parts[1], Float.parseFloat(parts[2]), parts[3]), Integer.parseInt(parts[4])));
            }

            wr.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Future<String> buy (String cod_produs, Integer cantitate){
        Callable<String> callbl = new BuyCallable(cod_produs,cantitate);
        return  executor.submit(callbl);
    }

    String creare_vanzare(String cod_produs, Integer cantitate){


        try {
            return buy(cod_produs,cantitate).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return "A aparut o eroare ! " ;
    }

    private class BuyCallable implements Callable<String>{
        private String cod_produs;
        private Integer cantitate;

        public BuyCallable(String cod_produs, Integer cantitate) {
            this.cod_produs = cod_produs;
            this.cantitate = cantitate;
        }

        @Override
        public String call() throws Exception {
            return creare_vanz(cod_produs,cantitate);
        }
    }

    String creare_vanz(String cod_produs, Integer cantitate) {

        lock.readLock().lock();

        //validare
        {
            if (cantitate <= 0) {
                lock.readLock().unlock();
                return "Cantitatea introdusa este incorecta";
            }

            if (!stocuri_existente.containsKey(cod_produs)) {
                lock.readLock().unlock();
                return "Produsul ales nu exista";
            }

            if (stocuri_existente.get(cod_produs).getCantitate() < cantitate) {
                lock.readLock().unlock();
                return "Stoc insuficient!";
            }
        }

        synchronized (stocuri_existente.get(cod_produs)) {
            stocuri_existente.get(cod_produs).setCantitate(stocuri_existente.get(cod_produs).getCantitate() - cantitate);
            Vanzare newVanzare = new Vanzare(Calendar.getInstance().getTime(), stocuri_existente.get(cod_produs).getProdus(), cantitate);
            vanzari_efectuate.add(cod_produs, newVanzare);

            sold += cantitate * stocuri_existente.get(cod_produs).getProdus().getPret_unitar();
            try {
                bwLog.write(newVanzare.toString() + "stoc ramas : " + stocuri_existente.get(cod_produs).getCantitate().toString() + "\n");
                bwLog.write("sold = " + sold + "\n\n");

                bwVanzari.write(cod_produs + ";" + cantitate.toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                lock.readLock().unlock();
            }
            return "Adaugare efectuata cu succes!";
        }
    }

    private void generare_vanzare() {
        lock.readLock().lock();
        int index = rand.nextInt(stocuri_existente.size());
        List<String> l = new ArrayList<String>(stocuri_existente.keySet());
        lock.readLock().unlock();
        creare_vanz(l.get(index), rand.nextInt(10));
    }

    @Override
    public String adaugare_produs(String cod_produs) {
        return creare_vanzare(cod_produs, 1);
    }

    @Override
    public List<Produs> get_all_produse() {

        lock.readLock().lock();

        List<Produs> prod = new ArrayList<Produs>();

        for (Stoc st : stocuri_existente.values()) {
            prod.add(st.getProdus());
        }
        lock.readLock().unlock();
        return prod;

    }

    void verificare() {
        try {
            if (verificare_executie() == false) {
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean verificare_executie() throws IOException {

        //(cod_produs, Stoc)

        lock.writeLock().lock();

        HashMap<String, Stoc> map_stoc = new HashMap<String, Stoc>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\check.txt"));
            String line;

            while (null != (line = br.readLine())) {
                if (line.equals("")) {
                    break;
                }

                String[] parts = line.split(";");
                map_stoc.put(parts[1],
                        new Stoc(new Produs(parts[0], parts[1], Float.parseFloat(parts[2]), parts[3]), Integer.parseInt(parts[4])));
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\vanzari.txt"));
            String line;

            while (null != (line = br.readLine())) {
                if (line.equals("")) {
                    break;
                }

                String[] parts = line.split(";");

                sold_initial += stocuri_existente.get(parts[0]).getProdus().getPret_unitar() * Integer.parseInt(parts[1]);
                map_stoc.get(parts[0]).setCantitate(map_stoc.get(parts[0]).getCantitate() - Integer.parseInt(parts[1]));

                bwLog.write("*** Produs: " + stocuri_existente.get(parts[0]).getProdus().getNume() + " -> cantitate : " + parts[1] + " **** Stoc dupa = " + map_stoc.get(parts[0]).getCantitate() + "  Sold =" + sold_initial + "\n");

            }
            br.close();
            bwVanzari = new BufferedWriter(new FileWriter("src\\main\\resources\\vanzari.txt")) {
                @Override
                public void write(String c) throws IOException {
                    super.write(c);
                    super.flush();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ((int) sold_initial != (int) sold) {
            bwLog.write("Solduri diferite ! \n");
            return false;
        }

        try {
            BufferedWriter wrCheck = new BufferedWriter(new FileWriter("src\\main\\resources\\check.txt")) {
                @Override
                public void write(String c) throws IOException {
                    super.write(c);
                    super.flush();
                }
            };

            for (Stoc st : map_stoc.values()) {

                if (!stocuri_existente.get(st.getProdus().getCod_produs()).getCantitate().equals(st.getCantitate())) {
                    bwLog.write("Diferenta gasita pt podusul " + st.getProdus().getCod_produs() + " cantitate = " + st.getCantitate() + "\n");
                    return false;
                }

                wrCheck.write(st.getProdus().getNume() + ";" + st.getProdus().getCod_produs() + ";" + st.getProdus().getPret_unitar()
                        + ";" + st.getProdus().getUnit_masura() + ";" + st.getCantitate() + "\n");
            }

        } catch (Exception x) {
            x.printStackTrace();
        }
        sold_initial = sold;

        bwLog.write("\n---**--- Verificare efectuata cu succes! ---**--- \n\n");

        lock.writeLock().unlock();
        return true;
    }


}
