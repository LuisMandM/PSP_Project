package Models;

import jakarta.persistence.*;

import javax.crypto.SecretKey;
import java.io.Serializable;

@Entity
@Table(name = "Incidencia")
public class Incidencia implements Serializable {

    @Id
    @Column(name = "codIncidencia")
    private Long codIncidencia;

    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    @JoinColumn(name = "id_area", referencedColumnName = "codigo")
    private Area area;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", referencedColumnName = "codigoEmple")
    private Usuario usuario;

    @Column(name = "nivel")
    private Nivel nivel;

    @Column(name = "estado")
    private Estado estado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "key")
    private byte[] key;

    @Column(name = "mensajes")
    private String mensajes;

    //region Constructors
    public Incidencia() {
    }

    public Incidencia(Long codIncidencia, Area area, Usuario usuario, Nivel nivel, Estado estado, String descripcion, byte[] key, String mensajes) {
        this.codIncidencia = codIncidencia;
        this.area = area;
        this.usuario = usuario;
        this.nivel = nivel;
        this.estado = estado;
        this.descripcion = descripcion;
        this.key = key;
        this.mensajes = mensajes;
    }
    //endregion

    //region Getters n' Setters
    public void setCodIncidencia(Long codIncidencia) {
        this.codIncidencia = codIncidencia;
    }

    public Long getCodIncidencia() {
        return codIncidencia;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public String getMensajes() {
        return mensajes;
    }

    public void setMensajes(String mensajes) {
        this.mensajes = mensajes;
    }


    //endregion
}
