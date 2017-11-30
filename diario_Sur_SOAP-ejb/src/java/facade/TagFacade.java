/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import entity.Evento;
import entity.Tag;
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
public class TagFacade extends AbstractFacade<Tag> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TagFacade() {
        super(Tag.class);
    }
    
    public List<Tag> encontrarTagPorNombre(String nombre) {
        Query q; 
        
        q = em.createQuery("select t from Tag t where t.nombre = :nombre");
        q.setParameter("nombre",  nombre);
        return q.getResultList();
    }
    
}
