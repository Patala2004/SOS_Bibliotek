package model;
import java.sql.Date;

public class Usuario {
    private int id;
    private String nombre;
    private Integer matricula;
    private String fecha_nacimiento;
    private String email;
    private Date sancionadoHasta;
    private ResourceLink _links;

    public ResourceLink get_links(){
        return this._links;
    }

    public int getId(){
        return this.id;
    }

    public String getNombre(){
        return this.nombre;
    }

    public Integer getMatricula(){
        return this.matricula;
    }

    public String getFechaNacimiento(){
        return this.fecha_nacimiento;
    }

    public String getEmail(){
        return this.email;
    }

    public Date getSancionadoHasta(){
        return this.sancionadoHasta;
    }

    public void setNombre(String name){
        this.nombre = name ;
    }
    
    public void setId(int usuarioId){
        this.id = usuarioId;
    }

    public void setMatricula(Integer matr){
        this.matricula = matr;
    }

    public void setFechaNacimiento(String fecha){
        this.fecha_nacimiento = fecha;
    }

    public void setEmail(String correo){
        this.email = correo;
    }

    public void setSancionadoHasta(Date sancionadoHasta){
        this.sancionadoHasta = sancionadoHasta;
    }
}

