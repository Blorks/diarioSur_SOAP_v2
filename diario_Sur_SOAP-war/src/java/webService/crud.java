/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webService;

import entity.Archivos;
import entity.Calendario;
import entity.Dateev;
import entity.Evento;
import entity.Fileev;
import entity.Notificacion;
import entity.Tag;
import entity.Tagevento;
import entity.Tagusuario;
import entity.Usuario;
import facade.ArchivosFacade;
import facade.CalendarioFacade;
import facade.DateevFacade;
import facade.EventoFacade;
import facade.FileevFacade;
import facade.NotificacionFacade;
import facade.TagFacade;
import facade.TageventoFacade;
import facade.TagusuarioFacade;
import facade.UsuarioFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author Dani
 */
@WebService(serviceName = "crud")
public class crud {
    @EJB
    private EventoFacade eventoFacade;
    @EJB
    private FileevFacade fileevFacade;
    @EJB
    private DateevFacade dateevFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private CalendarioFacade calendarioFacade;
    @EJB
    private ArchivosFacade archivosFacade;
    @EJB
    private NotificacionFacade notificacionFacade;
    @EJB
    private TagFacade tagFacade;
    @EJB
    private TageventoFacade tagEventoFacade;
    @EJB
    private TagusuarioFacade tagUsuarioFacade;
    
    /* METODOS PARA LO REFERENTE A LOS EVENTOS */

    @WebMethod(operationName = "encontrarEventoPorID") //Devuelve una lista con un solo evento
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public Evento encontrarEventoPorID(@WebParam(name = "id") int id) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByID(id);
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento.get(0);
        }
    }
    
    @WebMethod(operationName = "encontrarTodosLosEventosRevisados") //Devuelve una lista con todos los eventos
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public List<Evento> encontrarTodosLosEventosRevisados() {
        return eventoFacade.encontrarEventosRevisados();
    }
    
    @WebMethod(operationName = "encontrarTodosLosEventos") //Devuelve una lista con todos los eventos
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public List<Evento> encontrarTodosLosEventos() {
        return eventoFacade.findAll();
    }
    

    @WebMethod(operationName = "crearEvento")
    public int crearEvento(@WebParam(name = "titulo") String titulo, @WebParam(name = "subtitulo") String subtitulo, 
                @WebParam(name = "descripcion") String descripcion,@WebParam(name = "direccionFisica") String direccionFisica,
                @WebParam(name = "precio") double precio ,@WebParam(name = "estaRevisado") boolean estaRevisado,
                @WebParam(name = "idUsuario") int idUsuario) {
        
        Usuario user = encontrarUsuarioPorID(idUsuario);
        Evento evento = new Evento();
        int revisado=0;
        if(estaRevisado){
            revisado=1;
        }
        
        evento.setTitulo(titulo);
        evento.setSubtitulo(subtitulo);
        evento.setDescripcion(descripcion);
        evento.setDireccionfisica(direccionFisica);
        evento.setPrecio(precio);
        evento.setEstarevisado(revisado);
        evento.setDateevId(0);
        evento.setUsuarioId(user);
        
        eventoFacade.create(evento);

        List<Evento> ev = eventoFacade.ultimoIDInsertado();
        int idEvento = ev.get(0).getId();
        List<Evento> eventos = eventoFacade.encontrarEventoByID(idEvento);
        
        if(eventos.isEmpty()){
            idEvento = -1;
        }else{
            Calendario cal = new Calendario();
            cal.setEventoId(eventos.get(0));
            cal.setUsuarioId(user);
            
            calendarioFacade.create(cal);
        }
        
        return idEvento;
    }
    
    @WebMethod(operationName = "editarEvento")
    // ATRIBUTOS
    // idEvento -> solo se usa para buscar el evento a actualizar. No se puede modificar
    
    // descripcion -> String con la descripción del evento (maximo 4000 caracteres)
    // direccionFisica -> String con la dirección del evento (maximo 4000 caracteres)
    // precio -> el atributo es DOUBLE, no float
    // estaRevisado -> es un booleano, osea que espero true o false, pero en la BD se guarda como numero
    
    // DateevId y FileevId se inicializan a 0 para evitar problemas con el null, ya que si son necesarios, se usará otra función para añadirlos
  
    // la funcion devuelve true si el evento se ha actualizado correctamente, false si no
    public void editarEvento(@WebParam(name = "idEvento") int idEvento, @WebParam(name = "titulo") String titulo, 
            @WebParam(name = "subtitulo") String subtitulo, @WebParam(name = "descripcion") String descripcion,
            @WebParam(name = "direccionFisica") String direccionFisica, @WebParam(name = "precio") double precio ,
            @WebParam(name = "estaRevisado") boolean estaRevisado) {
        
        Evento evento = encontrarEventoPorID(idEvento);
        int revisado=0;
        if(estaRevisado){
            revisado=1;
        }

        evento.setTitulo(titulo);
        evento.setSubtitulo(subtitulo);
        evento.setDescripcion(descripcion);
        evento.setDireccionfisica(direccionFisica);
        evento.setPrecio(precio);
        evento.setEstarevisado(revisado);
        
        eventoFacade.edit(evento);
    }
    
    @WebMethod(operationName = "revisarEvento") //Cambia el estado del evento al booleano que se le pasa
    public boolean revisarEvento(@WebParam(name = "id") int id, @WebParam(name = "estaRevisado") boolean estaRevisado) {
        Evento evento = encontrarEventoPorID(id);
        
        int revisado=0;
        if(estaRevisado){
            revisado=1;
        }
        
        evento.setEstarevisado(revisado);
        eventoFacade.edit(evento);
        
        Evento eventoTemp = encontrarEventoPorID(id);
        
        boolean success = true;
        
        if(eventoTemp.getEstarevisado() != revisado){
            success = false;
        }
        return success;
    }
    
    @WebMethod(operationName = "eliminarEvento") //Cambia el estado del evento al booleano que se le pasa
    public boolean eliminarEvento(@WebParam(name = "id") int id) {
        Evento evento = encontrarEventoPorID(id);
        List<Calendario> listaCalendario = calendarioFacade.encontrarCalendarioPorEvento(evento);
        List<Archivos> listaArchivos = archivosFacade.encontrarArchivoPorEvento(evento);
        List<Tagevento> listaTag = tagEventoFacade.encontrarTagEv(evento);
        
        for(int i=0; i<listaCalendario.size(); i++){
            calendarioFacade.remove(listaCalendario.get(i));
        }
        
        String url;
        for(int i=0; i<listaArchivos.size(); i++){
            url = listaArchivos.get(i).getFileevId().getUrl();
            archivosFacade.remove(listaArchivos.get(i));
            List<Archivos> ar = archivosFacade.encontrarArchivoPorFile(listaArchivos.get(i).getFileevId());
            if(ar.isEmpty()){
                List<Fileev> file = fileevFacade.encontrarArchivoPorURL(url);
                fileevFacade.remove(file.get(0));
            }
        }
        
        String nombre;
        for(int i=0; i<listaTag.size(); i++){
            nombre = listaTag.get(i).getTagId().getNombre();
            tagEventoFacade.remove(listaTag.get(i));
            List<Tagevento> te = tagEventoFacade.encontrarTagEvPorTag(listaTag.get(i).getTagId());
            if(te.isEmpty()){
                List<Tag> tag = tagFacade.encontrarTagPorNombre(nombre);
                tagFacade.remove(tag.get(0));
            }
        }
        
        eventoFacade.remove(evento);
        
        List<Evento> eventoTemp = eventoFacade.encontrarEventoByID(id);
        
        boolean success = false;
        
        if(eventoTemp.isEmpty()){
            success = true;
        }
        return success;
    }
    
    
    /* METODOS PARA LO REFERENTE A LOS ARCHIVOS */

    @WebMethod(operationName = "encontrarArchivosDeEvento") //Cambia el estado del evento al booleano que se le pasa
    public List<Fileev> encontrarArchivosDeEvento(@WebParam(name = "idEvento") int idEvento){
        Evento ev = encontrarEventoPorID(idEvento);
        List<Archivos> listaArchivos = archivosFacade.encontrarArchivoPorEvento(ev);
        List<Fileev> listaFile = new ArrayList<>();
        
        for(int i=0; i<listaArchivos.size(); i++){
            listaFile.add(listaArchivos.get(i).getFileevId());
        }


        return listaFile;
    }
    
    private List<Fileev> crearArchivo(String nombre, String url, String tipo) {
        Fileev archivo = new Fileev();
        
        List<Fileev> listaArchivo = fileevFacade.encontrarArchivoPorURL(url);  //supongo que la url es unica por archivo
        
        if(listaArchivo.isEmpty()){
            archivo.setNombre(nombre);
            archivo.setUrl(url);
            archivo.setTipo(tipo);

            fileevFacade.create(archivo);
            
            listaArchivo = fileevFacade.encontrarArchivoPorURL(url);
        }
        
        return listaArchivo; //con esto consigo que no se creen archivos duplicados. Si el archivo ya está en la BD, lo referencio en vez
                                // de crear uno nuevo
    }
    
    
    
    @WebMethod(operationName = "adjuntarArchivo") //Cambia el estado del evento al booleano que se le pasa
    public boolean adjuntarArchivo(@WebParam(name = "nombre") String nombre, @WebParam(name = "URL") String url, @WebParam(name = "tipo") String tipo,
            @WebParam(name = "idEvento") int idEvento){
        
        List<Fileev> archivoCreado = crearArchivo(nombre, url, tipo);
        Evento evento = encontrarEventoPorID(idEvento);
        
        Archivos archivo = new Archivos();
        archivo.setEventoId(evento);
        archivo.setFileevId(archivoCreado.get(0));
        
        archivosFacade.create(archivo);
        
        List<Archivos> archivos = archivosFacade.encontrarArchivoPorEventoYArchivo(evento, archivoCreado.get(0));

        boolean success = true;

        if(archivos.isEmpty()){
            success=false;
        }
        return success;
    }
    

    /* METODOS PARA LO REFERENTE A LAS FECHAS */
    
    @WebMethod(operationName = "encontrarFechaDeEvento") //Devuelve una lista con un solo evento
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public Dateev encontrarFechaDeEvento(@WebParam(name = "idEvento") int idEvento) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByID(idEvento);
        List<Dateev> fecha = new ArrayList<>();
        
        if(!listaEvento.isEmpty()){
            fecha = dateevFacade.encontrarFechaPorID(listaEvento.get(0).getDateevId());
        }
        
        return fecha.get(0);
    }
    
    private List<Dateev> crearFecha(boolean esUnico, Date dia, boolean todosLosDias, Date inicio, Date fin, boolean variosDias, String listaDias) {
        Dateev fecha = new Dateev();
        
        List<Dateev> listaFecha;
        int esUnicoTemp = 0;
        int todosLosDiasTemp = 0;
        int variosDiasTemp = 0;
        
        if(esUnico){
            esUnicoTemp = 1;
            listaFecha = dateevFacade.encontrarFechaPorDia(dia);
            
            if(listaFecha.isEmpty()){
                fecha.setEsunico(esUnicoTemp);
                fecha.setDia(dia);
                fecha.setTodoslosdias(todosLosDiasTemp);
                fecha.setDesde(null);
                fecha.setHasta(null);
                fecha.setVariosdias(variosDiasTemp);
                fecha.setListadias(null);
                
                dateevFacade.create(fecha);
            }
            
            listaFecha = dateevFacade.encontrarFechaPorDia(dia);
        }else if(todosLosDias){
            todosLosDiasTemp = 1;
            listaFecha = dateevFacade.encontrarFechaPorInicioFin(inicio,fin);
            
            if(listaFecha.isEmpty()){
                fecha.setEsunico(esUnicoTemp);
                fecha.setDia(null);
                fecha.setTodoslosdias(todosLosDiasTemp);
                fecha.setDesde(inicio);
                fecha.setHasta(fin);
                fecha.setVariosdias(variosDiasTemp);
                fecha.setListadias(null);
                
                dateevFacade.create(fecha);
            }
            
            listaFecha = dateevFacade.encontrarFechaPorInicioFin(inicio,fin);
        }else{
            variosDiasTemp = 1;
            listaFecha = dateevFacade.encontrarFechaPorListaDias(listaDias);
            
            if(listaFecha.isEmpty()){
                fecha.setEsunico(esUnicoTemp);
                fecha.setDia(null);
                fecha.setTodoslosdias(todosLosDiasTemp);
                fecha.setDesde(null);
                fecha.setHasta(null);
                fecha.setVariosdias(variosDiasTemp);
                fecha.setListadias(listaDias);
                
                dateevFacade.create(fecha);
            }
            
            listaFecha = dateevFacade.encontrarFechaPorListaDias(listaDias);
        }
        
        
        return listaFecha; //con esto consigo que no se creen archivos duplicados. Si el archivo ya está en la BD, lo referencio en vez
                                // de crear uno nuevo
    }
    
    @WebMethod(operationName = "adjuntarFecha") //Cambia el estado del evento al booleano que se le pasa
    public boolean adjuntarFecha(@WebParam(name = "esUnico") boolean esUnico, @WebParam(name = "dia") Date dia, 
            @WebParam(name = "todosLosDias") boolean todosLosDias, @WebParam(name = "inicio") Date inicio, @WebParam(name = "fin") Date fin,
            @WebParam(name = "variosDias") boolean variosDias, @WebParam(name = "listaDias") String listaDias,
            @WebParam(name = "idEvento") int idEvento){
        
        List<Dateev> fechaCreada = crearFecha(esUnico, dia, todosLosDias, inicio, fin, variosDias, listaDias);

        Evento evento = encontrarEventoPorID(idEvento);
        
        evento.setDateevId(fechaCreada.get(0).getId());
        
        eventoFacade.edit(evento);
        
        Evento eventoTemp = encontrarEventoPorID(idEvento);
        boolean success = true;
        
        int idFecha = fechaCreada.get(0).getId();
        if(idFecha != eventoTemp.getDateevId()){
            success=false;
        }
        
        return success;
    }
    
    /* METODOS PARA LO REFERENTE A LOS USUARIOS */
    
    @WebMethod(operationName = "encontrarUsuarioPorID") //Devuelve una lista con un solo evento
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public Usuario encontrarUsuarioPorID(@WebParam(name = "id") int id) {
        List<Usuario> userList = usuarioFacade.encontrarUsuarioPorID(id);
        
        if(userList.isEmpty()){
            return null;
        }else{
            return userList.get(0);
        }
    }
    
    @WebMethod(operationName = "encontrarUsuarioPorEmail") //Devuelve una lista con un solo evento
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public Usuario encontrarUsuarioPorEmail(@WebParam(name = "email") String email) {
        List<Usuario> userList = usuarioFacade.encontrarUsuarioPorEmail(email);
        
        if(userList.isEmpty()){
            return null;
        }else{
            return userList.get(0);
        }
    }
    
    @WebMethod(operationName = "editarUsuario")
    public boolean editarUsuario(@WebParam(name = "idUsuario") int idUsuario, @WebParam(name = "nombre") String nombre, @WebParam(name = "apellidos") String apellidos,
            @WebParam(name = "email") String email, @WebParam(name = "rol") String rol) {
        
        Usuario user = encontrarUsuarioPorID(idUsuario);
        List<Usuario> userEmail = usuarioFacade.encontrarUsuarioPorEmail(email);
        boolean success = true;

        user.setNombre(nombre);
        user.setApellidos(apellidos);
        
        if(userEmail.isEmpty()){
            user.setEmail(email);
        }else{
            success = false;
        }
        
        user.setRol(rol);
        
        if(success){
            usuarioFacade.edit(user);
        
            List<Usuario> userTemp = usuarioFacade.encontrarUsuarioPorEmail(email);
        
        
            if(userTemp.isEmpty()){
                success = false;
            }
        }
        
        return success;
    }

    
    /* METODOS PARA LO REFERENTE A LOS FILTROS */
    
    
    @WebMethod(operationName = "filtrarEventosDeUsuario") //Devuelve una lista con un solo evento
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public List<Evento> filtrarEventosDeUsuario(@WebParam(name = "idUsuario") int idUsuario){
        List<Usuario> user = usuarioFacade.encontrarUsuarioPorID(idUsuario);
        List<Evento> listaEvento = eventoFacade.encontrarEventoByUsuario(user.get(0));
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento;
        }
    }
    
    @WebMethod(operationName = "filtrarEventosPorPrecioMaximo")
    public List<Evento> filtrarEventosPorPrecioMaximo(@WebParam(name = "precioMax") double precioMax) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByPrecioMax(precioMax);
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento;
        }
    }
    
    @WebMethod(operationName = "filtrarEventosNoRevisados")
    public List<Evento> filtrarEventosNoRevisados() {
        List<Evento> listaEvento = eventoFacade.encontrarEventosNoRevisados();
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento;
        }
    }
    
    
    /* METODOS PARA LO REFERENTE A LAS NOTIFICACIONES */
    
    @WebMethod(operationName = "encontrarNotificacionesDeUsuario") //Encuentra SOLO las notificaciones NO leidas
    public List<Notificacion> encontrarNotificacionesDeUsuario(@WebParam(name = "idUsuario") int idUsuario) {
        Usuario user = encontrarUsuarioPorID(idUsuario);
        
        return notificacionFacade.encontrarNotificacionesDeUsuario(user);
    }
    
    @WebMethod(operationName = "encontrarTodasLasNotificacionesDeUsuario") //Encuentra TODAS las notificaciones
    public List<Notificacion> encontrarTodasLasNotificacionesDeUsuario(@WebParam(name = "idUsuario") int idUsuario) {
        Usuario user = encontrarUsuarioPorID(idUsuario);
        
        return notificacionFacade.encontrarTodasLasNotificacionesDeUsuario(user);
    }
    
    @WebMethod(operationName = "enviarNotificacion")
    public boolean enviarNotificacion(@WebParam(name = "descripcion") String descripcion, @WebParam(name = "idUsuario") int idUsuario) {
        Usuario user = encontrarUsuarioPorID(idUsuario);
        int size1 = encontrarNotificacionesDeUsuario(idUsuario).size();
        boolean success = false;
        
        Notificacion not = new Notificacion();
        not.setDescripcion(descripcion);
        not.setLeida(0);
        not.setUsuarioId(user);
        
        notificacionFacade.create(not);
        
        if(encontrarNotificacionesDeUsuario(idUsuario).size() == size1+1){
            success = true;
        }
        
        return success;
    }
    
    @WebMethod(operationName = "marcarNotificacionComoLeida")
    public boolean marcarNotificacionComoLeida(@WebParam(name = "idNotificacion") int idNotificacion) {
        Notificacion not = notificacionFacade.encontrarNotificacionByID(idNotificacion).get(0);
        boolean success = false;
        
        not.setLeida(1);
        
        notificacionFacade.edit(not);
        
        if(notificacionFacade.encontrarNotificacionByID(idNotificacion).get(0).getLeida() == 1){
            success = true;
        }
        
        return success;
    } 
    
    /* METODOS PARA LO REFERENTE A LAS NOTIFICACIONES */
    
        private List<Tag> crearTag(String nombre) {
        Tag tag = new Tag();
        
        List<Tag> listaTag = tagFacade.encontrarTagPorNombre(nombre);  //supongo que el nombre es unica por archivo
        
        if(listaTag.isEmpty()){
            tag.setNombre(nombre);

            tagFacade.create(tag);
            
            listaTag = tagFacade.encontrarTagPorNombre(nombre);
        }
        
        return listaTag; //con esto consigo que no se creen archivos duplicados. Si el archivo ya está en la BD, lo referencio en vez
                                // de crear uno nuevo
    }
    
    
    
    @WebMethod(operationName = "adjuntarTagEv") //Cambia el estado del evento al booleano que se le pasa
    public void adjuntarTagEv(@WebParam(name = "listaTags") String listaTags, @WebParam(name = "idEvento") int idEvento){
        String[] partes = listaTags.split(",");
        List<Tag> tagCreado;
        String sinEspacio;
        
        Evento evento = encontrarEventoPorID(idEvento);
        
        Tagevento tagEv = new Tagevento();
        tagEv.setEventoId(evento);
        
        for(int i=0;i<partes.length; i++){
            sinEspacio = partes[i].trim().toLowerCase();
            tagCreado = crearTag(sinEspacio);
            
            tagEv.setTagId(tagCreado.get(0));
            
            tagEventoFacade.create(tagEv);
        }  
    }
    
    @WebMethod(operationName = "adjuntarTagUser") //Cambia el estado del evento al booleano que se le pasa
    public void adjuntarTagUser(@WebParam(name = "listaTags") String listaTags, @WebParam(name = "idUsuario") int idUsuario){
        String[] partes = listaTags.split(",");
        List<Tag> tagCreado;
        String sinEspacio;
        
        Usuario usuario = encontrarUsuarioPorID(idUsuario);
        
        Tagusuario tagUser = new Tagusuario();
        tagUser.setUsuarioId(usuario);
        
        for(int i=0;i<partes.length; i++){
            sinEspacio = partes[i].trim().toLowerCase();
            tagCreado = crearTag(sinEspacio);
            
            tagUser.setTagId(tagCreado.get(0));
            
            tagUsuarioFacade.create(tagUser);
        } 
    }
}
