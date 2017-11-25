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
    
    public List<Evento> encontrarEventoByID(int id) {
        Query q; 
        
        q = em.createQuery("select e from Evento e where e.id = :id");
        q.setParameter("id",  id);
        return q.getResultList();
    }
    
    public List<Evento> encontrarEventoByUsuario(int id) {
        Query q; 
        
        q = em.createQuery("select e from Evento e where e.usuarioId = :id");
        q.setParameter("id",  id);
        return q.getResultList();
    }
    
    public List<Evento> encontrarEventoByPrecioMax(double precioMax) {
        Query q; 
        
        q = em.createQuery("select e from Evento e where e.precio <= :precioMax");
        q.setParameter("precioMax", precioMax);
        return q.getResultList();
    }
    
    public List<Evento> encontrarEventosNoRevisados() {
        Query q;
        int estaRevisadoTemp = 0;
        
        q = em.createQuery("select e from Evento e where e.estarevisado = :estaRevisadoTemp");
        q.setParameter("estaRevisadoTemp", estaRevisadoTemp);
        return q.getResultList();
    }
    
    public List<Evento> encontrarEventoPorDescripcionYPrecio(String descripcion, double precio) {
        Query q; 
        
        q = em.createQuery("select e from Evento e where e.descripcion like :descripcion AND e.precio = :precio");
        q.setParameter("descripcion", descripcion);
        q.setParameter("precio", precio);
        return q.getResultList();
    }

    public void eliminarEventoPorID(int id) {
        Query q; 
        
        q = em.createQuery("DELETE FROM Evento e where e.id = :id");
        q.setParameter("id",  id);
    }
    
}
