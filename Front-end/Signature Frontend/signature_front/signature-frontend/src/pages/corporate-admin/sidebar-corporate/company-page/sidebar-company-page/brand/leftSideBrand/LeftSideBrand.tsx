import {COLOR_THEME_ARRAY} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {CorporateSettingTheme} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {setCorporateActiveColor} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {NGThick} from '@assets/Icon';
import NGDropzoneLogo, {
  LogoFileType,
} from '@components/ng-dropzone/ng-dropzone-logo/NGDropzoneLogo';
import NGText from '@components/ng-text/NGText';
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  Divider,
  Stack,
  darken,
} from '@mui/material';
import React, {Dispatch} from 'react';
import {useTranslation} from 'react-i18next';

const LeftSideBrand = ({
  isXl,
  upload,
  setUpload,
  alertMessage,
  theme,
}: {
  isXl: boolean | null;
  upload: LogoFileType;
  setUpload: Dispatch<React.SetStateAction<LogoFileType>>;
  setSourceFile: Dispatch<React.SetStateAction<string>>;
  alertMessage: string;
  setAlertMessage: Dispatch<React.SetStateAction<string>>;
  theme: CorporateSettingTheme;
}) => {
  // for translation
  const {t} = useTranslation();
  // activeColor when we click for change color
  const reduxTheme = useAppSelector(state => state.enterprise);
  const dispatch = useAppDispatch();
  const [confirmReset, setConfirmReset] = React.useState(false);
  // handle for change color when we click on circle color
  const handleChangeColor = (color: string) => {
    dispatch(
      setCorporateActiveColor({
        colors: {
          mainColor: color,
          secondaryColor: color,
          linkColor: color,
        },
      }),
    );
  };

  // Handle close dialog confirm reset theme
  const handleClose = () => {
    setConfirmReset(false);
  };
  return (
    <Stack
      width={isXl ? '440px' : '40%'}
      height="755px"
      sx={{p: '40px', borderRight: '1px solid #E9E9E9', mb: '20px'}}>
      {/* it is loading until get corporate finish*/}

      <Stack width={'360px'} gap={'32px'}>
        <Stack>
          <NGText
            text={t(Localization('enterprise-brand', 'your-brand'))}
            myStyle={{
              fontSize: '22px',
              fontWeight: 600,
            }}
          />
          <NGText
            text={t(Localization('enterprise-brand', 'personal-organize'))}
            myStyle={{
              fontSize: '14px',
              fontWeight: 400,
            }}
          />
        </Stack>

        <Divider sx={{borderBottomWidth: 2}} />

        <Stack width="360px" gap="20px">
          <Stack gap={'8px'}>
            <NGText
              text="Logo"
              myStyle={{
                fontSize: '14px',
                fontWeight: 600,
                lineHeight: '24px',
                width: '34px',
                height: '24px',
              }}
            />
            {/* <PreviewLogo
              setAlertMessage={setAlertMessage}
              setSourceFile={setSourceFile}
              logo={theme.logo ?? ''}
              setUpload={setUpload}
              upload={upload}
            />*/}

            <NGDropzoneLogo setLogo={setUpload} logo={upload} />
          </Stack>
          {alertMessage && (
            <Alert
              variant="filled"
              severity="error"
              sx={{position: 'absolute', bottom: 0}}>
              {alertMessage}
            </Alert>
          )}
        </Stack>

        <Divider sx={{borderBottomWidth: 2}} />

        <Stack gap={'18px'} width="360px" height="108px">
          <NGText
            text={t(Localization('enterprise-brand', 'color-theme'))}
            myStyle={{
              width: '172px',
              height: '24px',
              fontSize: '14px',
              fontWeight: 600,
            }}
          />
          <Stack
            direction={'row'}
            gap={'20px'}
            flexWrap={'wrap'}
            alignItems={'center'}>
            {COLOR_THEME_ARRAY.map((item: string) => (
              <Stack
                alignItems="center"
                justifyContent="center"
                width="32px"
                height="32px"
                key={item}
                sx={{
                  border:
                    reduxTheme.theme[0].mainColor === item
                      ? `0.5px solid ${item}`
                      : undefined,
                  borderRadius: '28px',
                }}>
                <ColorTheme
                  c={item}
                  handleChange={handleChangeColor}
                  active={reduxTheme.theme[0].mainColor === item}
                />
              </Stack>
            ))}
            <Button
              sx={{
                minHeight: 0,
                minWidth: 0,
                p: 0,
                width: '24px',
                height: '24px',
                bgcolor: '#ffffff',
                color: '#000000',
                borderRadius: '28px',
              }}
              variant="contained">
              +
            </Button>
          </Stack>
        </Stack>

        <Divider sx={{borderBottomWidth: 2}} />

        <Button
          onClick={async () => {
            setConfirmReset(true);
          }}
          sx={{
            minHeight: 0,
            minWidth: 0,
            p: 0,
            width: '162px',
            height: '40px',
            bgcolor: '#ffffff',
            border: `1px solid ${theme.mainColor}`,
            color: '#000000',
            borderRadius: '6px',
            textTransform: 'none',
            fontSize: '11px',
            fontWeight: 600,
          }}
          variant="outlined">
          {t(Localization('enterprise-brand', 'reset-to-default'))}
        </Button>
      </Stack>

      <Dialog
        open={confirmReset}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description">
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            {t(Localization('corporate-form', 'resetMessage'))}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            autoFocus
            sx={{textTransform: 'none'}}
            onClick={() => setConfirmReset(false)}>
            {t(Localization('pdf-edit', 'cancel'))}
          </Button>
          <Button
            onClick={() => window.location.reload()}
            autoFocus
            sx={{textTransform: 'none', color: theme.mainColor}}>
            {t(Localization('pdf-edit', 'confirm'))}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
};

const ColorTheme = ({
  c,
  active = false,
  handleChange,
}: {
  c: string;
  active?: boolean;
  handleChange?: (c: string) => void;
}) => {
  return (
    <Button
      onClick={() => handleChange!(c)}
      sx={{
        minHeight: 0,
        minWidth: 0,
        p: 0,
        width: '24px',
        height: '24px',
        bgcolor: c,
        borderRadius: '28px',
        '&:hover': {
          bgcolor: darken(c, 0.3),
        },
      }}
      variant="contained">
      {active && <NGThick sx={{pl: '5px'}} />}
    </Button>
  );
};

export default LeftSideBrand;
