package com.innovationandtrust.share.service;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.SftpConstant;
import com.innovationandtrust.share.model.project.InvitationMessage;
import com.innovationandtrust.share.model.sftp.DocumentDetailModel;
import com.innovationandtrust.share.model.sftp.ProjectDocumentModel;
import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.share.model.sftp.ProjectModelView;
import com.innovationandtrust.share.model.sftp.ProjectParticipantModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectGenerator {

  /**
   * Initializes the sample project.
   *
   * @return initialized project.
   */
  public static ProjectModel getSampleProject() {
    var projectModel = new ProjectModel();
    projectModel.setName("project-name");
    projectModel.setOrderSign(true);
    projectModel.setOrderApprove(true);
    Date now = new Date();
    projectModel.setExpireDate(new Date(now.getTime() + (1000 * 60 * 60 * 24)));
    projectModel.setAutoReminder(false);
    projectModel.setReminderOption(1); // convert to Text
    projectModel.setChannelReminder(1);
    projectModel.setTemplateId(1L);

    // set sample signatory
    var signatory = new ProjectParticipantModel();
    signatory.setFirstName("firstName");
    signatory.setLastName("lastName");
    signatory.setRole(ParticipantRole.SIGNATORY.getRole());
    signatory.setPhone("+3388");
    signatory.setEmail("mail@example");
    List<ProjectParticipantModel> signatories = new ArrayList<>();
    signatories.add(signatory);
    projectModel.setParticipants(signatories);

    // set sample document
    var document = new ProjectDocumentModel();
    document.setFileName("sample-project-model.pdf");
    List<DocumentDetailModel> docDetails = new ArrayList<>();
    docDetails.add(
        DocumentDetailModel.builder()
            .x(100.00)
            .y(100.00)
            .pageNum(1)
            .type(ParticipantRole.SIGNATORY.getRole())
            .signatoryEmail(signatory.getEmail())
            .build());
    document.setDocumentDetails(docDetails);
    List<ProjectDocumentModel> documents = new ArrayList<>();
    documents.add(document);
    projectModel.setDocuments(documents);

    // set invitation message(details)
    List<InvitationMessage> invitations = new ArrayList<>();
    invitations.add(new InvitationMessage("Title", "Message", ParticipantRole.SIGNATORY.getRole()));
    projectModel.setDetails(invitations);

    return projectModel;
  }

  /**
   * Generate xml file with only required contents for user input.
   *
   * @return Resource file.
   */
  public static Resource downloadSample() {
    try {
      XmlMapper xmlMapper = new XmlMapper();
      xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
      var project = ProjectGenerator.getSampleProject();
      byte[] data =
          xmlMapper
              .writer()
              .withDefaultPrettyPrinter()
              .withView(ProjectModelView.Xml.class)
              .writeValueAsBytes(project);
      return new ByteArrayResource(data);
    } catch (Exception e) {
      log.error("Generate sample project xml model failed", e);
      return null;
    }
  }

  private static <T> Set<String> findNullFields(T object) {
    Set<String> nullFields = new HashSet<>();

    Class<?> clazz = object.getClass();
    Field[] fields = clazz.getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);
      try {
        if (field.get(object) == null) {
          nullFields.add(field.getName());
        }
      } catch (IllegalAccessException e) {
        log.error("Could not find null field");
      }
    }
    return nullFields;
  }
}
