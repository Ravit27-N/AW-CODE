import {Localization} from '@/i18n/lan';
import {
  NGCalendarTableParticipant,
  NGDanger,
  NGDownloadIcon,
} from '@assets/iconExport/Allicon';
import {Divider, MenuItem} from '@mui/material';
import {useTranslation} from 'react-i18next';

import {CancelProjectDialog} from '@/components/ng-cancel-project/NGCancelProject';
import {store} from '@/redux';
import {projectDetailAction} from '@/redux/slides/authentication/authenticationSlide';
import {downloadCurrentDocumentSignatory} from '@/utils/request/services/MyService';
import NGText from '@components/ng-text/NGText';
import React from 'react';

interface ButtonThreeDotParticipantInterface {
  participantId: number;
  onClose?: () => void;
  data?: {
    projectId: string;
    projectName: string;
  };
}

function ButtonThreeDotParticipant({
  participantId,
  data,
}: ButtonThreeDotParticipantInterface) {
  const {t} = useTranslation();
  const [cancelProject, setCancelProject] = React.useState(false);
  const iconStyle = {
    mr: 2,
  };

  return (
    <>
      <MenuItem
        sx={{mt: '5px'}}
        disableRipple
        onClick={() =>
          store.dispatch(projectDetailAction({'modified-date': true}))
        }>
        <NGCalendarTableParticipant sx={{...iconStyle}} />
        <NGText text={t(Localization('list-action', 'modify-expire-date'))} />
      </MenuItem>

      <MenuItem
        onClick={async () => downloadCurrentDocumentSignatory(participantId)}
        disableRipple>
        <NGDownloadIcon sx={{...iconStyle}} />
        <NGText text={t(Localization('list-action', 'download-document'))} />
      </MenuItem>

      <Divider />
      <MenuItem disableRipple onClick={() => setCancelProject(true)}>
        <NGDanger sx={{...iconStyle}} />
        <NGText text={t(Localization('list-action', 'cancel-project'))} />
      </MenuItem>
      {cancelProject && data && (
        <CancelProjectDialog
          data={{
            projectId: data.projectId,
            projectName: data.projectName,
          }}
          open={cancelProject}
          setCancelProject={setCancelProject}
        />
      )}
    </>
  );
}

export default ButtonThreeDotParticipant;
