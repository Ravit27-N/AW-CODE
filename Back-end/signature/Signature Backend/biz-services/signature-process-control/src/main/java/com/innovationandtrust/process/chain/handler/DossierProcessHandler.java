package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.enums.VerificationChoice;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DossierProcessHandler extends AbstractExecutionHandler {

  private final SignatureIdentityVerificationFeignClient verificationFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    this.generateParticipantsUuid(project);
    var signatureLevel = project.getSignatureLevel();

    log.info("Project created in {} signature level...", signatureLevel);
    if (Objects.equals(signatureLevel, SignatureSettingLevel.SIMPLE.getValue())) {
      return ExecutionState.NEXT;
    }

    List<DossierDto> dossierDtoS = new ArrayList<>();

    var participants = project.getParticipantsByRole(ParticipantRole.SIGNATORY.getRole());
    participants.forEach(
        participant -> {
          var dossier =
              DossierDto.builder()
                  .dossierName(participant.getFullName())
                  .firstname(participant.getFirstName())
                  .tel(participant.getPhone())
                  .participantUuid(participant.getUuid())
                  .verificationChoice(VerificationChoice.CLASSIC_DOCUMENT)
                  .build();
          log.info("Creating dossier: {}", dossier.toString());
          dossierDtoS.add(dossier);
        });

    log.info("Invoking verification service to save participants dossiers...");
    var savedDossiers = this.verificationFeignClient.createDossiers(dossierDtoS);

    log.info("Successfully saved participants dossiers...");
    participants.forEach(
        participant ->
            savedDossiers.stream()
                .filter(d -> Objects.equals(d.getParticipantUuid(), participant.getUuid()))
                .findFirst()
                .ifPresent(d -> participant.setDossierId(d.getDossierId())));

    return ExecutionState.NEXT;
  }

  private void generateParticipantsUuid(Project project) {
    log.info("Generating participants uuid...");
    project
        .getParticipants()
        .forEach(participant -> participant.setUuid(UUID.randomUUID().toString()));
  }
}
