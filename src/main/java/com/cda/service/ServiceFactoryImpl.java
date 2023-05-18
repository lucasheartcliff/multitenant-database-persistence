package com.cda.service;

import com.cda.configuration.ApplicationProperties;
import com.cda.persistence.DatabaseContext;
import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactory;
import com.cda.service.tenant.TenantService;
import com.cda.utils.EncryptionService;
import com.cda.utils.EncryptionServiceImpl;

public class ServiceFactoryImpl implements ServiceFactory {
  private final RepositoryFactory repositoryFactory;
  private final DatabaseContext databaseContext;
  private final ApplicationProperties applicationProperties;

  private TransactionHandler cachedTransactionHandler;

  public ServiceFactoryImpl(
      RepositoryFactory repositoryFactory,
      DatabaseContext databaseContext,
      ApplicationProperties properties) {
    this.repositoryFactory = repositoryFactory;
    this.databaseContext = databaseContext;
    this.applicationProperties = properties;
  }

  public TenantService buildTenantService() {
    return new TenantService(
        repositoryFactory,
        getTransactionHandler(),
        buildEncryptionService(),
        null,
        null,
        null,
        applicationProperties.getTenantUrlPrefix(),
        applicationProperties.getSecret(),
        applicationProperties.getSalt());
  }

  private EncryptionService buildEncryptionService() {
    return new EncryptionServiceImpl();
  }

  private TransactionHandler getTransactionHandler() {
    if (cachedTransactionHandler == null)
      cachedTransactionHandler = new TransactionHandler(databaseContext);
    return cachedTransactionHandler;
  }
}
