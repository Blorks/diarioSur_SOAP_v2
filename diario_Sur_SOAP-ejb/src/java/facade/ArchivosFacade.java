/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import entity.Archivos;
import entity.Evento;
import entity.Fileev;
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
public class ArchivosFacade extends AbstractFacade<Archivos> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArchivosFacade() {
        super(Archivos.class);
    }
    
    public List<Archivos> encontrarArchivoPorID(int id) {
        Query q;
        
        q = em.createQuery("select a from Archivos a where a.id = :id");
        q.setParameter("id",  id);
        return q.getResultList();
    }
    
    public List<Archivos> encontrarArchivoPorEventoYArchivo(Evento ev, Fileev ar) {
        Query q;
        
        q = em.createQuery("select a from Archivos a where a.eventoId = :ev AND a.fileevId = :ar");
        q.setParameter("ev",  ev);
        q.setParameter("ar",  ar);
        return q.getResultList();
    }
    
    public List<Archivos> encontrarArchivoPorEvento(Evento ev) {
        Query q;
        
        q = em.createQuery("select a from Archivos a where a.eventoId = :ev");
        q.setParameter("ev",  ev);
        return q.getResultList();
    }
    
    public List<Archivos> encontrarArchivoPorFile(Fileev file) {
        Query q;
        
        q = em.createQuery("select a from Archivos a where a.fileevId = :file");
        q.setParameter("file",  file);
        return q.getResultList();
    }
    
}
