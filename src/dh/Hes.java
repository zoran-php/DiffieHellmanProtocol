package dh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Zoran Davidovic
 */
public class Hes {
    
    public static String hesujDatoteku(File datoteka, HesAlgoritam algoritam){
        String hesVrednost = "";
        try {
            MessageDigest md = MessageDigest.getInstance(algoritam.toString());
            FileInputStream ulazniTokPodatakaDatoteke = new FileInputStream(datoteka.getAbsoluteFile());
            byte[] buffer = new byte[4096];
            int brojProcitanihBajtova;
            while((brojProcitanihBajtova = ulazniTokPodatakaDatoteke.read(buffer)) != -1){
                md.update(buffer, 0, brojProcitanihBajtova);
            }
            byte[] hesBajtovi = md.digest();
            hesVrednost = DatatypeConverter.printHexBinary(hesBajtovi).toLowerCase();
        } catch (NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(Hes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hesVrednost;
    }
    
    public static String hesujTekst(String tekst, HesAlgoritam algoritam){
        String hesVrednost = "";
        try{
            MessageDigest md = MessageDigest.getInstance(algoritam.toString());
            md.update(tekst.getBytes("UTF8"));
            byte[] hesBajtovi = md.digest();
            hesVrednost = DatatypeConverter.printHexBinary(hesBajtovi).toLowerCase();
        }
        catch(NoSuchAlgorithmException | UnsupportedEncodingException ex){
            Logger.getLogger(Hes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hesVrednost;
    }
    
}
