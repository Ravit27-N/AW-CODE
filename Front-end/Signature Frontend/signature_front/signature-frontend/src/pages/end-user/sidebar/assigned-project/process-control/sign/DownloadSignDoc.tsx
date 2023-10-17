import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {styles} from '@/pages/form/process-upload/edit-pdf/other/css.style';
import {useLazyDownloadDocQuery} from '@/redux/slides/process-control/internal/processControlSlide';
import {IGetFlowId} from '@/redux/slides/project-management/project';
import {Backdrop, Button, CircularProgress, Stack} from '@mui/material';
import {t} from 'i18next';
import {Participant} from '@constant/NGContant';

type IDownloadDocument = {
  projectFlowById: IGetFlowId;
};

const DownloadSignDocument = (props: IDownloadDocument) => {
  const {projectFlowById} = props;
  const [trigger, {isLoading}] = useLazyDownloadDocQuery();

  /** title */
  const title = () => {
    let res = '';
    switch (projectFlowById.actor.role) {
      case Participant.Approval:
        res = t(Localization('sign-document', 'its-approve!'));
        break;
      case Participant.Signatory:
        res = t(Localization('sign-document', 'its-signed!'));
        break;
      default:
        res = t(Localization('sign-document', 'its-refuse!'));
    }

    return res;
  };

  /** subTitle */
  const subTitle = () => {
    let res = '';
    switch (projectFlowById.actor.role) {
      case Participant.Approval:
        res = t(
          Localization('sign-document', 'your-approval-has-been-validated'),
        );
        break;
      case Participant.Signatory:
        res = t(
          Localization('sign-document', 'your-signature-has-been-validated'),
        );
        break;
      default:
        res = t(
          Localization('sign-document', 'your refusal has been registered'),
        );
    }

    return res;
  };

  /** description */
  const description = () => {
    let res = '';
    switch (projectFlowById.actor.role) {
      case Participant.Approval:
        res = t(
          Localization('sign-document', 'approval-you-can-view-and-download'),
        );
        break;
      case Participant.Signatory:
        res = t(Localization('sign-document', 'you-can-view-and-download'));
        break;
      default:
        res = t(
          Localization('sign-document', 'your refusal has been registered'),
        );
    }

    return res;
  };

  return (
    <Stack
      alignItems={'center'}
      height="calc(100vh - 221px)"
      justifyContent={'center'}
      sx={{
        ...styles.scrollbarHidden,
        px: '10px',
      }}>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={isLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
      <Stack
        borderRadius={'10px'}
        width={'650px'}
        spacing={4}
        sx={{
          p: '30px',
          fontFamily: 'Poppins',
          justifyContent: 'center',
          alignItems: 'center',
        }}>
        <NGText
          fontSize={25}
          font={'poppins'}
          fontWeight={'600'}
          textAlign={'center'}
          text={title()}
        />
        <NGText
          font={'poppins'}
          textAlign={'center'}
          text={`${projectFlowById.actor.firstName} ${
            projectFlowById.actor.lastName
          } ${subTitle()}`}
        />
        <NGText
          font={'poppins'}
          fontWeight={'600'}
          textAlign={'center'}
          text={description()}
        />

        <a
          target={'_blank'}
          style={{textDecoration: 'none'}}
          onClick={async () => {
            try {
              await trigger({
                docId: projectFlowById.documents[0].docId,
                flowId: projectFlowById.flowId,
              }).unwrap();
            } catch (error) {
              /** */
            }
          }}
          rel="noreferrer">
          <Button
            variant="contained"
            sx={{
              width: '100%',
              mt: '10px',
              mb: '20px',
              '&:hover': {
                bgcolor: '#71717A',
              },
              '&.Mui-disabled': {
                bgcolor: '#71717A',
                color: '#ffffff',
              },
              textTransform: 'none',
              fontWeight: 600,
              bgcolor: 'Primary.main',
              py: '15px',
            }}>
            <NGText
              font={'poppins'}
              sx={{color: '#ffffff'}}
              text={t(Localization('sign-document', 'download-document'))}
            />
          </Button>
        </a>
      </Stack>
    </Stack>
  );
};

export default DownloadSignDocument;
