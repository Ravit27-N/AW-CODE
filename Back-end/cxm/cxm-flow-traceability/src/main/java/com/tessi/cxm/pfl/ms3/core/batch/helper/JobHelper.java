package com.tessi.cxm.pfl.ms3.core.batch.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.dto.DocumentCsvProjection;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentExportStatus;
import com.tessi.cxm.pfl.ms3.exception.TechnicalException;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobHelper.class);
    private static final String PRE_INVOICING_DOCUMENT_EXTENSION = ".csv";
    private static final String PATH_SEPARATOR = "/";

    private static final String FILE_SEPARATOR = "-";
    private final FlowDocumentRepository flowDocumentRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cxm.storage.preinvoicing.export}")
    private String preInvoicingExportPath;
    @Value("${cxm.storage.preinvoicing.import}")
    private String preInvoicingImportPath;
    @Value("${cxm.preinvoicing.document.export.name}")
    private String preInvoicingExportDocumentName;
    @Value("${cxm.preinvoicing.document.import.name}")
    private String preInvoicingImportDocumentName;


    @Autowired
    public JobHelper(FlowDocumentRepository flowDocumentRepository, RestTemplate restTemplate,
                     ObjectMapper objectMapper) {
        this.flowDocumentRepository = flowDocumentRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public void exportDocument(DocumentCsvProjection documentProjection) {

        try {
            LOGGER.info("Document {} Exported", documentProjection.getId_document());
            /*transactionPort.archiveTransaction(signature.getTransactionId(), null, archiveMetadata,
                    null);*/
            LOGGER.info("Document {} Exported", documentProjection.getId_document());
        } catch (Exception e) {
            LOGGER.error("Document {} Exported error", documentProjection.getId_document());
            throw new TechnicalException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,noRollbackFor = Exception.class)
    public void exportAllToCsv(List<DocumentCsvProjection> documents) {
        File repertoire=new File(this.preInvoicingExportPath);
        if(!repertoire.exists()){
            repertoire.mkdirs();
            LOGGER.info("repertoire créer!");
        }else {
            LOGGER.info("repertoire existe!");
        }
        CSVPrinter csvPrinter = null;
        CSVFormat csvFileFormat = CSVFormat.EXCEL.withHeader();

        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");
            String formattedDate = now.format(format);

            FileWriter fileWriter = new FileWriter(repertoire.getPath().concat(PATH_SEPARATOR)
                    .concat(this.preInvoicingExportDocumentName)
                    .concat(FILE_SEPARATOR)
                    .concat(formattedDate)
                    .concat(PRE_INVOICING_DOCUMENT_EXTENSION));
            List<Long> documentsId = new ArrayList<>();
                csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("Id_document", "Date_production", "Date_distribution", "Canal", "Categorie", "Recto_verso", "Nb_pages",
                    "Nb_feuilles", "Couleur", "Code_postal", "Enveloppe_reelle", "Compagnie", "Id_dest",
                    "Filler_1", "Filler_2", "Filler_3", "Filler_4", "Filler_5", "Statut", "Doc_name",
                    "Duree_archivage", "Type_agrafe", "Urgence_reelle", "Poids", "Tranche_reelle", "Affranchissement", "Service", "Zone_geo", "Code_pays", "Nom_prestataire"));
            CSVPrinter finalCsvPrinter = csvPrinter;
            documents.forEach(d -> {
                documentsId.add(d.getId_document());
                try {
                    finalCsvPrinter.printRecord(d.getId_document(), d.getDate_production(), d.getDate_distribution(), d.getCanal(), d.getCategorie(),
                            d.getRecto_verso(), d.getNb_pages(), d.getNb_feuilles(), d.getCouleur(), d.getCode_postal(),
                            d.getEnveloppe_reelle(), d.getCompagnie(), d.getId_dest(), d.getFiller_1(),
                            d.getFiller_2(), d.getFiller_3(), d.getFiller_4(), d.getFiller_5(), d.getStatut(), d.getDoc_name(),
                            d.getDuree_archivage(), d.getType_agrafe(), d.getUrgence_reelle(),
                            d.getPoids(), d.getTranche_reelle(), d.getAffranchissement(), d.getService(), d.getZone_geo(), d.getCode_pays(), d.getNom_prestataire());
                } catch (IOException e) {
                    LOGGER.error("Erreur lors de l'écriture dans le fichier CSV", e);
                }
            });
            fileWriter.flush();
            fileWriter.close();
            csvPrinter.close();
            LOGGER.info("Données exportées avec succès");
            List<FlowDocument> flowDocuments = flowDocumentRepository.findAllById(documentsId);
            flowDocuments.forEach(flowDocument -> {
                flowDocument.setExportStatus(FlowDocumentExportStatus.EXPORTED);
                flowDocumentRepository.saveAndFlush(flowDocument);
            });
        } catch (IOException e) {
            LOGGER.error("Erreur lors de l'exportation des données vers CSV", e);
        }
    }
}
