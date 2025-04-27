package model;

import java.sql.Date;

public class Prestamo {
    private Libro libro;
    private Usuario user;
    private int userId;
    private int libroId;
    private Date fechaInicio;
    private Date fechaFin;
    private int id;
    private Date fechaDevolucion;
    private ResourceLink _links;

    public Libro getLibro(){
        return this.libro;
    }

    public Usuario getUser(){
        return this.user;
    }

    public int getUserId(){
        return this.userId;
    }

    public int getLibroId(){
        return this.libroId;
    }

    public Date getFechaInicio(){
        return this.fechaInicio;
    }

    public Date getFechaFin(){
        return this.fechaFin;
    }

    public int getId(){
        return this.id;
    }

    public Date getFechaDevolucion(){
        return this.fechaDevolucion;
    }

    public ResourceLink get_links(){
        return this._links;
    }

    public void setTitulo(Libro book){
        this.libro = book;
    }

    public void setUsuario(Usuario usuario){
        this.user = usuario;
    }

    public void setUserId(int id){
        this.userId = id;
    }

    public void setLibroId(int id){
        this.libroId = id;
    }

    public void setFechaInicio(Date inicio){
        this.fechaInicio = inicio;
    }

    public void setFechaFin(Date fin){
        this.fechaFin = fin;
    }

    public void setId(int id_prestamo){
        this.id = id_prestamo;
    }


    public void setFechaDevolucion(Date fechaDev){
        this.fechaDevolucion = fechaDev;
    }


}
