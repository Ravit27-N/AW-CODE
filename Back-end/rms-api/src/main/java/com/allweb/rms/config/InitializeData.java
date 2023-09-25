package com.allweb.rms.config;

import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.entity.jpa.ApplicationInformation;
import com.allweb.rms.repository.jpa.ApplicationInformationRepository;
import com.allweb.rms.utils.StorageUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitializeData {
  private final String companyFolderName = "company";
  private final String logoName = "allweb-logo.jpg";
  private final DataSource dataSource;
  private final ApplicationInformationRepository applicationInformationRepository;
  private final StorageUtils storageUtils;
  @Value("${application.version}")
  private String currentAppVersion;
  @Value("classpath:images/allweb-logo.jpg")
  private Resource companyLogo;

  @Autowired
  public InitializeData(
      DataSource dataSource,
      ApplicationInformationRepository applicationInformationRepository,
      StorageUtils storageUtils) {
    this.dataSource = dataSource;
    this.applicationInformationRepository = applicationInformationRepository;
    this.storageUtils = storageUtils;
  }

  public void loadData(Resource[] resources, DataSource dataSource) throws SQLException {
    try (Connection con = Objects.requireNonNull(dataSource).getConnection()) {
      new ResourceDatabasePopulator(resources).populate(con);
      if (!con.getAutoCommit()) con.commit();
    }
  }

  @EventListener(ApplicationStartedEvent.class)
  public void setup() throws SQLException, IOException {
    initLogo();
    Optional<ApplicationInformation> systemDetails =
        this.applicationInformationRepository.findById(1);
    if (systemDetails.isPresent()) {
      log.info("Initializing is already initialized.");
      return;
    }
    log.info("Initializing application:");
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    // primary database
    Resource[] resources =
        new Resource[] {
          resourceLoader.getResource("sql/init-data.sql"),
          // ..... more resource
        };
    loadData(resources, dataSource);
    ApplicationInformation applicationInformation = new ApplicationInformation();
    applicationInformation.setInitialized(true);
    applicationInformation.setVersion(this.currentAppVersion);
    this.applicationInformationRepository.save(applicationInformation);
    log.info("Initializing is already initialized.");
  }

  public void initLogo() throws IOException {
    StorageObjectManager companyFolderManager =
        storageUtils.getSubDirectory(this.companyFolderName).getStorageObjectManager();
    if (!companyFolderManager.exists(this.logoName)) {
      storageUtils.saveFile(this.companyLogo.getInputStream(), this.logoName, companyFolderManager);
    }
  }
}
