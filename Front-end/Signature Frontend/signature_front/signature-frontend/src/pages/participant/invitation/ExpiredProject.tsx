import NGText from '@/components/ng-text/NGText';
import {ProjectStatus} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {useGetProjectByFlowIdQuery} from '@/redux/slides/process-control/participant';
import {Stack} from '@mui/material';
import {useEffect} from 'react';
import {useTranslation} from 'react-i18next';
import {useParams} from 'react-router-dom';
import {expiredLinkRoleCheck} from '../common/checkRole';
import InvitationLayout from './InvitationLayout';

const ExpiredProject = () => {
  const {id} = useParams();
  const {t} = useTranslation();
  const queryParameters = new URLSearchParams(window.location.search);
  const {data} = useGetProjectByFlowIdQuery({
    id: id + '?' + queryParameters,
  });

  useEffect(() => {
    if (data) {
      const {
        projectStatus,
        actor: {role},
      } = data;
      if (projectStatus !== ProjectStatus.EXPIRED) {
        expiredLinkRoleCheck(role, '' + id, queryParameters);
      }
    }
  }, [data]);

  return (
    <InvitationLayout>
      <Stack
        width="100%"
        height="100%"
        alignItems="center"
        justifyContent="center"
        gap="10px">
        <NGText
          text={t(Localization('invitation', 'link-expired'))}
          myStyle={{
            textAlign: 'center',
            fontSize: '16px',
            fontWeight: '600px',
            textJustify: 'inter-word',
          }}
        />
        {/* <Button
          onClick={() => router.navigate(Route.ROOT)}
          color="primary"
          variant="contained"
          sx={{
            fontFamily: 'Poppins',
            textTransform: 'none',
          }}>
          {t(Localization('upload-document', 'back'))}
        </Button> */}
      </Stack>
    </InvitationLayout>
  );
};

export default ExpiredProject;
