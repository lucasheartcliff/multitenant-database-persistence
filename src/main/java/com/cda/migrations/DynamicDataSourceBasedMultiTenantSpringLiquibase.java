package com.cda.migrations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.cda.entities.Tenant;
import com.cda.repository.tenant.TenantRepository;
import com.cda.utils.EncryptionService;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class DynamicDataSourceBasedMultiTenantSpringLiquibase implements InitializingBean, ResourceLoaderAware {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;

    @Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;

    private ResourceLoader resourceLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("DynamicDataSources based multitenancy enabled");
        this.runOnAllTenants(tenantRepository.findAll());
    }

    protected void runOnAllTenants(Collection<Tenant> tenants) throws LiquibaseException {
        for(Tenant tenant : tenants) {
            log.info("Initializing Liquibase for tenant " + tenant.getId());
            String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);
            try (Connection connection = DriverManager.getConnection(tenant.getUrl(), tenant.getDb(), decryptedPassword)) {
                DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
                SpringLiquibase liquibase = this.getSpringLiquibase(tenantDataSource);
                liquibase.afterPropertiesSet();
            } catch (SQLException | LiquibaseException e) {
                log.error("Failed to run Liquibase for tenant " + tenant.getId(), e);
            }
            log.info("Liquibase ran for tenant " + tenant.getId());
        }
    }

    protected SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(getResourceLoader());
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setContexts(liquibaseProperties.getContexts());
        return liquibase;
    }

}
