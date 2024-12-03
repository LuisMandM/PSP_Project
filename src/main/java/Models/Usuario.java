package Models;

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

    @Column(name = "llaves")
    private KeyPair llaves;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Incidencia> incidencias;


    //region Constructors
    public Usuario(Long codigoEmple, String nombre, String apellido, String email, String username, byte[] password, KeyPair llaves, List<Incidencia> incidencias) {
        this.codigoEmple = codigoEmple;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.username = username;
        this.password = password;
        this.llaves = llaves;
        this.incidencias = incidencias;
    }

    public Usuario() {
    }
    //endregion

    //region Getters n' Setters


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

    public PublicKey getPublicKey() {
        return llaves.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return llaves.getPrivate();
    }

    public KeyPair getLlaves() {
        return llaves;
    }

    public void setLlaves(KeyPair llaves) {
        this.llaves = llaves;
    }

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }
    //endregion
}
