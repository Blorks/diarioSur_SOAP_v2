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
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Dani
 */
@WebService(serviceName = "crud")
@Stateless()
public class crud {

    @EJB
    private EventoFacade eventoFacade;
    
    @WebMethod(operationName = "encontrarEventoPorID") //Devuelve una lista con un solo evento
    public List<Evento> encontrarEventoPorID(@WebParam(name = "id") int id) {
        return eventoFacade.encontrarEventoPorID(id);
    }
    
    @WebMethod(operationName = "encontrarEventos") //Devuelve una lista con todos los eventos
    public List<Evento> encontrarEventos() {
        return eventoFacade.findAll();
    }
    
    @WebMethod(operationName = "crearEvento")
    // ATRIBUTOS
    // descripcion -> String con la descripción del evento (maximo 4000 caracteres)
    // direccionFisica -> String con la dirección del evento (maximo 4000 caracteres)
    // precio -> el atributo es DOUBLE, no float
    // estaRevisado -> es un booleano, osea que espero true o false, no un numero
    // idUsuario -> la id del usuario que ha creado el evento
    
    // la funcion devuelve true si el evento se ha creado correctamente, false si no
    public boolean crearEvento(@WebParam(name = "descripcion") String descripcion,@WebParam(name = "direccionFisica") String direccionFisica,
                @WebParam(name = "precio") double precio ,@WebParam(name = "estaRevisado") boolean estaRevisado,@WebParam(name = "idUsuario") int idUsuario) {
        eventoFacade.crearEvento(descripcion, direccionFisica, precio, estaRevisado, idUsuario);
        
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
    // estaRevisado -> es un booleano, osea que espero true o false, no un numero
    // idUsuario -> la id del usuario que ha creado el evento
    
    // la funcion devuelve true si el evento se ha actualizado correctamente, false si no
    public boolean actualizarEvento(@WebParam(name = "idEvento") int idEvento, @WebParam(name = "descripcion") String descripcion,@WebParam(name = "direccionFisica") String direccionFisica,
                @WebParam(name = "precio") double precio ,@WebParam(name = "estaRevisado") boolean estaRevisado,@WebParam(name = "idUsuario") int idUsuario) {
        eventoFacade.actualizarEvento(idEvento, descripcion, direccionFisica, precio, estaRevisado, idUsuario);
        
        List<Evento> eventos = eventoFacade.encontrarEventoPorDescripcionYPrecio(descripcion, precio);
        
        boolean success = true;
        
        if(eventos.isEmpty()){
            success = false;
        }
        return success;
    }
    
    @WebMethod(operationName = "eliminarEventoPorID") //Si no consigue borrar el evento, devuelve false
    public boolean eliminarEventoPorID(@WebParam(name = "id") int id) {
        eventoFacade.eliminarEventoPorID(id);
        
        List<Evento> eventos = eventoFacade.encontrarEventoPorID(id);
        
        boolean success = true;
        
        if(!eventos.isEmpty()){
            success = false;
        }
        return success;
    }
}
