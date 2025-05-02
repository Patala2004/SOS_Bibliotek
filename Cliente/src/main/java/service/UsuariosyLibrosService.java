package service;

import java.sql.Date;
import java.util.Arrays;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import model.PageUsuario;
import model.Usuario;
import model.Libro;
import model.PageLibros;
import model.PagePrestamo;
import model.Prestamo;
import model.Prestamos;
import reactor.core.publisher.Mono;

public class UsuariosyLibrosService {

    //HE PUESTO LOCALHOST 9000 XQ ES LO Q TENEMOS EN EL POSTMAN SI NO RECUERDO MAL
    private WebClient webClient = WebClient.builder().baseUrl(("http://localhost:9000/api")).build();

    
///////////////////////////////////USUARIOS/////////////////////////////////////////////////////////////////////////
    

    //METODO GET USUARIO
    public void getUsuarioId(int usuarioId){
        System.out.println("/usuarios/" + usuarioId);
        Usuario usuario = webClient.get()
        .uri("/usuarios/" + usuarioId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())
        )
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: "+ body))
        .then(Mono.empty())).bodyToMono(Usuario.class)
        .block();

        if(usuario != null){
            String selfLink = usuario.get_links().getSelf().getHref();
            System.out.println("id: " + usuario.getId()
             + "\n nombre: " + usuario.getNombre() 
             + "\n matricula: " + usuario.getMatricula()
             + "\n fecha de nacimiento: " + usuario.getFechaNacimiento()
             + "\n correo: " + usuario.getEmail()
             + "\n sancionadoHasta: " + usuario.getSancionadoHasta());
            System.out.println("prestamos actuales:");
            for(Libro libro:usuario.getPrestamos()){
                String selfLinkLibro = libro.get_links().getSelf().getHref();
                System.out.println("\tid: " + libro.getId()
                + "\n\t titulo: " + libro.getTitulo()
                + "\n\t autores: " + Arrays.toString(libro.getAutores())
                + "\n\t edicion: " + libro.getEdicion()
                + "\n\t ISBN: " + libro.getISBN()
                + "\n\t editorial: " + libro.getEditorial()
                + "\n\t disponible: " + libro.getDisponible()
                + "\n\t se encuentra disponible en el enlace " + selfLinkLibro );
            }
            System.out.println("historial de prestamos:");
            for(Libro libro:usuario.getHistorial()){
                String selfLinkLibro = libro.get_links().getSelf().getHref();
                System.out.println("\tid: " + libro.getId()
                + "\n\t titulo: " + libro.getTitulo()
                + "\n\t autores: " + Arrays.toString(libro.getAutores())
                + "\n\t edicion: " + libro.getEdicion()
                + "\n\t ISBN: " + libro.getISBN()
                + "\n\t editorial: " + libro.getEditorial()
                + "\n\t disponible: " + libro.getDisponible()
                + "\n\t se encuentra disponible en el enlace " + selfLinkLibro );
            }
            System.out.println(" se encuentra disponible en el enlace: "+selfLink);
        }
    }
    //METODO GET DEL BASICO DE LOS USUARIOS
    public void getUsuariosBasico(int id){
        System.out.println("/usuarios/" + id +"/basico");
        Usuario usuario = webClient.get()
        .uri("/usuarios/" + id +"/basico")
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())) // Permite continuar la ejecución
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .bodyToMono(Usuario.class)
        .block();

        System.out.println("id: " + usuario.getId()
             + "\n nombre: " + usuario.getNombre() 
             + "\n matricula: " + usuario.getMatricula()
             + "\n fecha de nacimiento: " + usuario.getFechaNacimiento()
             + "\n correo: " + usuario.getEmail()
             + "\n sancionadoHasta: " + usuario.getSancionadoHasta()
             + "\n se encuentra disponible en el enlace: " + usuario.get_links().getSelf().getHref());
    }
        
     
    //METODO POST USUARIO
    public int postUsuario(String nombre,Integer matricula,String fechaNacimiento,String email){
        System.out.println("/usuarios");
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setMatricula(matricula);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setEmail(email);
        try{
            String referencia = webClient.post()
            .uri("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(usuario),Usuario.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
            .doOnNext(body -> System.err.println("Error 4xx: " + body))
            .then(Mono.empty()))
            .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
            .doOnNext(body -> System.err.println("Error 5xx: " + body))
            .then(Mono.empty()))
            .toBodilessEntity()
            .map(response -> {
                if(response.getHeaders().getLocation() != null){
                    return response.getHeaders().getLocation().toString();
                }   else{
                    throw new RuntimeException("No se recibió una URL en la cabecera Location");
                }
            })
            .block();
            if (referencia != null) {
                System.out.println("Referencia: " + referencia);
                String[] partes = referencia.split("/");
                String idStr = partes[partes.length - 1];
                return Integer.parseInt(idStr);
            } else {
                throw new RuntimeException("No se recibió referencia del usuario creado");
            }
        }   catch(RuntimeException e){
            System.err.println("Error: " + e.getMessage());
        }
        return -1;
    }
 
    //METODO PUT USUARIO
    public void putUsuario(int usuarioId, String nombre,Integer matricula, String fechaNacimiento, String email){
        System.out.println("/usuarios/" + usuarioId);
        Usuario usuario = new Usuario();
        if(nombre == null && matricula == null && fechaNacimiento == null && email == null){
            System.out.println("Cambia algun dato para hacer put");
            return;
        }
        if(nombre != null)usuario.setNombre(nombre);
        if(matricula != null)usuario.setMatricula(matricula);
        if(fechaNacimiento != null)usuario.setFechaNacimiento(fechaNacimiento);
        if(email != null)usuario.setEmail(email);
        webClient.put()
        .uri("/usuarios/{id}",usuarioId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(usuario),Usuario.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty()))
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .toBodilessEntity()
        .block();
    }
 
    //METODO DELETE USUARIO
    public void deleteUsuario(int usuarioId){
        System.out.println("/usuarios/" + usuarioId);
        webClient.delete()
        .uri("/usuarios/{id}", usuarioId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty()))
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .toBodilessEntity()
        .block();
    }
   
    //METODO GET DE LOS USUARIOS EN DISTINTAS PAGS Y SIZE
    public void getUsuarios(String params){
        System.out.println("/usuarios" + params);
        PageUsuario usuarios = webClient.get()
        .uri("/usuarios" + params)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())) // Permite continuar la ejecución
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .bodyToMono(PageUsuario.class)
        .block();

        System.out.println("Usuarios");
        System.out.println(
            " total de usuarios: " + usuarios.getPage().getTotalElements()
            + "\n Pagina actual: " + usuarios.getPage().getNumber()
            + "\n Tamano de pagina: " + usuarios.getPage().getSize()
            + "\n Numero de paginas: " + usuarios.getPage().getTotalPages()
            + "\n Links");
            if(usuarios.get_links().getFirst()!=null) System.out.println( " First: " + usuarios.get_links().getFirst().getHref());
            if(usuarios.get_links().getSelf()!=null) System.out.println( " Self: " + usuarios.get_links().getSelf().getHref());
            if(usuarios.get_links().getNext()!=null) System.out.println( " Next: " + usuarios.get_links().getNext().getHref());
            if(usuarios.get_links().getLast()!=null) System.out.println( " Last: " + usuarios.get_links().getLast().getHref());
            if(usuarios.get_embedded() == null){
                System.out.println("No se han encontrado usuarios");
                return;
            }
        for(Usuario usuario:usuarios.get_embedded().getUserList()){
            System.out.println("id: " + usuario.getId()
             + "\n nombre: " + usuario.getNombre() 
             + "\n matricula: " + usuario.getMatricula()
             + "\n fecha de nacimiento: " + usuario.getFechaNacimiento()
             + "\n correo: " + usuario.getEmail()
             + "\n se encuentra disponible en el enlace: " + usuario.get_links().getSelf().getHref());
        }
    }

    //METODO GET DEL HISTORICO DE LOS USUARIOS
    public void getUsuariosPrestamos(int id, String params){
        System.out.println("/usuarios/" + id +"/prestamos" + params);
        PagePrestamo prestamos = webClient.get()
        .uri("/usuarios/" + id +"/prestamos" + params)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx (" + response.statusCode().value() + "): " + body))
        .then(Mono.empty())) // Permite continuar la ejecución
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx (" + response.statusCode().value() + "): " + body))
        .then(Mono.empty()))
        .bodyToMono(PagePrestamo.class)
        .block();

        System.out.println("Prestamos");
        for(Prestamo prestamo:prestamos.get_embedded().getUserLibroList()){
            System.out.println("id: " + prestamo.getId()
            + "\n id_libro: " + prestamo.getLibro().getId()
            + "\n titulo: " + prestamo.getLibro().getTitulo()
            + "\n autores: " + Arrays.toString(prestamo.getLibro().getAutores())
            + "\n edicion: " + prestamo.getLibro().getEdicion()
            + "\n isbn: " + prestamo.getLibro().getISBN()
            + "\n editorial: " + prestamo.getLibro().getEditorial()
            + "\n disponible: " + prestamo.getLibro().getDisponible()
            + "\n fecha de inicio: " + prestamo.getFechaInicio()
            + "\n fecha de fin: " + prestamo.getFechaFin()
            + "\n con devolucion " + prestamo.getFechaDevolucion()
            + "\n se encuentra disponible en el enlace: " + prestamo.get_links().getSelf().getHref());
        }
    }

    //METODO GET DEL HISTORICO DE LOS USUARIOS
    public void getUsuariosHistorico(int id){
        System.out.println("/usuarios/" + id +"/historico");
        PageLibros libros = webClient.get()
        .uri("/usuarios/" + id +"/historico")
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())) // Permite continuar la ejecución
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .bodyToMono(PageLibros.class)
        .block();

        System.out.println("Libros");
        for(Libro libro:libros.get_embedded().getLibroList()){
            System.out.println("id: " + libro.getId()
            + "\n titulo: " + libro.getTitulo()
            + "\n autores: " + Arrays.toString(libro.getAutores())
            + "\n edicion: " + libro.getEdicion()
            + "\n ISBN: " + libro.getISBN()
            + "\n editorial: " + libro.getEditorial()
            + "\n disponible: " + libro.getDisponible()
            + "\n se encuentra disponible en el enlace: " + libro.get_links().getSelf().getHref());
        }
    }

    
///////////////////////////////////LIBROS/////////////////////////////////////////////////////////////////////////
    

    //METODO GET LIBRO
    public void getLibroId(int libroId){
        System.out.println("/libros/" + libroId);
        Libro libro = webClient.get()
        .uri("/libros/" + libroId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())
        )
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: "+ body))
        .then(Mono.empty())).bodyToMono(Libro.class)
        .block();

        if(libro != null){
            String selfLink = libro.get_links().getSelf().getHref();
            System.out.println("id: " + libro.getId()
            + "\n titulo: " + libro.getTitulo()
            + "\n autores: " + Arrays.toString(libro.getAutores())
            + "\n edicion: " + libro.getEdicion()
            + "\n ISBN: " + libro.getISBN()
            + "\n editorial: " + libro.getEditorial()
            + "\n disponible: " + libro.getDisponible()
            + "\n se encuentra disponible en el enlace " + selfLink );
        }
    }

    //METODO POST LIBRO
    public int postLibro(String titulo,String[]autores,int edicion,String ISBN, String editorial,boolean disponible){
        System.out.println("/libros");
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setAutores(autores);
        libro.setEdicion(edicion);
        libro.setISBN(ISBN);
        libro.setEditorial(editorial);
        libro.setDisponible(disponible);
        try{
            String referencia = webClient.post()
            .uri("/libros")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(libro),Libro.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
            .doOnNext(body -> System.err.println("Error 4xx: " + body))
            .then(Mono.empty()))
            .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
            .doOnNext(body -> System.err.println("Error 5xx: " + body))
            .then(Mono.empty()))
            .toBodilessEntity()
            .map(response -> {
                if(response.getHeaders().getLocation() != null){
                    return response.getHeaders().getLocation().toString();
                }   else{
                    throw new RuntimeException("No se recibió una URL en la cabecera Location");
                }
            })
            .block();
            if (referencia != null) {
                System.out.println("Referencia: " + referencia);
                String[] partes = referencia.split("/");
                String idStr = partes[partes.length - 1];
                return Integer.parseInt(idStr);
            } else {
                throw new RuntimeException("No se recibió referencia del usuario creado");
            }
        }   catch(RuntimeException e){
            System.err.println("Error: " + e.getMessage());
        }
        return -1;
    }

    //METODO PUT LIBRO
    public void putLibro(int libroId, String titulo,String[]autores,Integer edicion,String ISBN, String editorial){
        System.out.println("/libros/" + libroId);
        Libro libro = new Libro();
        if(titulo == null && autores == null && edicion == null && ISBN == null && editorial == null){
            System.out.println("Cambia algun dato para hacer put");
            return;
        }
        if(titulo != null) libro.setTitulo(titulo);
        if(autores != null) libro.setAutores(autores);
        if(edicion != null) libro.setEdicion(edicion);
        if(ISBN != null) libro.setISBN(ISBN);
        if(editorial != null) libro.setEditorial(editorial);
        webClient.put()
        .uri("/libros/" + libroId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(libro),Libro.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty()))
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .toBodilessEntity()
        .block();
    }

    //METODO DELETE LIBRO
    public void deleteLibro(int libroId){
        System.out.println("/libros/" + libroId);
        webClient.delete()
        .uri("/libros/" + libroId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty()))
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .toBodilessEntity()
        .block();
    }

    //METODO GET LIBROS
    public void getLibros(String params){
        System.out.println("/libros" + params);
        PageLibros libros = webClient.get()
        .uri("/libros" + params)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())) // Permite continuar la ejecución
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .bodyToMono(PageLibros.class)
        .block();

        System.out.println("Libros");
        System.out.println(
            " total de libros: " + libros.getPage().getTotalElements()
            + "\n Pagina actual: " + libros.getPage().getNumber()
            + "\n Tamano de pagina: " + libros.getPage().getSize()
            + "\n Numero de paginas: " + libros.getPage().getTotalPages()
            + "\n Links");
            if(libros.get_links().getFirst()!=null) System.out.println( " First: " + libros.get_links().getFirst().getHref());
            if(libros.get_links().getSelf()!=null) System.out.println( " Self: " + libros.get_links().getSelf().getHref());
            if(libros.get_links().getNext()!=null) System.out.println( " Next: " + libros.get_links().getNext().getHref());
            if(libros.get_links().getLast()!=null) System.out.println( " Last: " + libros.get_links().getLast().getHref());
            if(libros.get_embedded() == null){
                System.out.println("No se han encontrado libros");
                return;
            }
        for(Libro libro:libros.get_embedded().getLibroList()){
            System.out.println("id: " + libro.getId()
            + "\n titulo: " + libro.getTitulo()
            + "\n autores: " + Arrays.toString(libro.getAutores())
            + "\n edicion: " + libro.getEdicion()
            + "\n ISBN: " + libro.getISBN()
            + "\n editorial: " + libro.getEditorial()
            + "\n disponible: " + libro.getDisponible()
            + "\n se encuentra disponible en el enlace: " + libros.get_links().getSelf().getHref());
        }
    }
    


///////////////////////////////////PRESTAMOS/////////////////////////////////////////////////////////////////////////
    

    //METODO GET PRESTAMO
     public void getPrestamos(String params){
        System.out.println("/prestamos" + params);
        PagePrestamo prestamos = webClient.get()
        .uri("/prestamos" + params)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())
        )
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: "+ body))
        .then(Mono.empty())).bodyToMono(PagePrestamo.class)
        .block();
        
        System.out.println("Prestamos");
        System.out.println(
            " total de prestamos: " + prestamos.getPage().getTotalElements()
            + "\n Pagina actual: " + prestamos.getPage().getNumber()
            + "\n Tamano de pagina: " + prestamos.getPage().getSize()
            + "\n Numero de paginas: " + prestamos.getPage().getTotalPages()
            + "\n Links");
            if(prestamos.get_links().getFirst()!=null) System.out.println( " First: " + prestamos.get_links().getFirst().getHref());
            if(prestamos.get_links().getSelf()!=null) System.out.println( " Self: " + prestamos.get_links().getSelf().getHref());
            if(prestamos.get_links().getNext()!=null) System.out.println( " Next: " + prestamos.get_links().getNext().getHref());
            if(prestamos.get_links().getLast()!=null) System.out.println( " Last: " + prestamos.get_links().getLast().getHref());
            if(prestamos.get_embedded() == null){
                System.out.println("No se han encontrado prestamos");
                return;
            }
        for(Prestamo prestamo:prestamos.get_embedded().getUserLibroList()){
            String selfLink = prestamos.get_links().getSelf().getHref();
            System.out.println("id: " + prestamo.getId()
            + "\n id_libro: " + prestamo.getLibro().getId()
            + "\n titulo: " + prestamo.getLibro().getTitulo()
            + "\n autores: " + Arrays.toString(prestamo.getLibro().getAutores())
            + "\n edicion: " + prestamo.getLibro().getEdicion()
            + "\n isbn: " + prestamo.getLibro().getISBN()
            + "\n editorial: " + prestamo.getLibro().getEditorial()
            + "\n disponible: " + prestamo.getLibro().getDisponible()
            + "\n id_usuario: " + prestamo.getUser().getId()
            + "\n nombre: " + prestamo.getUser().getNombre()
            + "\n matricula: " + prestamo.getUser().getMatricula()
            + "\n fecha Nacimiento: " + prestamo.getUser().getFechaNacimiento()
            + "\n correo: " + prestamo.getUser().getEmail()
            + "\n fecha de inicio: " + prestamo.getFechaInicio()
            + "\n fecha de fin: " + prestamo.getFechaFin()
            + "\n con devolucion " + prestamo.getFechaDevolucion()
            + "\n se encuentra disponible en el enlace " + selfLink );
        }
    }

    //METODO GET PRESTAMO ID
    public void getPrestamoId(int prestamoId){
        System.out.println("/prestamos/" + prestamoId);
        Prestamo prestamo = webClient.get()
        .uri("/prestamos/" + prestamoId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())
        )
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: "+ body))
        .then(Mono.empty())).bodyToMono(Prestamo.class)
        .block();

        if(prestamo != null){
            String selfLink = prestamo.get_links().getSelf().getHref();
            System.out.println("id: " + prestamo.getId()
            + "\n id_libro: " + prestamo.getLibro().getId()
            + "\n titulo: " + prestamo.getLibro().getTitulo()
            + "\n autores: " + Arrays.toString(prestamo.getLibro().getAutores())
            + "\n edicion: " + prestamo.getLibro().getEdicion()
            + "\n isbn: " + prestamo.getLibro().getISBN()
            + "\n editorial: " + prestamo.getLibro().getEditorial()
            + "\n disponible: " + prestamo.getLibro().getDisponible()
            + "\n id_usuario: " + prestamo.getUser().getId()
            + "\n nombre: " + prestamo.getUser().getNombre()
            + "\n matricula: " + prestamo.getUser().getMatricula()
            + "\n fecha Nacimiento: " + prestamo.getUser().getFechaNacimiento()
            + "\n correo: " + prestamo.getUser().getEmail()
            + "\n fecha de inicio: " + prestamo.getFechaInicio()
            + "\n fecha de fin: " + prestamo.getFechaFin()
            + "\n con devolucion " + prestamo.getFechaDevolucion()
            + "\n se encuentra disponible en el enlace " + selfLink );
        }
    }

    //METODO POST PRESTAMO
    public int postPrestamo(int idLibro,int idUsuario){
        System.out.println("/prestamos");
        Prestamo prestamo = new Prestamo();
        prestamo.setLibroId(idLibro);
        prestamo.setUserId(idUsuario);
        try{
            String referencia = webClient.post()
            .uri("/prestamos")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(prestamo),Prestamo.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
            .doOnNext(body -> System.err.println("Error 4xx: " + body))
            .then(Mono.empty()))
            .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
            .doOnNext(body -> System.err.println("Error 5xx: " + body))
            .then(Mono.empty()))
            .toBodilessEntity()
            .map(response -> {
                if(response.getHeaders().getLocation() != null){
                    return response.getHeaders().getLocation().toString();
                }   else{
                    throw new RuntimeException("No se recibió una URL en la cabecera Location");
                }
            })
            .block();
            if (referencia != null) {
                System.out.println("Referencia: " + referencia);
                String[] partes = referencia.split("/");
                String idStr = partes[partes.length - 1];
                return Integer.parseInt(idStr);
            } else {
                throw new RuntimeException("No se recibió referencia del usuario creado");
            }
        }   catch(RuntimeException e){
            System.err.println("Error: " + e.getMessage());
        }
        return -1;
    }
 
    //METODO PUT PRESTAMO AMPLIAR
    public void putPrestamoAmpliar(int id,Date fechaFin){
        System.out.println("/prestamos/" + id);
        Prestamo prestamo = new Prestamo();
        if(fechaFin == null){
            System.out.println("Cambia algun dato para hacer put");
            return;
        }
        prestamo.setFechaFin(fechaFin);
        webClient.put()
        .uri("/prestamos/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(prestamo),Prestamo.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty()))
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .toBodilessEntity()
        .block();
    }
    
    //METODO POST PRESTAMO DEVOLUCION
    public void postPrestamoDevolucion(int id){
        System.out.println("/prestamos/" + id + "/devolver");
        Prestamo prestamo = new Prestamo();
        Date fechaDevolucion = java.sql.Date.valueOf(java.time.LocalDate.now());
        prestamo.setFechaDevolucion(fechaDevolucion);
        try{
            String referencia = webClient.post()
            .uri("/prestamos/" + id + "/devolver")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(prestamo),Prestamo.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
            .doOnNext(body -> System.err.println("Error 4xx: " + body))
            .then(Mono.empty()))
            .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
            .doOnNext(body -> System.err.println("Error 5xx: " + body))
            .then(Mono.empty()))
            .toBodilessEntity()
            .map(response -> {
                if(response.getStatusCode().is2xxSuccessful()){
                    return "Devolucion: " + response.getStatusCode().toString();
                }   else{
                    throw new RuntimeException("No completo la solicitud");
                }
            })
            .block();
            if(referencia != null){
                System.out.println(referencia);
            }
        }   catch(RuntimeException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
    public void putTestPrestamoAmpliar(int id,Date fechaFin,Date fechaInicio){
        Prestamo prestamo = new Prestamo();
        if(fechaFin == null && fechaInicio == null){
            System.out.println("Cambia algun dato para hacer put");
            return;
        }
        if(fechaFin != null)prestamo.setFechaFin(fechaFin);
        if(fechaInicio != null)prestamo.setFechaInicio(fechaInicio);
        webClient.put()
        .uri("/prestamos/" + id + "/testingURLToTestSanctionsInClient")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(prestamo),Prestamo.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty()))
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
        .then(Mono.empty()))
        .toBodilessEntity()
        .block();
    }

}
