package com.cda;

import com.cda.configuration.ApplicationProperties;
import com.cda.persistence.DatabaseContext;
import com.cda.persistence.DatabaseContextImpl;
import com.cda.repository.RepositoryFactory;
import com.cda.repository.RepositoryFactoryImpl;
import com.cda.service.ServiceFactory;
import com.cda.service.ServiceFactoryImpl;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;

@Service
public class RequestHandler {

  private final EntityManagerFactory entityManagerFactory;
  private final ApplicationProperties properties;

  public RequestHandler(EntityManagerFactory entityManagerFactory, ApplicationProperties properties) {
    this.entityManagerFactory = entityManagerFactory;
    this.properties = properties;
  }

  public DatabaseContext buildDatabaseContext() {
    return new DatabaseContextImpl(entityManagerFactory);
  }

  private RepositoryFactory buildRepositoryFactory(DatabaseContext databaseContext) {
    return new RepositoryFactoryImpl(databaseContext);
  }

  public ServiceFactory buildServiceFactory(DatabaseContext databaseContext) {
    RepositoryFactory repositoryFactory = buildRepositoryFactory(databaseContext);
    return new ServiceFactoryImpl(repositoryFactory, databaseContext, properties);
  }

}
