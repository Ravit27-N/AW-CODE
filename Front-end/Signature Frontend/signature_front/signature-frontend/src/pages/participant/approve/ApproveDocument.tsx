import NGText from '@/components/ng-text/NGText';
import {ProjectStatus} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {Localization} from '@/i18n/lan';
import {styles} from '@/pages/form/process-upload/edit-pdf/other/css.style';
import {
  useApproveDocumentMutation,
  useGetProjectByFlowIdQuery,
} from '@/redux/slides/process-control/participant';

import {downloadLink} from '@/utils/common/DownloadDocLink';
import {LoadingButton} from '@mui/lab';
import {Stack} from '@mui/material';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate, useParams, useSearchParams} from 'react-router-dom';
import {validateDownloadRoleApprove} from '../common/checkRole';
import InvitationLayout from '../invitation/InvitationLayout';

const ApproveDocument = () => {
  const {t} = useTranslation();
  const [searchParam] = useSearchParams();
  // DocId
  const param = useParams();
  const company_uuid = param.id;
  const [approveDocument, {isLoading, isError, error}] =
    useApproveDocumentMutation();
  const queryParameters = new URLSearchParams(window.location.search);
  const boxShadow = '2px 4px 16px rgba(112, 144, 176, 0.16)';
  const navigate = useNavigate();
  const {
    data: projectData,
    isLoading: getProjectLoading,
    isSuccess: getProjectSuccess,
    isError: getProjectError,
    refetch,
  } = useGetProjectByFlowIdQuery({
    id: company_uuid + '?' + queryParameters,
  });

  const submitApproveDocument = async () => {
    const flowId = company_uuid as any;
    const uuid = searchParam.get('token')!;

    try {
      await approveDocument({flowId, uuid}).unwrap();
      return refetch();
    } catch (e) {
      if (e) {
        return <pre>An error occurred!</pre>;
      }
    }
    return null;
  };

  React.useEffect(() => {
    if (getProjectSuccess) {
      const {
        projectStatus,
        actor: {role, processed, comment},
      } = projectData;
      if (projectStatus === ProjectStatus.EXPIRED) {
        navigate(
          `${Route.participant.expiredProject}/${company_uuid}?${queryParameters}`,
        );
      }
      const validate = validateDownloadRoleApprove(role);
      if (validate) {
        navigate(`${validate}/${company_uuid}?${queryParameters}`);
      } else {
        if (!processed) {
          submitApproveDocument().then(r => r);
        } else {
          if (comment) {
            navigate(
              `${Route.participant.refuseDocument}/${company_uuid}?${queryParameters}`,
            );
          } else {
            navigate(
              `${Route.participant.approveDocument}/${company_uuid}?${queryParameters}`,
            );
          }
        }
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
          ...styles.scrollbarHidden,
          px: '10px',
        }}>
        {isError ? (
          <pre>{JSON.stringify(error, null, 2)}</pre>
        ) : (
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
              text={t(Localization('sign-document', 'its-approve!'))}
            />
            <NGText
              font={'poppins'}
              textAlign={'center'}
              text={`${projectData!.actor.firstName},
            ${t(
              Localization('sign-document', 'your-approval-has-been-validated'),
            )}`}
            />
            <NGText
              font={'poppins'}
              fontWeight={'600'}
              textAlign={'center'}
              text={t(
                Localization(
                  'sign-document',
                  'approval-you-can-view-and-download',
                ),
              )}
            />
            {isLoading && (
              <LoadingButton
                loading={isLoading}
                loadingPosition="end"
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
                  text={
                    isLoading
                      ? t(Localization('sign-document', 'in-preparation'))
                      : t(Localization('sign-document', 'download-document'))
                  }
                />
              </LoadingButton>
            )}
            {projectData!.actor.processed && (
              <a
                target={'_blank'}
                style={{textDecoration: 'none'}}
                href={downloadLink({
                  docId: projectData!.documents[0].docId,
                  token: searchParam.get('token')!,
                  company_uuid: company_uuid!,
                })}
                rel="noreferrer">
                <LoadingButton
                  loading={false}
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
                </LoadingButton>
              </a>
            )}
          </Stack>
        )}
      </Stack>
    </InvitationLayout>
  );
};

export default ApproveDocument;
