/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import entity.Archivos;
import entity.Evento;
import entity.Fileev;
import entity.Tag;
import entity.Tagevento;
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
public class TageventoFacade extends AbstractFacade<Tagevento> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TageventoFacade() {
        super(Tagevento.class);
    }
    
    public List<Tagevento> encontrarTagEvPorID(int id){
        Query q;
        
        q = em.createQuery("select t from Tagevento t where t.tagId.id = :id");
        q.setParameter("id",  id);
        return q.getResultList();
    }

    
    public List<Tagevento> encontrarTagEvPorTagyEvento(Tag tag, Evento ev){
        Query q;
        
        q = em.createQuery("select t from Tagevento t where t.tagId = :tag AND t.eventoId = :ev");
        q.setParameter("tag",  tag);
        q.setParameter("ev",  ev);
        return q.getResultList();
    }
    
    public List<Tagevento> encontrarTagEv(Evento ev){
        Query q;
        
        q = em.createQuery("select t from Tagevento t where t.eventoId = :ev");
        q.setParameter("ev",  ev);
        return q.getResultList();
    }
    
    public List<Tagevento> encontrarTagEvPorTag(Tag tag) {
        Query q;
        
        q = em.createQuery("select t from Tagevento t where t.tagId = :tag");
        q.setParameter("tag",  tag);
        return q.getResultList();
    }
    
}
