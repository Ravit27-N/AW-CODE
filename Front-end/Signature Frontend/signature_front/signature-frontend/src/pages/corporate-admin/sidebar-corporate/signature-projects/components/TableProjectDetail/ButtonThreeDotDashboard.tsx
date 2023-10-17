import {UNKOWNERROR} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {Localization} from '@/i18n/lan';
import {useSendReminderMutation} from '@/redux/slides/process-control';
import {Navigate} from '@/utils/common';
import {downloadCurrentDocumentProject} from '@/utils/request/services/MyService';
import {NGDownloadIcon, NGTelegramOutline} from '@assets/iconExport/Allicon';
import {NGDanger, NGEYE} from '@assets/iconExport/ExportIcon';
import NGText from '@components/ng-text/NGText';
import {Divider, MenuItem} from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import {enqueueSnackbar} from 'notistack';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate} from 'react-router-dom';

function ButtonThreeDotDashboard({data}: {data?: any}) {
  const navigate = useNavigate();
  const {t} = useTranslation();
  const [isLoading, setIsLoading] = React.useState(false);
  const [sendReminder, {isLoading: sendReminderLoading}] =
    useSendReminderMutation();

  /** Send reminder */
  const handleSendReminder = async () => {
    if (!data.flowId) {
      return;
    }
    try {
      await sendReminder({flowId: data.flowId}).unwrap();
      enqueueSnackbar(t(Localization('invitation', 'reminder-is-sent')), {
        variant: 'successSnackbar',
      });
    } catch (e: any) {
      enqueueSnackbar(e ? e.message : UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  return (
    <>
      <MenuItem disableRipple onClick={handleSendReminder}>
        {sendReminderLoading ? (
          <CircularProgress
            sx={{fontSize: '9px', width: '17px', height: '17px', mr: '6px'}}
            size="small"
          />
        ) : (
          <NGTelegramOutline />
        )}
        {t(Localization('list-action', 'send-reminder'))}
      </MenuItem>
      <MenuItem
        disableRipple
        onClick={() => {
          navigate(
            Navigate(`${Route.corporate.SIGNATURE_PROJECT_DETAIL}/${data.id}`),
          );
        }}>
        <NGEYE />
        <NGText text={t(Localization('list-action', 'view-project'))} />
      </MenuItem>
      <MenuItem
        disableRipple
        disabled={isLoading}
        onClick={async () => {
          setIsLoading(true);
          const res = await downloadCurrentDocumentProject(data.docId);
          if (res) {
            setIsLoading(false);
            window.open(res);
          }
        }}>
        {isLoading ? (
          <CircularProgress size={'16px'} sx={{mr: 1}} />
        ) : (
          <NGDownloadIcon />
        )}

        <NGText text={t(Localization('list-action', 'download-document'))} />
      </MenuItem>

      <Divider />
      <MenuItem disableRipple>
        <NGDanger />
        <NGText text={t(Localization('list-action', 'cancel-project'))} />
      </MenuItem>
    </>
  );
}

export default ButtonThreeDotDashboard;
