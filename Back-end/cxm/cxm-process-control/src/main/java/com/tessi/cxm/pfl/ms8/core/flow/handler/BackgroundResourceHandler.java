package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.tessi.cxm.pfl.ms8.constant.Go2pdfBackgroundPosition;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.BackgroundPosition;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BackgroundResourceHandler implements ResourceHandler {

  private final FileManagerResource fileManagerResource;

  @Override
  public void addContext(ExecutionContext context, List<ResourceFile> resourceFiles) {
    final String configPath = fileManagerResource.getConfigPath();

    Map<String, ResourceFile> backgroundFileMap =
        resourceFiles.stream()
            .collect(
                Collectors.toMap(ResourceFile::getPosition, backgroundFile -> backgroundFile));

    BackgroundPage backgroundPage = new BackgroundPage();

    if (backgroundFileMap.containsKey(BackgroundPosition.ALL_PAGES.name())) {
      ResourceFile resourceFile = backgroundFileMap.get(BackgroundPosition.ALL_PAGES.name());
      backgroundPage.setBackground(getBackgroundPath(configPath, resourceFile));
      backgroundPage.setPosition(getGo2pdfBackgroundPosition(resourceFile.getPosition()));
    } else {
      if (backgroundFileMap.containsKey(BackgroundPosition.FIRST_PAGE.name())) {
        ResourceFile resourceFile = backgroundFileMap.get(BackgroundPosition.FIRST_PAGE.name());
        backgroundPage.setBackgroundFirst(getBackgroundPath(configPath, resourceFile));
        backgroundPage.setPositionFirst(getGo2pdfBackgroundPosition(resourceFile.getPosition()));
      }

      if (backgroundFileMap.containsKey(BackgroundPosition.NEXT_PAGES.name())) {
        ResourceFile resourceFile = backgroundFileMap.get(BackgroundPosition.NEXT_PAGES.name());
        backgroundPage.setBackground(getBackgroundPath(configPath, resourceFile));
        backgroundPage.setPosition(getGo2pdfBackgroundPosition(resourceFile.getPosition()));
      }

      if (backgroundFileMap.containsKey(BackgroundPosition.LAST_PAGE.name())) {
        ResourceFile resourceFile = backgroundFileMap.get(BackgroundPosition.LAST_PAGE.name());
        backgroundPage.setBackgroundLast(getBackgroundPath(configPath, resourceFile));
        backgroundPage.setPositionLast(getGo2pdfBackgroundPosition(resourceFile.getPosition()));
      }
    }
    context.put(ProcessControlConstants.BACKGROUND_DTO, backgroundPage);
  }

  private String getBackgroundPath(String configPath, ResourceFile resourceFile) {
    return Path.of(configPath)
        .resolve(resourceFile.getFileId().concat("." + resourceFile.getExtension()))
        .toString();
  }

  private String getGo2pdfBackgroundPosition(String positionKey) {
    return Go2pdfBackgroundPosition.getValueByKey(positionKey).getValue();
  }
}
