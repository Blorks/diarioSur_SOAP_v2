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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @WebMethod(operationName = "encontrarEventoPorID")
    public Evento encontrarEventoPorID(@WebParam(name = "id") int id) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByID(id);
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento.get(0);
        }
    }
    
    @WebMethod(operationName = "encontrarTodosLosEventosRevisados")
    public List<Evento> encontrarTodosLosEventosRevisados() {
        return eventoFacade.encontrarEventosRevisados();
    }
    
    @WebMethod(operationName = "encontrarTodosLosEventos")
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
    
    @WebMethod(operationName = "revisarEvento")
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
    
    @WebMethod(operationName = "eliminarEvento")
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
            List<Tagusuario> tagUsuario = tagUsuarioFacade.encontrarTagUserPorID(listaTag.get(i).getTagId().getId());
            if(te.isEmpty() && tagUsuario.isEmpty()){
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

    @WebMethod(operationName = "encontrarArchivosDeEvento")
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
        
        List<Fileev> listaArchivo = fileevFacade.encontrarArchivoPorURL(url);
        
        if(listaArchivo.isEmpty()){
            archivo.setNombre(nombre);
            archivo.setUrl(url);
            archivo.setTipo(tipo);

            fileevFacade.create(archivo);
            
            listaArchivo = fileevFacade.encontrarArchivoPorURL(url);
        }
        
        return listaArchivo;
    }
    
    
    
    @WebMethod(operationName = "adjuntarArchivo")
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
    
    @WebMethod(operationName = "encontrarFechaDeEvento")
    public Dateev encontrarFechaDeEvento(@WebParam(name = "idEvento") int idEvento) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByID(idEvento);
        List<Dateev> fecha = new ArrayList<>();
        
        if(!listaEvento.isEmpty()){
            fecha = dateevFacade.encontrarFechaPorID(listaEvento.get(0).getDateevId());
        }
        
        return fecha.get(0);
    }
    
    private List<Dateev> crearFecha(boolean esUnico, String dia, boolean todosLosDias, String inicio, String fin, boolean variosDias, String listaDias) {
        Dateev fecha = new Dateev();
        
        List<Dateev> listaFecha;
        int esUnicoTemp = 0;
        int todosLosDiasTemp = 0;
        int variosDiasTemp = 0;
        
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        if(esUnico){
            esUnicoTemp = 1;
            
            Date unicoDia = new Date();
        
            try {
                unicoDia = formato.parse(dia);
            } catch (ParseException ex) {
                Logger.getLogger(crud.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            listaFecha = dateevFacade.encontrarFechaPorDia(unicoDia);
            
            if(listaFecha.isEmpty()){
                fecha.setEsunico(esUnicoTemp);
                fecha.setDia(unicoDia);
                fecha.setTodoslosdias(todosLosDiasTemp);
                fecha.setDesde(null);
                fecha.setHasta(null);
                fecha.setVariosdias(variosDiasTemp);
                fecha.setListadias(null);
                
                dateevFacade.create(fecha);
            }
            
            listaFecha = dateevFacade.encontrarFechaPorDia(unicoDia);
        }else if(todosLosDias){
            todosLosDiasTemp = 1;
            
            Date desde = new Date();
            Date hasta = new Date();
        
            try {
                desde = formato.parse(inicio);
                hasta = formato.parse(fin);
            } catch (ParseException ex) {
                Logger.getLogger(crud.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            listaFecha = dateevFacade.encontrarFechaPorInicioFin(desde,hasta);
            
            if(listaFecha.isEmpty()){
                fecha.setEsunico(esUnicoTemp);
                fecha.setDia(null);
                fecha.setTodoslosdias(todosLosDiasTemp);
                fecha.setDesde(desde);
                fecha.setHasta(hasta);
                fecha.setVariosdias(variosDiasTemp);
                fecha.setListadias(null);
                
                dateevFacade.create(fecha);
            }
            
            listaFecha = dateevFacade.encontrarFechaPorInicioFin(desde,hasta);
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
        
        
        return listaFecha;
    }
    
    @WebMethod(operationName = "adjuntarFecha")
    public boolean adjuntarFecha(@WebParam(name = "esUnico") boolean esUnico, @WebParam(name = "dia") String dia, 
            @WebParam(name = "todosLosDias") boolean todosLosDias, @WebParam(name = "inicio") String inicio, @WebParam(name = "fin") String fin,
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
    
    @WebMethod(operationName = "encontrarUsuarioPorID")
    public Usuario encontrarUsuarioPorID(@WebParam(name = "id") int id) {
        List<Usuario> userList = usuarioFacade.encontrarUsuarioPorID(id);
        
        if(userList.isEmpty()){
            return null;
        }else{
            return userList.get(0);
        }
    }
    
    @WebMethod(operationName = "encontrarUsuarioPorEmail")
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
    
    
    @WebMethod(operationName = "filtrarEventosDeUsuario")
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
    
    @WebMethod(operationName = "encontrarNotificacionesDeUsuario")
    public List<Notificacion> encontrarNotificacionesDeUsuario(@WebParam(name = "idUsuario") int idUsuario) {
        Usuario user = encontrarUsuarioPorID(idUsuario);
        
        return notificacionFacade.encontrarNotificacionesDeUsuario(user);
    }
    
    @WebMethod(operationName = "encontrarTodasLasNotificacionesDeUsuario")
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
        
        List<Tag> listaTag = tagFacade.encontrarTagPorNombre(nombre);
        
        if(listaTag.isEmpty()){
            tag.setNombre(nombre);

            tagFacade.create(tag);
            
            listaTag = tagFacade.encontrarTagPorNombre(nombre);
        }
        
        return listaTag;
    }
    
    
    
    @WebMethod(operationName = "adjuntarTagEv")
    public boolean adjuntarTagEv(@WebParam(name = "listaTags") String listaTags, @WebParam(name = "idEvento") int idEvento){
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
        return true;
    }
    
    @WebMethod(operationName = "adjuntarTagUser")
    public boolean adjuntarTagUser(@WebParam(name = "listaTags") String listaTags, @WebParam(name = "idUsuario") int idUsuario){
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
        
        return true;
    }
    
    @WebMethod(operationName = "encontrarTagsDeEvento")
    public List<Tag> encontrarTagsDeEvento(@WebParam(name = "idEvento") int idEvento){
        Evento ev = encontrarEventoPorID(idEvento);
        List<Tagevento> listaTagEv = tagEventoFacade.encontrarTagEv(ev);
        List<Tag> listaTag = new ArrayList<>();
        
        for(int i=0; i<listaTagEv.size(); i++){
            listaTag.add(listaTagEv.get(i).getTagId());
        }


        return listaTag;
    }
    
    @WebMethod(operationName = "encontrarTagsDeUsuario")
    public List<Tag> encontrarTagsDeUsuario(@WebParam(name = "idUsuario") int idUsuario){
        Usuario user = encontrarUsuarioPorID(idUsuario);
        List<Tagusuario> listaTagEv = tagUsuarioFacade.encontrarTagUser(user);
        List<Tag> listaTag = new ArrayList<>();
        
        for(int i=0; i<listaTagEv.size(); i++){
            listaTag.add(listaTagEv.get(i).getTagId());
        }


        return listaTag;
    }
    
    @WebMethod(operationName = "eliminarTagEv")
    public boolean eliminarTagEv(@WebParam(name = "nombre") String nombre, @WebParam(name = "idEvento") int idEvento){
        List<Tag> tag = tagFacade.encontrarTagPorNombre(nombre);
        Evento ev = encontrarEventoPorID(idEvento);
        boolean success = true;
        
        List<Tagevento> tagEv = tagEventoFacade.encontrarTagEvPorTagyEvento(tag.get(0), ev);
        
        if(!tagEv.isEmpty()){
            tagEventoFacade.remove(tagEv.get(0));
            
            List<Tagevento>tagEvento = tagEventoFacade.encontrarTagEv(ev);
            List<Tagusuario> tagUsuario = tagUsuarioFacade.encontrarTagUserPorID(tagEv.get(0).getId());
            
            if(tagEvento.isEmpty() && tagUsuario.isEmpty()){
                tagFacade.remove(tag.get(0));
                success = false;
            }
        }
        
        return success;
    }
    
    @WebMethod(operationName = "eliminarTagUser")
    public boolean eliminarTagUser(@WebParam(name = "nombre") String nombre, @WebParam(name = "idUsuario") int idUsuario){
        List<Tag> tag = tagFacade.encontrarTagPorNombre(nombre);
        Usuario user = encontrarUsuarioPorID(idUsuario);
        boolean success = true;
        
        List<Tagusuario> tagUser = tagUsuarioFacade.encontrarTagUserPorTagyUsuario(tag.get(0), user);
        
        if(!tagUser.isEmpty()){
            tagUsuarioFacade.remove(tagUser.get(0));
            
            List<Tagusuario> tagUsuario = tagUsuarioFacade.encontrarTagUser(user);
            List<Tagevento> tagEv = tagEventoFacade.encontrarTagEvPorID(tag.get(0).getId());
            
            if(tagUsuario.isEmpty() && tagEv.isEmpty()){
                tagFacade.remove(tag.get(0));
                success = false;
            }
        }
        return success;
    }
}
