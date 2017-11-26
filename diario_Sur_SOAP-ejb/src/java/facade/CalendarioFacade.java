/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import entity.Calendario;
import entity.Evento;
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
public class CalendarioFacade extends AbstractFacade<Calendario> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CalendarioFacade() {
        super(Calendario.class);
    }
    
    public List<Calendario> encontrarCalendarioPorEvento(Evento ev) {
        Query q; 

        q = em.createQuery("select c from Calendario c where c.eventoId = :ev");
        q.setParameter("ev",  ev);
        return q.getResultList();
    }
}
