package com.innovationandtrust.project.config;

import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.model.entity.Document;
import com.innovationandtrust.project.model.entity.Project;
import com.innovationandtrust.project.model.entity.ProjectDetail;
import com.innovationandtrust.project.model.entity.Signatory;
import com.innovationandtrust.share.model.project.InvitationMessage;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.sftp.ProjectDocumentModel;
import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.share.model.sftp.ProjectParticipantModel;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
  @Bean
  public ModelMapper modelMapper() {
    var mapper = new ModelMapper();

    mapper.addMappings(
        new PropertyMap<Signatory, SignatoryDto>() {
          @Override
          protected void configure() {
            map().setDocumentStatus(source.getDocumentStatus());
          }
        });

    mapper.addMappings(
        new PropertyMap<SignatoryDto, Participant>() {
          @Override
          protected void configure() {
            map().setOrder(source.getSortOrder());
          }
        });
    mapper.addMappings(
        new PropertyMap<ProjectModel, Project>() {
          @Override
          protected void configure() {
            skip(destination.getId());
          }
        });
    mapper.addMappings(
        new PropertyMap<ProjectParticipantModel, Signatory>() {
          @Override
          protected void configure() {
            map().setFirstName(source.getFirstName());
            map().setLastName(source.getLastName());
            skip(destination.getProject());
          }
        });

    mapper.addMappings(
        new PropertyMap<InvitationMessage, ProjectDetail>() {
          @Override
          protected void configure() {
            map().setTitleInvitation(source.getInvitationSubject());
          }
        });

    mapper.addMappings(
        new PropertyMap<ProjectDocumentModel, Document>() {
          @Override
          protected void configure() {
            map().setTotalPages(source.getInfo().getTotalPages());
            map().setOriginalFileName(source.getInfo().getOriginalFileName());
            map().setSize(source.getInfo().getSize());
          }
        });

    mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    return mapper;
  }
}
