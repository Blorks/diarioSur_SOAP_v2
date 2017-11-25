/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import entity.Dateev;
import java.util.Date;
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
public class DateevFacade extends AbstractFacade<Dateev> {

    @PersistenceContext(unitName = "diario_Sur_SOAP-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DateevFacade() {
        super(Dateev.class);
    }
    
    public List<Dateev> encontrarFechaPorDia(Date dia) {
        Query q; 
        
        q = em.createQuery("select f from Dateev f where f.dia = :dia");
        q.setParameter("dia", dia);
        return q.getResultList();
    }
    
    public List<Dateev> encontrarFechaPorInicioFin(Date inicio, Date fin) {
        Query q; 
        
        q = em.createQuery("select f from Dateev f where f.desde = :inicio AND f.hasta = :fin");
        q.setParameter("inicio", inicio);
        q.setParameter("fin", fin);
        return q.getResultList();
    }
        
    public List<Dateev> encontrarFechaPorListaDias(String listaDias) {
        Query q; 
        
        q = em.createQuery("select f from Dateev f where f.listadias like :listaDias");
        q.setParameter("listaDias", listaDias);
        return q.getResultList();
    }
    
}
