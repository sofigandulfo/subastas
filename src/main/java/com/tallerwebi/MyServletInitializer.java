package com.tallerwebi;

import com.tallerwebi.config.DatabaseInitializationConfig;
import com.tallerwebi.config.HibernateConfig;
import com.tallerwebi.config.SpringWebConfig;
import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MyServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  // services and data sources
  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class[0];
  }

  // controller, view resolver, handler mapping
  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class[] {
      SpringWebConfig.class,
      HibernateConfig.class,
      DatabaseInitializationConfig.class,
    };
  }

  @Override
  protected String[] getServletMappings() {
    return new String[] { "/" };
  }

  @Override
  protected Filter[] getServletFilters() {
    return new Filter[] { new DelegatingFilterProxy("springSecurityFilterChain") };
  }

  @Override
  protected void customizeRegistration(javax.servlet.ServletRegistration.Dynamic registration) {
    MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
      "/ruta/temporal",
      1024 * 1024 * 5, // Tamaño máximo del archivo (en bytes)
      1024 * 1024 * 10, // Tamaño máximo total de la solicitud (en bytes)
      0
    ); // Tamaño umbral para almacenar en memoria (0 para almacenar todo en disco)
    registration.setMultipartConfig(multipartConfigElement);
  }
}
