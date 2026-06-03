package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.autopuja.AutoPuja;
import com.tallerwebi.dominio.autopuja.RepositorioAutoPuja;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioAutoPujaImpl implements RepositorioAutoPuja {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioAutoPujaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(AutoPuja autoPuja) {
    sessionFactory.getCurrentSession().save(autoPuja);
  }

  @Override
  public List<AutoPuja> obtenerAutoPujasActivasPorSubasta(Long subastaId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM AutoPuja " +
        "WHERE subasta.id = :subastaId " +
        "AND activa = true " +
        "ORDER BY montoMaximo DESC, fechaCreacion ASC",
        AutoPuja.class
      )
      .setParameter("subastaId", subastaId)
      .getResultList();
  }
}
