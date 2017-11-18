/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import entity.Evento;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Dani
 */
@Stateless
public class EventoFacade extends AbstractFacade<Evento> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoFacade() {
        super(Evento.class);
    }
    
    public List<Evento> encontrarEventoPorID(int id) {
        Query q; 
        
        q = em.createQuery("select e from Evento e where e.id = :id");
        q.setParameter("id",  id);
        return q.getResultList();
    }
    
    public List<Evento> encontrarEventoPorDescripcionYPrecio(String descripcion, double precio) {
        Query q; 
        
        q = em.createQuery("select e from Evento e where e.descripcion like :descripcion AND e.precio = :precio");
        q.setParameter("descripcion", descripcion);
        q.setParameter("precio", precio);
        return q.getResultList();
    }
    
    public void crearEvento(String descripcion, String direccionFisica, double precio, boolean estaRevisado, int idUsuario) {
        Query q; 
        
        q = em.createQuery("INSERT INTO Evento e VALUES (:descripcion, :direccionFisica, :precio, :estaRevisado, :idUsuario)");
        q.setParameter("descripcion", descripcion);
        q.setParameter("direccionFisica", direccionFisica);
        q.setParameter("precio", precio);
        q.setParameter("estaRevisado", estaRevisado);
        q.setParameter("idUsuario",  idUsuario);
    }
    
    public void actualizarEvento(int idEvento, String descripcion, String direccionFisica, double precio, boolean estaRevisado, int idUsuario) {
        Query q; 
        
        q = em.createQuery("UPDATE Evento e SET e.descripcion = :descripcion, e.direccionFisica = :direccionFisica, e.precio = :precio, e.estaRevisado = :estaRevisado, e.idUsuario = :idUsuario WHERE e.id = :idEvento");
        q.setParameter("idEvento", idEvento);
        q.setParameter("descripcion", descripcion);
        q.setParameter("direccionFisica", direccionFisica);
        q.setParameter("precio", precio);
        q.setParameter("estaRevisado", estaRevisado);
        q.setParameter("idUsuario",  idUsuario);
    }
    
    public void eliminarEventoPorID(int id) {
        Query q; 
        
        q = em.createQuery("DELETE FROM Evento e where e.id = :id");
        q.setParameter("id",  id);
    }
    
}
