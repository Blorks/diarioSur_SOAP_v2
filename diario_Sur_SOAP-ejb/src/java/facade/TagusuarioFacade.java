/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import entity.Evento;
import entity.Tag;
import entity.Tagevento;
import entity.Tagusuario;
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
public class TagusuarioFacade extends AbstractFacade<Tagusuario> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TagusuarioFacade() {
        super(Tagusuario.class);
    }
    
        public List<Tagusuario> encontrarTagUserPorID(int id){
        Query q;
        
        q = em.createQuery("select t from Tagusuario t where t.id = :id");
        q.setParameter("id",  id);
        return q.getResultList();
    }
    
    public List<Tagusuario> encontrarTagUser(Usuario user){
        Query q;
        
        q = em.createQuery("select t from Tagusuario t where t.usuarioId = :user");
        q.setParameter("user",  user);
        return q.getResultList();
    }
    
        public List<Tagusuario> encontrarTagUserPorTagyUsuario(Tag tag, Usuario user){
        Query q;
        
        q = em.createQuery("select t from Tagusuario t where t.tagId = :tag AND t.usuarioId = :user");
        q.setParameter("tag",  tag);
        q.setParameter("user",  user);
        return q.getResultList();
    }
    
}
