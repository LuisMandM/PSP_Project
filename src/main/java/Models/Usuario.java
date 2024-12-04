package Models;

import Server.Utils;
import jakarta.persistence.*;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Usuario")
public class Usuario implements Serializable {

    @Id
    @Column(name = "codigoEmple")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigoEmple;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellido")
    private String apellido;
    @Column(name = "fNacimiento")
    private Date fNacimiento;
    @Column(name = "email")
    private String email;
    @Column(name = "usuario", unique = true)
    private String username;
    @Column(name = "password")
    private byte[] password;

    @Lob
    private byte[] publicKey;

    @Lob
    private byte[] privateKey;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Incidencia> incidencias;


    //region Constructors


    public Usuario() {
    }
    //endregion

    //region Getters n' Setters


    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public Date getfNacimiento() {
        return fNacimiento;
    }

    public void setfNacimiento(Date fNacimiento) {
        this.fNacimiento = fNacimiento;
    }

    public void setCodigoEmple(Long codigoEmple) {
        this.codigoEmple = codigoEmple;
    }

    public Long getCodigoEmple() {
        return codigoEmple;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String usuario) {
        this.username = usuario;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    public PublicKey getPublicKey() {
        return Utils.bytesToPublicKey(this.publicKey);
    }

    public PrivateKey getPrivateKey() {
        return Utils.bytesToPrivateKey(this.privateKey);
    }
    //endregion
}
