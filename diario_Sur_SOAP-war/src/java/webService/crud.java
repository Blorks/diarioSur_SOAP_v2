/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webService;

import entity.Evento;
import facade.EventoFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.Oneway;
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
    private EventoFacade eventoFacade;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "encontrarEventoPorID") //Devuelve una lista con un solo evento
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public Evento encontrarEventoPorID(@WebParam(name = "id") int id) {
        List<Evento> listaEvento = eventoFacade.encontrarEventoByID(id);
        return listaEvento.get(0);
    }
    
    @WebMethod(operationName = "encontrarEventos") //Devuelve una lista con todos los eventos
    //Cuidado con el "estaRevisado". En la BD se guarda como un numero, no como un bool, así que al recogerlo habrá que hacer el cambio
    public List<Evento> encontrarEventos() {
        return eventoFacade.findAll();
    }
    
    @WebMethod(operationName = "crearEvento")
    // ATRIBUTOS
    // descripcion -> String con la descripción del evento (maximo 4000 caracteres)
    // direccionFisica -> String con la dirección del evento (maximo 4000 caracteres)
    // precio -> el atributo es DOUBLE, no float
    // estaRevisado -> es un booleano, osea que espero true o false, pero en la BD se guarda como numero
    // idUsuario -> la id del usuario que ha creado el evento
    
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
        evento.setUsuarioId(idUsuario);
        
        eventoFacade.create(evento);
       
        List<Evento> eventos = eventoFacade.encontrarEventoPorDescripcionYPrecio(descripcion, precio);
        
        boolean success = true;
        
        if(eventos.isEmpty()){
            success = false;
        }
        return success;
    }
    
    @WebMethod(operationName = "actualizarEvento")
    // ATRIBUTOS
    // idEvento -> solo se usa para buscar el evento a actualizar. No se puede modificar
    
    // descripcion -> String con la descripción del evento (maximo 4000 caracteres)
    // direccionFisica -> String con la dirección del evento (maximo 4000 caracteres)
    // precio -> el atributo es DOUBLE, no float
    // estaRevisado -> es un booleano, osea que espero true o false, pero en la BD se guarda como numero
  
    // la funcion devuelve true si el evento se ha actualizado correctamente, false si no
    public boolean actualizarEvento(@WebParam(name = "idEvento") int idEvento, @WebParam(name = "descripcion") String descripcion,@WebParam(name = "direccionFisica") String direccionFisica,
                @WebParam(name = "precio") double precio ,@WebParam(name = "estaRevisado") boolean estaRevisado) {
        
        Evento evento = encontrarEventoPorID(idEvento);
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
    
    @WebMethod(operationName = "eliminarEventoPorID") //Si no consigue borrar el evento, devuelve false
    public boolean eliminarEventoPorID(@WebParam(name = "id") int id) {
        Evento evento = encontrarEventoPorID(id);
        
        eventoFacade.remove(evento);
        
        
        boolean success = true;
        
        //if(!eventos.isEmpty()){
            //success = false;
        //}
        return success;
    }
    
}
