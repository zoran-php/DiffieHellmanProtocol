package dh;

import java.io.File;

/**
 *
 * @author Zoran Davidovic
 */
public class RazmenaKljuceva {
    
    public static final HesAlgoritam HES_ALGORITAM = HesAlgoritam.SHA1;
    
    public static void main(String[] args) {
        
        // Kreiramo osobe Alisa i Bob
        Osoba alisa = new Osoba();
        Osoba bob = new Osoba();
        
        // Alisa i Bob razmenjuju javne kljuceve
        alisa.setPrimljeniJavniKljuc(bob.getJavniKljuc());
        bob.setPrimljeniJavniKljuc(alisa.getJavniKljuc());
        
        // Alisa generise zajednicki simetricni tajni kljuc (DES)
        alisa.generisiZajednickiTajniKljuc();
        
        // Bob generise zajednicki simetricni tajni kljuc (DES)
        bob.generisiZajednickiTajniKljuc();
        
        // Sada Alisa i Bob imaju isti zajednicki tajni simetricni kljuc
        // iako ga nisu razmenili (razmenili su samo javne kljuceve)
        
        String izvornaTajnaPoruka = "Ovo je tajna poruka";
        
        // Alisa sifruje tajnu poruku zajednickim simetricnim tajnim kljucem
        // i vraca je u base64 formatu
        String sifrovanaTajnaPoruka = alisa.sifrujTajnuPoruku(izvornaTajnaPoruka);
        
        // Bob prima sifrovanu tajnu poruku u base64 formatu
        bob.setSifrovanaTajnaPoruka(sifrovanaTajnaPoruka);
        
        // Bob govori sifrovanu tajnu poruku
        System.out.println("Bob (sifrovana tajna poruka): " + bob.getSifrovanaTajnaPoruka());
        
        // Bob desifruje tajnu poruku
        String desifrovanaTajnaPoruka = bob.getDesifrovanaTajnaPoruka();
        
        // Bob govori desifrovanu tajnu poruku
        System.out.println("Bob (desifrovana tajna poruka): " + desifrovanaTajnaPoruka);
        
        /*********************************************************************/
        /********************* PROVERA INTEGRITETA PORUKE ********************/
        /*********************************************************************/
        
        // Izracunavamo hes vrednost izvorne tajne poruke
        String hesVrednostIzvornePoruke = Hes.hesujTekst(izvornaTajnaPoruka, HES_ALGORITAM);
        
        // Izracunavamo hes vrednost desifrovane tajne poruke
        String hesVrednostDesifrovaneTajnePoruke = Hes.hesujTekst(desifrovanaTajnaPoruka, HES_ALGORITAM);
        
        // Ispisujemo hes vrednost izvorne tajne poruke
        System.out.println("Hes vrednost izvorne tajne poruke: " + hesVrednostIzvornePoruke);
        
        // Ispisujemo hes vrednosti desifrovane tajne poruke
        System.out.println("Hes vrednost desifrovane tajne poruke: " + hesVrednostDesifrovaneTajnePoruke);
                
        // Uporedjujemo hes vrednosti izvorne i desifrovane tajne poruke
        if(hesVrednostIzvornePoruke.equals(hesVrednostDesifrovaneTajnePoruke)){
            System.out.println("Izvorna tajna poruka i desifrovana tajna poruka se poklapaju");
        }
        else{
            System.out.println("Izvorna tajna poruka i desifrovana tajna poruka se ne poklapaju");
        }
        
        /*********************************************************************/
        /***************** SIFROVANJE I DESIFROVANJE DATOTEKE ****************/
        /*********************************************************************/
        
        // Kreiramo datoteku koju ce Alisa da sifruje, 
        // a Bob da desifruje zajednickim simetricnim tajnim kljucem
        File izvornaDatoteka = new File("./Otvoreni tekst.txt");
        
        //Kreiramo sifrovanu datoteku (datoteka jos uvek ne postoji)
        File sifrovanaDatoteka = new File("./Sifrovana datoteka.txt");
        
        // Kreiramo desifrovanu datoteku (datoteka jos uvek ne postoji)
        File desifrovanaDatoteka = new File("./Desifrovana datoteka.txt");
        
        // Alisa sifruje datoteku zajednickim simetricnim tajnim kljucem
        alisa.sifrujDatoteku(izvornaDatoteka, sifrovanaDatoteka);
        
        // Bob desifruje datoteku zajednickim simetricnim tajnim kljucem
        bob.desifrujDatoteku(sifrovanaDatoteka, desifrovanaDatoteka);
        
        /*********************************************************************/
        /******************** PROVERA INTEGRITETA DATOTEKE *******************/
        /*********************************************************************/
        
        // Izracunavamo hes vrednost izvorne i desifrovane datoteke
        String hesVrednostIzvorneDatoteke = Hes.hesujDatoteku(izvornaDatoteka, HES_ALGORITAM);
        String hesVrednostDesifrovaneDatoteke = Hes.hesujDatoteku(desifrovanaDatoteka, HES_ALGORITAM);
        
        // Ispisujemo hes vrednost izvorne i desifrovane datoteke
        System.out.println("Hes vrednost originalne datoteke: " + hesVrednostIzvorneDatoteke);
        System.out.println("Hes vrednost desifrovane datoteke: " + hesVrednostDesifrovaneDatoteke);
        
        // Poredimo hes vrednosti izvorne i desifrovane datoteke
        // Ako se hes vrednosti poklapaju, desifrovanje je uspesno
        if(hesVrednostIzvorneDatoteke.equals(hesVrednostDesifrovaneDatoteke)){
            System.out.println("Desifrovana datoteka se poklapa sa originalnom datotekom");
        }
        else{
            System.out.println("Desifrovana datoteka nije ista kao originalna datoteka");
        }
        
    }
    
}
