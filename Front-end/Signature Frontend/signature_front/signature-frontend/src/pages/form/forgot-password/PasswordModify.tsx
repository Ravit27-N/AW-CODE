import {Route} from '@/constant/Route';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {Navigate} from '@/utils/common';
import Logo from '@assets/background/login/NGLogo.svg';
import bg from '@assets/background/login/bg.svg';
import {NGArrowLeft} from '@assets/iconExport/Allicon';
import {NgSvgBackground} from '@components/ng-background/NGSvgBackground';
import NGText from '@components/ng-text/NGText';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {FigmaCTA} from '@constant/style/themFigma/CTA';
import {FigmaHeading} from '@constant/style/themFigma/FigmaHeading';
import {Stack} from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import {useTranslation} from 'react-i18next';
import {useNavigate} from 'react-router-dom';
function PasswordModify() {
  const {t} = useTranslation();
  const navigate = useNavigate();
  const {theme} = useAppSelector(state => state.enterprise);
  return (
    <Grid container component="main" sx={{height: '100vh'}}>
      <CssBaseline />
      <Grid
        item
        xs={12}
        sm={12}
        md={5}
        overflow={'hidden'}
        component={Paper}
        elevation={6}
        square>
        <Stack
          height={'70%'}
          width={'100%'}
          ml={'10%'}
          mt={10}
          alignItems={'flex-start'}>
          <Stack width={'100%'} spacing={12}>
            <img
              src={theme[0].logo ?? Logo}
              style={{height: '48px', maxWidth: '205px'}}
              alt={'Logo'}
            />
            <Stack spacing={'28px'}>
              <Stack spacing={2}>
                <NGText
                  text={t(Localization('form', `request-sent`))}
                  myStyle={{...FigmaHeading.H1}}
                />
                <NGText
                  text={t(Localization('form', `text-request-sent`))}
                  width={'416px'}
                  myStyle={{...FigmaBody.BodyMedium}}
                />
              </Stack>
              <Stack
                direction={'row'}
                onClick={() => {
                  navigate(Navigate(Route.LOGIN));
                }}>
                <NGArrowLeft sx={{mr: 1, color: 'Primary.main'}} />
                <NGText
                  sx={{
                    ...FigmaCTA.CtaMedium,
                  }}
                  text={t(Localization('form', 'Back to the connection'))}
                />
              </Stack>
            </Stack>
          </Stack>
        </Stack>
      </Grid>
      <Grid
        item
        xs={false}
        sm={false}
        md={7}
        sx={{
          backgroundColor: '#FAFBFE',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          position: 'relative',
        }}>
        <NgSvgBackground resource={bg} />
      </Grid>
    </Grid>
  );
}

export default PasswordModify;
