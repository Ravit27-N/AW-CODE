import {Box, Stack} from '@mui/material';
import {NGButton} from '@components/ng-button/NGButton';
import NGText from '@components/ng-text/NGText';
import {Center, HStack} from '@/theme';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {useTranslation} from 'react-i18next';
import {Localization} from '@/i18n/lan';
import {NGArrowLeft} from '@assets/iconExport/ExportIcon';
import {StyleSecondText} from '@pages/form/process-upload/envoi/function';
import Avatar from '@mui/material/Avatar';
import {useAppSelector} from '@/redux/config/hooks';
import {pixelToRem} from '@/utils/common/pxToRem';
import {Navigate} from '@/utils/common';
import {Route} from '@/constant/Route';
import {useNavigate} from 'react-router-dom';
export function ProjectLeftSide({data}: {data?: any}) {
  const {username} = useAppSelector(state => state.authentication.user);
  const {t} = useTranslation();
  const navigate = useNavigate();
  return (
    <Box height={'100%'} width={'100%'}>
      <Center
        sx={{
          height: '100%',
          width: '100%',
          justifyContent: 'flex-start',
          alignItems: 'flex-start',
        }}>
        <Stack height={'100%'} spacing={0.8}>
          <HStack sx={{width: '100%', justifyContent: 'flex-start'}}>
            <NGButton
              title={
                <NGText
                  text={t(Localization('project', 'projects'))}
                  myStyle={{
                    ...StyleSecondText,
                    color: '#000000',
                    fontWeight: 600,
                    fontSize: pixelToRem(11),
                    lineHeight: pixelToRem(20),
                  }}
                />
              }
              onClick={() => navigate(Navigate(Route.project.projectDetail))}
              variant={'text'}
              icon={<NGArrowLeft sx={{color: 'Primary.main'}} />}
            />
          </HStack>
          <NGText
            text={data.name}
            myStyle={{
              ...StyleConstant.textBold,
              fontSize: pixelToRem(31),
              fontWeight: 600,
              lineHeight: pixelToRem(40),
              // fontFamily: FONT_TYPE.POPPINS,
            }}
            color={'black.main'}
          />

          <Stack
            direction={'row'}
            justifyContent={'left'}
            spacing={1.2}
            alignItems={'center'}>
            <NGText
              text={t(Localization('project', 'created-by'))}
              myStyle={{
                ...StyleConstant.textSmall,
                fontSize: pixelToRem(14),
                color: 'black.main',
                fontWeight: 400,
                lineHeight: pixelToRem(24),
                // fontFamily: FONT_TYPE.POPPINS,
              }}
            />
            <Avatar
              sx={{
                bgcolor: 'blue.light',
                fontSize: {md: 12, lg: 12},
                width: pixelToRem(36),
                height: pixelToRem(36),
              }}>
              <NGText
                font={'poppins'}
                text={`${username?.split(' ')[0].charAt(0)}${username
                  ?.split(' ')[1]
                  .charAt(0)}`}
                myStyle={{
                  color: 'blue.main',
                  fontWeight: 700,
                  fontSize: pixelToRem(13),
                  lineHeight: pixelToRem(20),
                }}
              />
            </Avatar>
            <NGText
              font={'poppins'}
              text={username + ' (Moi)'}
              myStyle={{
                ...StyleConstant.textSmall,
                fontSize: pixelToRem(14),
                fontWeight: 600,
                color: 'black.main',
                lineHeight: pixelToRem(24),
                // fontFamily: FONT_TYPE.POPPINS,
              }}
            />
          </Stack>
        </Stack>
      </Center>
    </Box>
  );
}
