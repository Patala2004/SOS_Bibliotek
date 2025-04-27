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
             + "\n matrícula: " + usuario.getMatricula()
             + "\n fecha de nacimiento: " + usuario.getFechaNacimiento()
             + "\n correo: " + usuario.getEmail()
             + "\n sancionadoHasta: " + usuario.getSancionadoHasta()
             + "\n se encuentra disponible en el enlace " + selfLink );
        }
    }
    //METODO GET DEL BASICO DE LOS USUARIOS
    public void getUsuariosBasico(int id){
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
             + "\n matrícula: " + usuario.getMatricula()
             + "\n fecha de nacimiento: " + usuario.getFechaNacimiento()
             + "\n correo: " + usuario.getEmail()
             + "\n se encuentra disponible en el enlace: " + usuario.get_links().getSelf().getHref());
    }
        
     
    //METODO POST USUARIO
    public int postUsuario(String nombre,Integer matricula,String fechaNacimiento,String email){
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
        for(Usuario usuario:usuarios.get_embedded().getUserList()){
            System.out.println("id: " + usuario.getId()
             + "\n nombre: " + usuario.getNombre() 
             + "\n matrícula: " + usuario.getMatricula()
             + "\n fecha de nacimiento: " + usuario.getFechaNacimiento()
             + "\n correo: " + usuario.getEmail()
             + "\n se encuentra disponible en el enlace: " + usuario.get_links().getSelf().getHref());
        }
    }

    //METODO GET DEL HISTORICO DE LOS USUARIOS
    public void getUsuariosPrestamos(int id){
        PagePrestamo prestamos = webClient.get()
        .uri("/usuarios/" + id +"/prestamos")
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 4xx: " + body))
        .then(Mono.empty())) // Permite continuar la ejecución
        .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
        .doOnNext(body -> System.err.println("Error 5xx: " + body))
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
            + "\n id_usuario: " + prestamo.getUser().getId()
            + "\n nombre: " + prestamo.getUser().getNombre()
            + "\n matricula: " + prestamo.getUser().getMatricula()
            + "\n fecha Nacimiento: " + prestamo.getUser().getFechaNacimiento()
            + "\n correo: " + prestamo.getUser().getEmail()
            + "\n fecha de inicio: " + prestamo.getFechaInicio()
            + "\n fecha de fin: " + prestamo.getFechaFin()
            + "\n con devolucion " + prestamo.getFechaDevolucion()
            + "\n se encuentra disponible en el enlace: " + prestamo.get_links().getSelf().getHref());
        }
    }

    //METODO GET DEL HISTORICO DE LOS USUARIOS
    public void getUsuariosHistorico(int id){
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
    public void putTestPrestamoAmpliar(int id,Date fechaFin){
        Prestamo prestamo = new Prestamo();
        if(fechaFin == null){
            System.out.println("Cambia algun dato para hacer put");
            return;
        }
        prestamo.setFechaFin(fechaFin);
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
