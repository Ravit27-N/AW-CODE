import certignaLogo from '@/assets/image/LOGO.png';
import {
  colorBlack,
  colorDisable,
  colorWhite,
  StyleConstant,
} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {NGButton} from '@components/ng-button/NGButton';
import {
  initialLogoFileType,
  LogoFileType,
} from '@components/ng-dropzone/ng-dropzone-logo/NGDropzoneLogo';
import NGText from '@components/ng-text/NGText';
import {Backdrop, CircularProgress, Stack} from '@mui/material';
import {Box} from '@mui/system';
import {useMatchMedia} from '@wojtekmaj/react-hooks';
import React from 'react';
import {useTranslation} from 'react-i18next';

import {UNKOWNERROR} from '@/constant/NGContant';
import {useUpdateThemeSuperAdminMutation} from '@/redux/slides/corporate-admin/corporateUserSlide';
import LeftSideBrand from '@pages/super-admin/sidebar-super-admin/company/brand/leftSideBrand/LeftSideBrand';
import RightSideBrand from '@pages/super-admin/sidebar-super-admin/company/brand/RightSideBrand';
import {useSnackbar} from 'notistack';
import {useParams} from 'react-router-dom';

function Brand() {
  const isXl = useMatchMedia('(max-width:1440px)');
  const reduxTheme = useAppSelector(state => state.enterprise);
  const [upload, setUpload] = React.useState<LogoFileType>(initialLogoFileType);
  const [sourceFile, setSourceFile] = React.useState<any>(null);
  const {enqueueSnackbar, closeSnackbar} = useSnackbar();
  const {t} = useTranslation();
  const [updateCorporateThemeByAdmin, {isLoading}] =
    useUpdateThemeSuperAdminMutation({});
  const [alertMessage, setAlertMessage] = React.useState('');
  const {uuid} = useParams();

  React.useEffect(() => {
    setUpload({
      file: initialLogoFileType.file,
      preview: reduxTheme.companyProviderTheme.logo ?? certignaLogo,
    });
  }, [reduxTheme.companyProviderTheme.logo]);
  // handle for set theme
  const setTheme = async () => {
    const formData = new FormData();
    formData.append(
      'logoFile',
      upload.file ?? (reduxTheme.companyProviderTheme.logo as any),
    );
    formData.append(
      'mainColor',
      reduxTheme.companyProviderTheme.mainColor as string,
    );
    formData.append(
      'secondaryColor',
      reduxTheme.theme[0].secondaryColor as string,
    );
    formData.append('linkColor', reduxTheme.theme[0].linkColor as string);
    formData.append('companyUuid', uuid as string);
    try {
      const res = await updateCorporateThemeByAdmin(formData).unwrap();
      enqueueSnackbar(
        t(Localization('corporate-form', 'your-changes-have-been-saved')),
        {
          variant: 'successSnackbar',
        },
      );
      if (res) {
        setTimeout(() => {
          window.location.reload();
        }, 1500);
      }
    } catch (error: any) {
      enqueueSnackbar(error ? error.message : UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };
  React.useEffect(() => {
    return () => {
      closeSnackbar();
    };
  }, []);

  return (
    <Box width={'100%'} height={'100%'}>
      <Stack
        width={'100%'}
        direction={'row'}
        justifyContent={'space-between'}
        borderBottom={2}
        py={'8px'}
        px={'20px'}
        borderColor={'bg.main'}
        alignItems={'center'}>
        <NGText text={'Marque'} myStyle={{fontSize: 16, fontWeight: 600}} />
        <NGButton
          title={
            <NGText
              text={t(Localization('enterprise-brand', 'save-change'))}
              myStyle={{color: 'white', fontSize: 12}}
            />
          }
          onClick={async () => {
            await setTheme();
          }}
          disabled={alertMessage !== ''}
          myStyle={{
            bgcolor: 'Primary.main',
            borderRadius: '6px',
            py: 1,
            '&.MuiButton-contained': {
              fontWeight: 600,
            },
            '&.Mui-disabled': {
              bgcolor: colorDisable,
              color: colorWhite,
            },
            '&:hover': {
              bgcolor: colorBlack,
            },
          }}
        />
      </Stack>
      <Stack
        direction={'row'}
        justifyContent={isXl ? 'space-around' : 'space-between'}
        width="100%"
        height="100%"
        flexWrap={'wrap'}
        sx={{
          overflowY: 'auto',
          overflowX: 'hidden',
          ...StyleConstant.scrollNormal,
        }}>
        <LeftSideBrand
          alertMessage={alertMessage} // alert message error message
          setAlertMessage={setAlertMessage} // set alert message error message
          setSourceFile={setSourceFile} // set file for view
          theme={reduxTheme.theme[0]}
          upload={upload} // upload file by form data
          setUpload={setUpload} // set upload file by form data
          isXl={isXl}
        />
        <RightSideBrand
          sourceFile={sourceFile} // source file for view in right side
          upload={upload} // upload file by form data
          isXl={isXl}
          companyDetail={reduxTheme}
        />
      </Stack>
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={isLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Box>
  );
}

export default Brand;
