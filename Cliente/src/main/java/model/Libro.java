package model;

import java.util.List;

public class Libro {
    private int id;
    private String titulo;
    private String[] autores;
    private int edicion;
    private String isbn;
    private String editorial;
    private boolean disponible;
    private List<Libro> libros;
    private ResourceLink _links;

    public int getId(){
        return this.id;
    }

    public String getTitulo(){
        return this.titulo;
    }

    public String[] getAutores(){
        return this.autores;
    }

    public int getEdicion(){
        return this.edicion;
    }

    public String getISBN(){
        return this.isbn;
    }

    public String getEditorial(){
        return this.editorial;
    }

    public boolean getDisponible(){
        return this.disponible;
    }

    public List<Libro> getLibros(){
        return this.libros;
    }

    public ResourceLink get_links(){
        return this._links;
    }

    public void setId(int usuarioId){
        this.id = usuarioId;
    }

    public void setTitulo(String title){
        this.titulo = title;
    }

    public void setAutores(String[] aut){
        this.autores = aut;
    }

    public void setEdicion(int edic){
        this.edicion = edic;
    }
    public void setISBN(String ISBN){
        this.isbn = ISBN;
    }

    public void setEditorial(String edit){
        this.editorial = edit;
    }

    public void setDisponible(boolean disp){
        this.disponible = disp;
    }

    public void setLibros(List<Libro> books){
        this.libros = books;
    }
}
