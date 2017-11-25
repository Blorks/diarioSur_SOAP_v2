/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

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
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    public List<Usuario> encontrarUsuarioPorID(int id) {
        Query q; 
        
        q = em.createQuery("select u from Usuario u where u.id = :id");
        q.setParameter("id",  id);
        return q.getResultList();
    }
    
    public List<Usuario> encontrarUsuarioPorEmail(String email) {
        Query q; 
        
        q = em.createQuery("select u from Usuario u where u.email like :email");
        q.setParameter("email",  email);
        return q.getResultList();
    }
    
    public void eliminarUsuarioPorID(int id) {
        Query q; 
        
        q = em.createQuery("DELETE FROM Usuario u where u.id = :id");
        q.setParameter("id",  id);
    }
    
}
