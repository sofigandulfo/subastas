package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.EstadoSubasta;
import com.tallerwebi.dominio.RepositorioSubasta;
import com.tallerwebi.dominio.Subasta;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioSubastaImpl implements RepositorioSubasta {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioSubastaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Subasta guardarSubasta(Subasta subasta) {
    sessionFactory.getCurrentSession().save(subasta);
    return subasta; // después del save, hibernate ya le asignó el id
  }

  @Override
  public Subasta obtenerSubasta(Long id) {
    return (Subasta) sessionFactory
      .getCurrentSession()
      .createCriteria(Subasta.class)
      .add(Restrictions.eq("id", id))
      .uniqueResult();
  }

  @Override
  public List<Subasta> obtenerSubastasPorVencer() {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM Subasta WHERE fechaCierre < :ahora " +
        "AND (estadoSubasta = :activa OR estadoSubasta = :cuentaAtras)",
        Subasta.class
      )
      .setParameter("ahora", LocalDateTime.now())
      .setParameter("activa", EstadoSubasta.ACTIVA)
      .setParameter("cuentaAtras", EstadoSubasta.CUENTA_ATRAS)
      .getResultList();
  }

  @Override
  public List<Subasta> obtenerTodasLasSubastas() {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Subasta", Subasta.class)
      .getResultList();
  }
}
