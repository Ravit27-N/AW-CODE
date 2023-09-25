package com.tessi.cxm.pfl.ms3.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;


@Configuration
public class MultipartConfig {
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver(){
            @Override
            public boolean isMultipart(HttpServletRequest request) {
                // Vérifier si le type de contenu est autorisé
                String contentType = request.getContentType();
                if (contentType != null && contentType.toLowerCase().startsWith("text/csv")) {
                    return super.isMultipart(request);
                }
                return false;
            }
        };
        // Configurez les paramètres du résolveur multipartes
        // Définir la taille maximale du fichier (en octets)
        resolver.setMaxUploadSize(10485760); // 10 Mo
        // Définir la taille maximale totale des fichiers (en octets)
        resolver.setMaxUploadSizePerFile(5242880); // 5 Mo
        // Activer la résolution des noms de fichier Unicode
        resolver.setResolveLazily(true);
        return resolver;
    }
}
