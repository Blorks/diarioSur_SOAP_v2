/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

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
public class FileevFacade extends AbstractFacade<Fileev> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FileevFacade() {
        super(Fileev.class);
    }
    
    public List<Fileev> encontrarArchivoPorURL(String url) {
        Query q; 
        
        q = em.createQuery("select a from Fileev a where a.url like :url");
        q.setParameter("url", url);
        return q.getResultList();
    }
    
}
