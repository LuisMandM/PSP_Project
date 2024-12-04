package Models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

@Entity
@Table(name = "Area")
public class Area implements Serializable {
    @Id
    @Column(name = "codigo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "password")
    private byte[] password;
    @Lob
    private byte[] publicKey;

    @Lob
    private byte[] privateKey;

    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    private List<Incidencia> incidencias;

    //region Constructors
    public Area() {
    }


    //endregion

    //region Getters n' Setters
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public PublicKey getPublicKey() {
        return bytesToPublicKey(this.publicKey);
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return bytesToPrivateKey(this.privateKey);
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    //endregion

    private static PublicKey bytesToPublicKey(byte[] publicKeyBytes) {
        PublicKey publicKey = null;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException: " + e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.out.println("InvalidKeySpecException: " + e.getMessage());
        }
        return publicKey;
    }

    private static PrivateKey bytesToPrivateKey(byte[] privateKeyBytes) {
        PrivateKey privateKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = keyFactory.generatePrivate(spec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException: " + e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.out.println("InvalidKeySpecException: " + e.getMessage());
        }

        return privateKey;
    }
}
