/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webService;

import entity.Dateev;
import entity.Evento;
import entity.Fileev;
import facade.DateevFacade;
import facade.EventoFacade;
import facade.FileevFacade;
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
    
    /* METODOS PARA LO REFERENTE A LOS EVENTOS */

    @WebMethod(operationName = "encontrarEventoPorID") //Devuelve una lista con un solo evento
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public List<Evento> encontrarEventoPorID(@WebParam(name = "id") int id) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByID(id);
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento;
        }
    }
    
    @WebMethod(operationName = "listarTodosLosEventos") //Devuelve una lista con todos los eventos
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public List<Evento> listarTodosLosEventos() {
        return eventoFacade.findAll();
    }
    
    @WebMethod(operationName = "crearEvento")
    // ATRIBUTOS
    // descripcion -> String con la descripción del evento (maximo 4000 caracteres)
    // direccionFisica -> String con la dirección del evento (maximo 4000 caracteres)
    // precio -> el atributo es DOUBLE, no float
    // estaRevisado -> es un booleano, osea que espero true o false, pero en la BD se guarda como numero
    // idUsuario -> la id del usuario que ha creado el evento
    
    // DateevId y FileevId se inicializan a 0 para evitar problemas con el null, ya que si son necesarios, se usará otra función para añadirlos
    
    
    // la funcion devuelve true si el evento se ha creado correctamente, false si no
    public boolean crearEvento(@WebParam(name = "descripcion") String descripcion,@WebParam(name = "direccionFisica") String direccionFisica,
                @WebParam(name = "precio") double precio ,@WebParam(name = "estaRevisado") boolean estaRevisado,@WebParam(name = "idUsuario") int idUsuario) {
        
        
        Evento evento = new Evento();
        int revisado=0;
        if(estaRevisado){
            revisado=1;
        }
        
        evento.setDescripcion(descripcion);
        evento.setDireccionfisica(direccionFisica);
        evento.setPrecio(precio);
        evento.setEstarevisado(revisado);
        evento.setDateevId(0);
        evento.setFileevId(0);
        evento.setUsuarioId(idUsuario);
        
        eventoFacade.create(evento);
       
        List<Evento> eventos = eventoFacade.encontrarEventoPorDescripcionYPrecio(descripcion, precio);
        
        boolean success = true;
        
        if(eventos.isEmpty()){
            success = false;
        }
        return success;
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
    public boolean editarEvento(@WebParam(name = "idEvento") int idEvento, @WebParam(name = "descripcion") String descripcion,@WebParam(name = "direccionFisica") String direccionFisica,
                @WebParam(name = "precio") double precio ,@WebParam(name = "estaRevisado") boolean estaRevisado) {
        
        Evento evento = encontrarEventoPorID(idEvento).get(0);
        int revisado=0;
        if(estaRevisado){
            revisado=1;
        }

        evento.setDescripcion(descripcion);
        evento.setDireccionfisica(direccionFisica);
        evento.setPrecio(precio);
        evento.setEstarevisado(revisado);
        
        eventoFacade.edit(evento);
        
        List<Evento> eventos = eventoFacade.encontrarEventoPorDescripcionYPrecio(descripcion, precio);
        boolean success = true;
        
        if(eventos.isEmpty()){
            success = false;
        }
        return success;
    }
    
    @WebMethod(operationName = "revisarEvento") //Cambia el estado del evento al booleano que se le pasa
    public boolean revisarEvento(@WebParam(name = "id") int id, @WebParam(name = "estaRevisado") boolean estaRevisado) {
        Evento evento = encontrarEventoPorID(id).get(0);
        
        int revisado=0;
        if(estaRevisado){
            revisado=1;
        }
        
        evento.setEstarevisado(revisado);
        eventoFacade.edit(evento);
        
        Evento eventoTemp = encontrarEventoPorID(id).get(0);
        
        boolean success = true;
        
        if(eventoTemp.getEstarevisado() != revisado){
            success = false;
        }
        return success;
    }
    
    @WebMethod(operationName = "eliminarEvento(NO_FUNCIONA)") //Si no consigue borrar el evento, devuelve false
    public boolean eliminarEvento(@WebParam(name = "id") int id) {
        //Evento evento = encontrarEventoPorID(id);

        //Evento eventoTemp = encontrarEventoPorID(id);

        boolean success = true;
        
        //if(eventoTemp == null){
            //success = false;
        //}
        return success;
    }
    
    /* METODOS PARA LO REFERENTE A LOS ARCHIVOS */
    
    
    private List<Fileev> crearArchivo(String nombre, String url, String tipo, int idEvento) {
        Fileev archivo = new Fileev();
        
        List<Fileev> listaArchivo = fileevFacade.encontrarArchivoPorURL(url);  //supongo que la url es unica por archivo
        
        if(listaArchivo.isEmpty()){
            archivo.setNombre(nombre);
            archivo.setUrl(url);
            archivo.setTipo(tipo);
            archivo.setEventoId(idEvento);
        
            fileevFacade.create(archivo);
            
            listaArchivo = fileevFacade.encontrarArchivoPorURL(url);
        }
        
        return listaArchivo; //con esto consigo que no se creen archivos duplicados. Si el archivo ya está en la BD, lo referencio en vez
                                // de crear uno nuevo
    }
    
    
    @WebMethod(operationName = "adjuntarArchivo") //Cambia el estado del evento al booleano que se le pasa
    public boolean adjuntarArchivo(@WebParam(name = "nombre") String nombre, @WebParam(name = "URL") String url, @WebParam(name = "tipo") String tipo,
            @WebParam(name = "idEvento") int idEvento){
        
        List<Fileev> archivoCreado = crearArchivo(nombre, url, tipo, idEvento);

        Evento evento = encontrarEventoPorID(idEvento).get(0);
        
        evento.setFileevId(archivoCreado.get(0).getId());
        
        eventoFacade.edit(evento);
        
        Evento eventoTemp = encontrarEventoPorID(idEvento).get(0);
        boolean success = true;
        
        int idArchivo = archivoCreado.get(0).getId();
        if(idArchivo != eventoTemp.getFileevId()){
            success=false;
        }
        
        return success;
    }
    
    
    
    
    /* METODOS PARA LO REFERENTE A LAS FECHAS */
    
    
    
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

        Evento evento = encontrarEventoPorID(idEvento).get(0);
        
        evento.setDateevId(fechaCreada.get(0).getId());
        
        eventoFacade.edit(evento);
        
        Evento eventoTemp = encontrarEventoPorID(idEvento).get(0);
        boolean success = true;
        
        int idFecha = fechaCreada.get(0).getId();
        if(idFecha != eventoTemp.getDateevId()){
            success=false;
        }
        
        return success;
    }
    
    /* METODOS PARA LO REFERENTE A LOS USUARIOS */
    
    @WebMethod(operationName = "filtrarEventosDeUsuario") //Devuelve una lista con un solo evento
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public List<Evento> filtrarEventosDeUsuario(@WebParam(name = "id") int id) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByUsuario(id);
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento;
        }
    }
    
    public List<Evento> filtrarEventosPorPrecioMaximo(@WebParam(name = "precioMax") double precioMax) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByPrecioMax(precioMax);
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento;
        }
    }
    
    public List<Evento> filtrarEventosNoRevisados() {
        List<Evento> listaEvento = eventoFacade.encontrarEventosNoRevisados();
        
        if(listaEvento.isEmpty()){
            return null;
        }else{
            return listaEvento;
        }
    }
}
