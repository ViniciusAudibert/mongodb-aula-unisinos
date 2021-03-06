/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.farmacia.farmacia.entity;

import br.com.farmacia.farmacia.entity.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author vini
 */
public class ProdutoMovimentacaoJpaController implements Serializable {

    public ProdutoMovimentacaoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean create(ProdutoMovimentacao produtoMovimentacao) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(produtoMovimentacao);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProdutoMovimentacao produtoMovimentacao) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            produtoMovimentacao = em.merge(produtoMovimentacao);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = produtoMovimentacao.getId();
                if (findProdutoMovimentacao(id) == null) {
                    throw new NonexistentEntityException("The produtoMovimentacao with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProdutoMovimentacao produtoMovimentacao;
            try {
                produtoMovimentacao = em.getReference(ProdutoMovimentacao.class, id);
                produtoMovimentacao.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produtoMovimentacao with id " + id + " no longer exists.", enfe);
            }
            em.remove(produtoMovimentacao);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProdutoMovimentacao> findProdutoMovimentacaoEntities() {
        return findProdutoMovimentacaoEntities(true, -1, -1);
    }

    public List<ProdutoMovimentacao> findProdutoMovimentacaoEntities(int maxResults, int firstResult) {
        return findProdutoMovimentacaoEntities(false, maxResults, firstResult);
    }

    private List<ProdutoMovimentacao> findProdutoMovimentacaoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProdutoMovimentacao.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public ProdutoMovimentacao findProdutoMovimentacao(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProdutoMovimentacao.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdutoMovimentacaoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProdutoMovimentacao> rt = cq.from(ProdutoMovimentacao.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
