import NGText from '@components/ng-text/NGText';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {FigmaHeading} from '@constant/style/themFigma/FigmaHeading';
import {Box, Divider, Stack} from '@mui/material';
import {useTranslation} from 'react-i18next';
import {useNavigate, useParams} from 'react-router-dom';

import NGCopyCountrySelect from '@components/ng-copy/select-country/NGCopyCountrySelecte';

import {Localization} from '@/i18n/lan';
import {useGetProjectByFlowIdQuery} from '@/redux/slides/process-control/participant';
import {NGCIN, NGPassport, NGStayBook} from '@assets/iconExport/Allicon';
import {Route} from '@constant/Route';
import {StyleConstant} from '@constant/style/StyleConstant';
import IdentityLayout from '@pages/participant/advance-signature/identity/IdentityLayout';
import React from 'react';

function IdentityPage() {
  const {t} = useTranslation();
  const navigate = useNavigate();
  const {id} = useParams();
  const queryParameters = new URLSearchParams(window.location.search);
  const {currentData} = useGetProjectByFlowIdQuery({
    id: id + '?' + queryParameters,
  });
  /** data of Identity cards **/
  const OptionIdentity = [
    {
      icon: <NGCIN sx={{color: 'black', width: '40px', height: '40px'}} />,
      title: t(Localization('identity-page', 'card-national')),
      goto:
        Route.participant.advance.identityDoc.cardNational +
        `/${id}?${queryParameters}`,
    },
    {
      icon: <NGPassport sx={{color: 'white', width: '40px', height: '40px'}} />,
      title: t(Localization('identity-page', 'passport')),
      goto:
        Route.participant.advance.identityDoc.passport +
        `/${id}?${queryParameters}`,
    },
    {
      icon: <NGStayBook sx={{color: 'black', width: '40px', height: '40px'}} />,
      title: t(Localization('identity-page', 'stay-book')),
      goto:
        Route.participant.advance.identityDoc.stayBook +
        `/${id}?${queryParameters}`,
    },
  ];

  React.useMemo(() => {
    if (currentData) {
      const {
        actor: {documentVerified},
      } = currentData;

      if (documentVerified) {
        return navigate(
          `${Route.participant.viewSignatoryFile}/${id}?${queryParameters}`,
        );
      }
    }
  }, [currentData]);

  return (
    <IdentityLayout>
      <Stack gap={'10px'} width={'100%'} alignItems={'center'} mb={'10px'}>
        <NGText
          text={t(Localization('identity-page', 'select-ur-id'))}
          sx={{
            ...FigmaHeading.H3,
            width: '250px',
            textAlign: 'center',
          }}
        />

        <NGText
          sx={{
            ...FigmaBody.BodyMedium,
            width: '350px',
            textAlign: 'center',
          }}
          text={t(Localization('identity-page', 'choose-type-doc'))}
        />
      </Stack>
      <NGCopyCountrySelect />
      <Divider
        style={{
          ...StyleConstant.line.lineIdentityPage,
          marginLeft: '2px',
        }}
      />
      <Box
        display="grid"
        sx={{width: '100%', marginTop: '20px'}}
        gridTemplateColumns="repeat(2, 1fr)"
        rowGap={2}
        columnGap={'10px'}>
        {/** Data of Option Identity **/}
        {OptionIdentity.map(item => (
          <Stack
            key={item.title}
            onClick={() => {
              navigate(item.goto);
            }}
            sx={{
              ...StyleConstant.box.gridCard,
              borderColor: 'red',
            }}>
            {item.icon}
            <NGText
              text={item.title}
              sx={{width: '125px', textAlign: 'center'}}
            />
          </Stack>
        ))}
      </Box>
    </IdentityLayout>
  );
}
export default IdentityPage;
