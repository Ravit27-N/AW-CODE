import NGText from '@/components/ng-text/NGText';
import {Participant, ProjectStatus} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {Localization} from '@/i18n/lan';
import {styles} from '@/pages/form/process-upload/edit-pdf/other/css.style';
import {useGetProjectByFlowIdQuery} from '@/redux/slides/process-control/participant';
import {Stack} from '@mui/material';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate, useParams} from 'react-router-dom';
import {signDocumentRoleCheck} from '../common/checkRole';
import InvitationLayout from '../invitation/InvitationLayout';

export type IProjectRes = {
  actor: {
    firstName: string;
    lastName: string;
    processed: boolean;
    role: string;
  };
  documents: {docId: string}[];
};

const RefuseDocument = () => {
  const {t} = useTranslation();
  // DocId
  const param = useParams();
  const boxShadow = '2px 4px 16px rgba(112, 144, 176, 0.16)';
  const queryParameters = new URLSearchParams(window.location.search);

  const {
    data: projectData,
    isLoading: getProjectLoading,
    isSuccess: getProjectSuccess,
    isError: getProjectError,
  } = useGetProjectByFlowIdQuery({
    id: param.id + '?' + queryParameters,
  });
  const navigate = useNavigate();
  React.useEffect(() => {
    if (getProjectSuccess) {
      const {
        projectStatus,
        actor: {role, processed, comment},
      } = projectData;
      if (projectStatus === ProjectStatus.EXPIRED) {
        navigate(
          `${Route.participant.expiredProject}/${param.id}?${queryParameters}`,
        );
      }
      if (processed) {
        // signatory not cancel document
        if (!comment) {
          if (role === Participant.Signatory) {
            navigate(
              `${Route.participant.signDocument}/${param.id}?${queryParameters}`,
            );
          } else if (role === Participant.Approval) {
            navigate(
              `${Route.participant.approveDocument}/${param.id}?${queryParameters}`,
            );
          } else if (role === Participant.Receipt) {
            navigate(
              `${Route.participant.receiptDocument}/${param.id}?${queryParameters}`,
            );
          }
        }
      } else {
        // signatory not yet do process yet
        signDocumentRoleCheck(role, param.id!, queryParameters);
      }
    }
  }, [getProjectSuccess]);

  if (getProjectLoading) {
    return <>loading...</>;
  }

  if (getProjectError) {
    return <>An error has occurred!</>;
  }

  return (
    <InvitationLayout position={false}>
      <Stack
        alignItems={'center'}
        justifyContent={'center'}
        sx={{
          height: `calc(100vh - 55px)`,
          ...styles.scrollbarHidden,
          px: '10px',
        }}>
        <Stack
          borderRadius={'10px'}
          boxShadow={boxShadow}
          width={'350px'}
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
            text={t(Localization('sign-document', 'its-refuse!'))}
          />
          <NGText
            font={'poppins'}
            textAlign={'center'}
            text={`${projectData!.actor.firstName},
            ${t(
              Localization('sign-document', 'your refusal has been registered'),
            )}`}
          />
          <NGText
            font={'poppins'}
            fontWeight={'600'}
            textAlign={'center'}
            text={`${projectData?.creatorInfo.firstName} ${
              projectData?.creatorInfo.lastName
            } ${t(Localization('sign-document', 'refuse-notified'))}`}
          />
          <NGText
            font={'poppins'}
            textAlign={'center'}
            text={`${t(Localization('sign-document', 'refuse-leave-page'))}`}
          />
        </Stack>
      </Stack>
    </InvitationLayout>
  );
};

export default RefuseDocument;
