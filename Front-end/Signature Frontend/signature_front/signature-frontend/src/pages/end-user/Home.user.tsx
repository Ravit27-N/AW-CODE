import {Localization} from '@/i18n/lan';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {storeSignatureTemplate} from '@/redux/slides/authentication/authenticationSlide';

import {
  TemplateInterface,
  useGetTemplatesCorporateQuery,
} from '@/redux/slides/profile/template/templateSlide';
import {Center, HStack} from '@/theme';
import {$isarray} from '@/utils/request/common/type';
import bgLogo from '@assets/background/projectDetail/NGProjectDetailBackground.svg';
import {BoxAddModel, NGBoxModel} from '@components/ng-box-of-model/NGBoxModel';
import TableComponent from '@components/ng-table/TableDashboard/NGTableComponent';
import NGText from '@components/ng-text/NGText';
import {NGTitle} from '@components/ng-title/NGTitle';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import {Box} from '@mui/system';
import React, {useEffect} from 'react';
import {useTranslation} from 'react-i18next';
import {useOutletContext} from 'react-router';
import {useParams, useNavigate} from 'react-router-dom';
import {FigmaCTA} from '@constant/style/themFigma/CTA';
import {Navigate} from '@/utils/common';
import {Route} from '@/constant/Route';

const padding = 7;
const Dashboard = () => {
  const {t} = useTranslation();
  const navigate = useNavigate();
  const param = useParams();
  const {user} = useAppSelector(state => state.authentication);
  const dispatch = useAppDispatch();
  const {setPopup} = useOutletContext<{
    setPopup: React.Dispatch<React.SetStateAction<boolean>>;
  }>();
  const [hide, setHide] = React.useState<boolean>(false);
  const [templates, setTemplates] = React.useState<TemplateInterface[]>();
  const getTemplateRedux = useGetTemplatesCorporateQuery();

  useEffect(() => {
    if (param) {
      if ('projectId' in param) {
        setPopup(true);
      }
    }
  }, []);

  useEffect(() => {
    setTemplates(getTemplateRedux.data);
  }, [getTemplateRedux.data]);
  return (
    <Box
      sx={{
        width: '100%',
        height: {lg: '100vh', md: 'auto', sm: 'auto'},
        overflowY: {lg: 'hidden', md: 'scroll', sm: 'scroll'},
      }}>
      <Center
        sx={{
          width: 'full',
          backgroundImage: `url(${bgLogo})`,
          backgroundRepeat: 'no-repeat',
          backgroundSize: 'cover',
        }}>
        <Stack
          sx={{
            width: '100%',
            p: '40px 72px',
          }}
          spacing={2}>
          <HStack sx={{width: '100%', justifyContent: 'flex-start'}}>
            <NGText text={'|'} myStyle={{color: 'Primary.main'}} />
            <NGText
              text={t(Localization('text', 'Welcome')) + user.username}
              myStyle={{fontWeight: 600}}
            />
          </HStack>
          <NGTitle
            title1={t(
              Localization(
                'text',
                'Créez un projet de zéro ou depuis un modèle',
              ),
            )}
            title2={
              templates?.length === 0 ? (
                ''
              ) : (
                <NGText
                  onClick={() => {
                    navigate(Navigate(Route.MODEL));
                  }}
                  myStyle={{
                    ...FigmaCTA.CtaMedium,
                    color: 'primary.main',
                    cursor: 'pointer',
                  }}
                  text={t(Localization('text', 'Voir tous les modèles'))}
                />
              )
            }
          />
          <Box
            display={hide ? 'show' : 'none'}
            onClick={() => {
              setHide(false);
            }}></Box>
          <Box display={hide ? 'none' : 'show'} sx={{cursor: 'pointer'}}>
            <Grid container spacing={2} width={'100%'}>
              <Grid item lg={2} md={6} sm={12} onClick={() => setPopup(true)}>
                <NGBoxModel />
              </Grid>
              {$isarray(templates) &&
                templates?.map(item => {
                  return (
                    <Grid
                      item
                      lg={2}
                      md={6}
                      sm={12}
                      key={item.id}
                      onClick={() => {
                        dispatch(storeSignatureTemplate({template: item}));
                        setPopup(true);
                      }}>
                      <BoxAddModel
                        title={item.name}
                        sub1={
                          item.signature +
                          ' ' +
                          t(Localization('text', 'signataire.trice.s'))
                        }
                        sub2={
                          item.approval +
                          ' ' +
                          t(Localization('text', 'approbateur.trice.s'))
                        }
                        noActionOnClickPlus={true}
                      />
                    </Grid>
                  );
                })}
            </Grid>
          </Box>
        </Stack>
      </Center>
      <NGTitle
        props={{mt: 2, mb: 2, px: padding}}
        title1={t(Localization('text', 'Vos projets de signature récents'))}
        title2={
          <NGText
            onClick={() => {
              navigate(Navigate(Route.project.projectDetail));
            }}
            myStyle={{
              ...FigmaCTA.CtaMedium,
              color: 'primary.main',
              cursor: 'pointer',
            }}
            text={t(Localization('text', 'Voir tous les projets'))}
          />
        }
      />
      <Stack px={5}>
        <TableComponent />
      </Stack>
    </Box>
  );
};

export default Dashboard;
