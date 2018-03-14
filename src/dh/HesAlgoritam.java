package dh;

/**
 *
 * @author Zoran Davidovic
 */
public enum HesAlgoritam {
    MD2("MD2"),
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA224("SHA-224"),
    SHA256("SHA-256"),
    SHA512("SHA-512");

    private final String hesAlgoritam;

    private HesAlgoritam(final String algoritam) {
        this.hesAlgoritam = algoritam;
    }

    @Override
    public String toString() {
        return hesAlgoritam;
    }
}
