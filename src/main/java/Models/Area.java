package Models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

@Entity
@Table(name = "Area")
public class Area implements Serializable {
    @Id
    @Column(name = "codigo")
    private Long codigo;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "password")
    private String password;
    @Column(name = "llaves")
    private KeyPair llaves;

    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    private List<Incidencia> incidencias;

    //region Constructors
    public Area() {
    }

    public Area(Long codigo, String nombre, String password, KeyPair llaves) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.password = password;
        this.llaves = llaves;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PublicKey getPublicKey() {
        return llaves.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return llaves.getPrivate();
    }

    public void setLlaves(KeyPair llaves) {
        this.llaves = llaves;
    }

    public KeyPair getLlaves() {
        return llaves;
    }

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    //endregion
}
