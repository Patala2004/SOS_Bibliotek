import model.Usuario;
import model.Libro;
import service.UsuariosyLibrosService;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
public class Cliente {
    static UsuariosyLibrosService service = new UsuariosyLibrosService();

    public static void main(String[] args){
        // Usuarios
        System.out.println("-- POST usuarios");
        System.out.println("-- Creamos usuario");
        int uId1 = service.postUsuario("pepe",220121,"2001-12-02","pepe@gmail.com");

        System.out.println("-- Creamos usuario");
        int uId2 = service.postUsuario("carlos",220021,"2001-11-02","carlos@gmail.com");

        System.out.println("-- Creamos usuario");
        int uId3 = service.postUsuario("miguel",220131,"2001-09-20","miguel@gmail.com");
        System.out.println();

        // GETS de usuarios
        System.out.println("-- Comprobaciones de los gets usuarios");

        System.out.println("-- Get de los datos basicos de un usario");
        service.getUsuariosBasico(uId1);
        System.out.println();

        System.out.println("-- Get de todos usuarios");
        service.getUsuarios("");
        System.out.println();

        // PUTS de usuarios
        System.out.println("-- Comprobaciones de los puts de usuarios");
        System.out.println("-- Cambiamos los datos de pepe ");
        service.putUsuario(uId1,"pepe",190234,null,"francisco@upm.es");
        service.getUsuariosBasico(uId1); // vemos las cosas
        



        // Libros
        System.out.println();
        System.out.println("-- POST libros");
        System.out.println("-- Creamos libro");
        int lId1 = service.postLibro("Hola",new String[]{"pepe","juan"},3,"129921-34","planeta",false);

        System.out.println("-- Creamos libro");
        int lId2 = service.postLibro("Quijote",new String[]{"cervantes"},3,"12991-34","sda",false);

        System.out.println("-- Creamos libro");
        int lId3 = service.postLibro("Lazarillo",new String[]{"maria","pepe"},3,"1299321-12","planeta",true);
        System.out.println();

        // GETS de Libros
        System.out.println("-- Comprobaciones de los gets Libros");

        System.out.println("-- Get de todos Libros");
        service.getLibros("");
        System.out.println();
        System.out.println("-- Get de todos Libros que contengan la a ");
        service.getLibros("?contains=a");
        System.out.println();
        System.out.println("-- Get de todos Libros disponibles");
        service.getLibros("?disponible=true");
        System.out.println();
        // PUTS de libros
        System.out.println("-- Comprobaciones de los puts de libros");
        System.out.println("-- Cambiamos los datos de el libro hola ");
        service.getLibroId(lId1);
        System.out.println("-- Cambiamos el titulo y los autores por ejemplo");
        service.putLibro(lId1, "Hola como estas", null, null, null, null);
        service.getLibroId(lId1);

        // POST de prestamos
        System.out.println();
        System.out.println("-- POST prestamo");
        System.out.println("-- Creamos prestamo");
        int pId1 = service.postPrestamo(lId1, uId1);
        
        System.out.println("-- Creamos prestamo");
        int pId2 = service.postPrestamo(lId2, uId2);

        System.out.println("-- Creamos prestamo");
        int pId3 = service.postPrestamo(lId3, uId3);

        // Devolucion
        System.out.println("-- Devolvemos el prestamo correctamente");
        service.getPrestamoId(pId1);

        service.postPrestamoDevolucion(pId1);

        service.getPrestamoId(pId1);

        System.out.println();
        // Fuera de plazo
        System.out.println("-- Devolvemos el prestamo fuera del plazo");
        service.putTestPrestamoAmpliar(pId2, Date.valueOf(LocalDate.now().minusMonths(3)),null);
        service.getPrestamoId(pId2);

        service.postPrestamoDevolucion(pId2);

        service.getPrestamoId(pId2);

        System.out.println();

        System.out.println("-- El usuario" + uId3 + " va prestar los dos libro devueltos el dia de despues");
        int pId4 = service.postPrestamo(lId1, uId3);
        int pId5 = service.postPrestamo(lId2, uId3);
        service.putTestPrestamoAmpliar(pId4, null,Date.valueOf(LocalDate.now().plusWeeks(1)));
        service.putTestPrestamoAmpliar(pId5, null,Date.valueOf(LocalDate.now().plusWeeks(1)));


        System.out.println();
        // GET de los prestamos de un usuario
        System.out.println("-- Vemos todos los libros prestados por el usuario con id " + uId3);

        service.getUsuariosPrestamos(uId3, "?size=3");
        System.out.println();
        System.out.println("-- Vemos todos los libros prestados con filtro de fechas por el usuario con id " + uId3);
        System.out.println("-- por ejemplo vamos a ver los libros que tuvo prestados entre " + Date.valueOf(LocalDate.now().minusWeeks(1)) + " y " + Date.valueOf(LocalDate.now().plusDays(1)));
        service.getUsuariosPrestamos(uId3, "?" + "from=" + Date.valueOf(LocalDate.now().minusWeeks(1)) + "&" + "to=" + Date.valueOf(LocalDate.now().plusDays(1)));
        System.out.println();
        System.out.println();

        // Ampliar
        System.out.println("-- Ampliamos el plazo del prestamo con id" + pId3 + " hasta " +  Date.valueOf(LocalDate.now().plusWeeks(3)));
        service.putPrestamoAmpliar(pId3, Date.valueOf(LocalDate.now().plusWeeks(3)));
        service.getPrestamoId(pId3);
        System.out.println();

        // Historicos
        System.out.println("-- Vemos el historico de usuario con id " + uId1);
        service.getUsuariosHistorico(uId1);
        System.out.println();

        // Actividad
        System.out.println("-- Vemos la actividad de usuario con id " + uId3);
        service.getUsuarioId(uId3);

        // Deletes
        System.out.println();
        System.out.println("-- Deletes de todos los usuarios, libros");


        System.out.println("-- Delete Libro" + lId1);
        service.deleteLibro(lId1);

        System.out.println("-- Delete Libro" + lId2);
        service.deleteLibro(lId2);

        System.out.println("-- Delete Libro" + lId3);
        service.deleteLibro(lId3);

        System.out.println("-- Delete usuario" + uId1);
        service.deleteUsuario(uId1);

        System.out.println("-- Delete usuario" + uId2);
        service.deleteUsuario(uId2);

        System.out.println("-- Delete usuario" + uId3);
        service.deleteUsuario(uId3);
    }
    
}