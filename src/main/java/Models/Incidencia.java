package Models;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "Incidencia")
public class Incidencia implements Serializable {

    @Id
    @Column(name = "codIncidencia")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codIncidencia;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
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

    @Column(name = "tiempo")
    private Long tiempo;

    @Lob
    private byte[] llave;

    @Column(name = "mensajes")
    private byte[] mensajes;

    //region Constructors
    public Incidencia() {
    }

    public Incidencia(Long codIncidencia, Area area, Usuario usuario, Nivel nivel, Estado estado, String descripcion, byte[] llave, byte[] mensajes) {
        this.codIncidencia = codIncidencia;
        this.area = area;
        this.usuario = usuario;
        this.nivel = nivel;
        this.estado = estado;
        this.descripcion = descripcion;
        this.llave = llave;
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

    public byte[] getLlave() {
        return llave;
    }

    public void setLlave(byte[] key) {
        this.llave = key;
    }

    public byte[] getMensajes() {
        return mensajes;
    }

    public void setMensajes(byte[] mensajes) {
        this.mensajes = mensajes;
    }

    public Long getTiempo() {
        return tiempo;
    }

    public void setTiempo(Long tiempo) {
        this.tiempo = tiempo;
    }

    //endregion
}
