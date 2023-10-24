package com.innovationandtrust.project;

import com.innovationandtrust.utils.pdf.provider.PdfProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = QuartzAutoConfiguration.class)
public class ProjectManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProjectManagementApplication.class, args);
  }

  @Bean
  public PdfProvider pdfProvider() {
    return new PdfProvider();
  }
}
