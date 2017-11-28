/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import entity.Evento;
import entity.Notificacion;
import entity.Usuario;
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
public class NotificacionFacade extends AbstractFacade<Notificacion> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NotificacionFacade() {
        super(Notificacion.class);
    }
    
    public List<Notificacion> encontrarNotificacionesDeUsuario(Usuario user) {
        Query q; 
        
        int leida = 0;
        
        q = em.createQuery("select n from Notificacion n where n.usuarioId = :user AND n.leida = :leida");
        q.setParameter("user",  user);
        q.setParameter("leida",  leida);
        return q.getResultList();
    }
    
    public List<Notificacion> encontrarTodasLasNotificacionesDeUsuario(Usuario user) {
        Query q; 
        
        q = em.createQuery("select n from Notificacion n where n.usuarioId = :user");
        q.setParameter("user",  user);
        return q.getResultList();
    }
    
    public List<Notificacion> encontrarNotificacionByID(int id) {
        Query q; 
        
        q = em.createQuery("select n from Notificacion n where n.id = :id");
        q.setParameter("id",  id);
        return q.getResultList();
    }
    
}
