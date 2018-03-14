package dh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 *
 * @author Zoran Davidovic
 */
public class Osoba {
    
    private PrivateKey       privatniKljuc;
    private PublicKey        javniKljuc;
    private PublicKey        primljeniJavniKljuc;
    private SecretKey        zajednickiTajniKljuc;
    private String           sifrovanaTajnaPoruka;
    private final String     simetricniAlgoritam = "DES";
    private final String     asimetricniAlgoritam = "DH";
    private final String     enkodovanjeKaraktera = "UTF8";

    public Osoba() {
        generisiAsimetricneKljuceve();
    }
    
    private void generisiAsimetricneKljuceve() {
        try {
            KeyPairGenerator generatorAsimetricnihKljuceva = KeyPairGenerator.getInstance(asimetricniAlgoritam); //DH za Diffie-Hellman
            generatorAsimetricnihKljuceva.initialize(512, new SecureRandom()); // Moze i 1024, 2048,... (sto je veci kljuc vise procesor radi)
            KeyPair asimetricniKljucevi = generatorAsimetricnihKljuceva.generateKeyPair();
            this.privatniKljuc = asimetricniKljucevi.getPrivate();
            this.javniKljuc  = asimetricniKljucevi.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Osoba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setPrimljeniJavniKljuc(PublicKey primljeniKljuc){
        this.primljeniJavniKljuc = primljeniKljuc;
    }
    
    public PublicKey getJavniKljuc() {
        return javniKljuc;
    }
    
    public void setSifrovanaTajnaPoruka(String poruka){
        this.sifrovanaTajnaPoruka = poruka;
    }
    
    public String getSifrovanaTajnaPoruka(){
        return sifrovanaTajnaPoruka;
    }
    
    public String getDesifrovanaTajnaPoruka(){
        return desifrujTajnuPoruku();
    }
    
    public void generisiZajednickiTajniKljuc() {
        try {
            KeyAgreement dogovor = KeyAgreement.getInstance(asimetricniAlgoritam); // DH za Diffie-Hellman
            dogovor.init(privatniKljuc);
            dogovor.doPhase(primljeniJavniKljuc, true);
            byte[] tajna = dogovor.generateSecret(); // Vraca 64 nasumicna bajta jer je DH inicijalizovan sa 512 bita (64 bajta)
            SecretKeyFactory fabrikaTajnogKljuca = SecretKeyFactory.getInstance(simetricniAlgoritam);
            DESKeySpec specifikacijeDESKljuca = new DESKeySpec(tajna); // Uzima se prvih 8 bajtova iz tajne kako bi se napravio DES kljuc (moze se staviti i offset)
            zajednickiTajniKljuc = fabrikaTajnogKljuca.generateSecret(specifikacijeDESKljuca);
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Osoba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String desifrujTajnuPoruku() {
        String desifrovanaPoruka = "";
        try {
            Cipher sifra = Cipher.getInstance(String.format("%s/ECB/PKCS5Padding", simetricniAlgoritam));
            sifra.init(Cipher.DECRYPT_MODE, zajednickiTajniKljuc);
            byte[] bajtoviSifrovanePoruke = Base64.getDecoder().decode(sifrovanaTajnaPoruka);
            desifrovanaPoruka = new String(sifra.doFinal(bajtoviSifrovanePoruke), enkodovanjeKaraktera);
        } catch (UnsupportedEncodingException | InvalidKeyException | 
                NoSuchAlgorithmException | BadPaddingException | 
                IllegalBlockSizeException | NoSuchPaddingException ex) {
            Logger.getLogger(Osoba.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return desifrovanaPoruka;
    }
    
    public String sifrujTajnuPoruku(final String poruka) {
        String base64SifrovanaPoruka = "";
        try {
            Cipher sifra = Cipher.getInstance(String.format("%s/ECB/PKCS5Padding", simetricniAlgoritam));
            sifra.init(Cipher.ENCRYPT_MODE, zajednickiTajniKljuc);
            byte[] bajtoviSifrovanePoruke = sifra.doFinal(poruka.getBytes(enkodovanjeKaraktera));
            base64SifrovanaPoruka = Base64.getEncoder().encodeToString(bajtoviSifrovanePoruke);
        } catch (UnsupportedEncodingException | InvalidKeyException | 
                NoSuchAlgorithmException | BadPaddingException | 
                IllegalBlockSizeException | NoSuchPaddingException ex) {
            Logger.getLogger(Osoba.class.getName()).log(Level.SEVERE, null, ex);
        }
        return base64SifrovanaPoruka;
    }
    
    public void sifrujDatoteku(File izvornaDatoteka, File sifrovanaDatoteka){
        
        try (FileInputStream ulazniTokPodatakaDatoteke = new FileInputStream(izvornaDatoteka);
                FileOutputStream izlazniTokPodatakaDatoteke = new FileOutputStream(sifrovanaDatoteka)) {
            Cipher sifra = Cipher.getInstance(String.format("%s/ECB/PKCS5Padding", simetricniAlgoritam));
            sifra.init(Cipher.ENCRYPT_MODE, zajednickiTajniKljuc);
            try (CipherOutputStream izlazniTokPodatakaSifre = new CipherOutputStream(izlazniTokPodatakaDatoteke, sifra)) {
                kopiraj(ulazniTokPodatakaDatoteke, izlazniTokPodatakaSifre);
            }
        }
         catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | FileNotFoundException ex) {
            Logger.getLogger(Osoba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Osoba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void desifrujDatoteku(File sifrovanaDatoteka, File desifrovanaDatoteka){
        
        try (FileInputStream ulazniTokPodatakaDatoteke = new FileInputStream(sifrovanaDatoteka);
                FileOutputStream izlazniTokPodatakaDatoteke = new FileOutputStream(desifrovanaDatoteka)) {
            Cipher sifra = Cipher.getInstance(String.format("%s/ECB/PKCS5Padding", simetricniAlgoritam));
            sifra.init(Cipher.DECRYPT_MODE, zajednickiTajniKljuc);
            try (CipherInputStream ulazniTokPodatakaSifre = new CipherInputStream(ulazniTokPodatakaDatoteke, sifra)) {
                kopiraj(ulazniTokPodatakaSifre, izlazniTokPodatakaDatoteke);
            }
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | FileNotFoundException ex) {
            Logger.getLogger(Osoba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Osoba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void kopiraj(InputStream ulazniTokPodataka, OutputStream izlazniTokPodataka) throws IOException{
        byte[] buffer = new byte[64];
        int brojProcitanihBajtova;
        while((brojProcitanihBajtova = ulazniTokPodataka.read(buffer)) != -1){
            izlazniTokPodataka.write(buffer, 0, brojProcitanihBajtova);
        }
    }
    
}
