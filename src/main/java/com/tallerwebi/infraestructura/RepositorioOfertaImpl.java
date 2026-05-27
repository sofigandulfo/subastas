package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Oferta;
import com.tallerwebi.dominio.RepositorioOferta;
import com.tallerwebi.dominio.Subasta;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioOfertaImpl implements RepositorioOferta {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioOfertaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardarOferta(Oferta oferta) {
    sessionFactory.getCurrentSession().save(oferta);
  }

  @Override
  public List<Oferta> obtenerMejoresOfertasPorSubasta(Long subastaId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT o FROM Oferta o " +
        "WHERE o.subasta.id = :subastaId " +
        "AND o.monto = (SELECT MAX(o2.monto) FROM Oferta o2 WHERE o2.usuario = o.usuario AND o2.subasta.id = :subastaId) " +
        "ORDER BY o.monto DESC",
        Oferta.class
      )
      .setParameter("subastaId", subastaId)
      .getResultList();
  }

  @Override
  public Oferta obtenerMejorOfertaPorSubasta(Long subastaId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT o FROM Oferta o " + "WHERE o.subasta.id = :subastaId " + "ORDER BY o.monto DESC",
        Oferta.class
      )
      .setParameter("subastaId", subastaId)
      .setMaxResults(1)
      .uniqueResult();
  }

  @Override
  public List<Subasta> buscarSubastasDondeOferto(Long idUsuario) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "SELECT DISTINCT o.subasta FROM Oferta o WHERE o.usuario.id = :idUsuario",
        Subasta.class
      )
      .setParameter("idUsuario", idUsuario)
      .getResultList();
  }
}
