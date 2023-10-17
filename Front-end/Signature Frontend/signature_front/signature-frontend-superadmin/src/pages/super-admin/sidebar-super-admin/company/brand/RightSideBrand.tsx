import {NGPlusIcon} from '@/assets/Icon';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {CorporateCompanyInterface} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import certignaLogo from '@assets/image/LOGO.png';
import {LogoFileType} from '@components/ng-dropzone/ng-dropzone-logo/NGDropzoneLogo';
import {InputBase, MenuItem, Select, Stack, Typography} from '@mui/material';
import {styled} from '@mui/material/styles';
import React from 'react';
import {useTranslation} from 'react-i18next';

const BootstrapInput = styled(InputBase)(({theme}) => ({
  '& .MuiInputBase-input': {
    border: '1px solid #E9E9E9',
    padding: '10px 26px 10px 12px',
    transition: theme.transitions.create(['border-color', 'box-shadow']),
    '&:focus': {
      borderColor: '#80bdff',
    },
  },
}));

const SKELETON_LENGTH = 9;

const RightSideBrand = ({
  sourceFile,
  isXl,
  upload,
  companyDetail,
}: {
  isXl: boolean | null;
  upload: LogoFileType;
  sourceFile: string;
  companyDetail: CorporateCompanyInterface;
}) => {
  const {t} = useTranslation();

  const corporateTheme = useAppSelector(
    state => state.enterprise,
  ).companyProviderTheme;

  const OPT_SELECT = [
    {
      key: 1,
      value: t(Localization('enterprise-brand', 'platform')),
    },
  ];
  const [state] = React.useState<{selectKey: number}>({
    selectKey: 1,
  });
  const handleChangeSelect = () => {
    return;
  };
  return (
    <Stack
      width={isXl ? '645px' : '60%'}
      height="755px"
      gap="36px"
      sx={{bgcolor: '#FAFAFA', p: '40px'}}>
      <Stack
        width="573px"
        height="48px"
        gap="12px"
        alignItems="center"
        justifyContent="center"
        direction="row">
        <NGText
          text={t(Localization('enterprise-brand', 'preview'))}
          myStyle={{
            width: '104px',
            height: '20px',
            fontSize: '13px',
            fontWeight: 500,
          }}
        />
        <Select
          sx={{
            '& .MuiInputBase-input': {
              width: '247px',
              gap: '8px',
              fontFamily: 'Poppins',
              padding: '8px 12px 8px 10px',
              border: '1px solid ' + corporateTheme.mainColor,
              fontSize: '14px',
              fontWeight: 500,
              borderColor: corporateTheme.mainColor,
              '&:focus': {
                borderColor: corporateTheme.mainColor,
              },
            },
          }}
          input={<BootstrapInput />}
          value={state.selectKey}
          onChange={handleChangeSelect}
          displayEmpty
          inputProps={{'aria-label': 'Without label'}}>
          {OPT_SELECT.map(item => (
            <MenuItem
              key={item.key}
              value={item.key}
              sx={{fontSize: '14px', fontWeight: 500, fontFamily: 'Poppins'}}>
              {item.value}
            </MenuItem>
          ))}
        </Select>
      </Stack>
      <Stack
        width="576px"
        height="334.88px"
        sx={{
          bgcolor: '#FFFFFF',
          border: '1px solid #E9E9E9',
          boxShadow: '0px 0px 12px rgba(0, 0, 0, 0.1)',
          borderRadius: '4.4px',
        }}>
        <Stack
          width="100%"
          height="39.12px"
          alignItems="center"
          direction={'row'}
          sx={{
            borderBottom: '1.11628px solid #E9E9E9',
            p: '11px 26.79px 10px 12.5px',
          }}>
          <Stack width="66px" height="21px" direction={'row'} gap="4px">
            {/*   If it has file upload preview it if not preview file get form backend.*/}
            {upload.preview.length > 0 ? (
              <img
                src={upload.preview}
                width="auto"
                height="21px"
                alt={'Logo'}
              />
            ) : (
              <img src={certignaLogo} alt="logo" />
            )}
            {sourceFile && <NGText text={companyDetail.name} />}
          </Stack>
          <Typography
            sx={{
              ml: '66.62px',
              width: '246.7px',
              height: '17.86px',
              bgcolor: '#E9E9E9',
              mr: '33.5px',
            }}
          />
          <NGPlusIcon
            sx={{
              marginRight: '4px',
              color: corporateTheme.mainColor,
              pl: '7px',
              mt: '-1px',
            }}
          />
          {/* <img src={plusLogo} width="12.77px" style={{}} /> */}
          <NGText
            text={t(Localization('enterprise-brand', 'new-project'))}
            sx={{fontSize: '10px', fontWeight: 600, mr: '10px'}}
            width="90px"
            height="16px"
          />
          <Typography
            sx={{
              width: '22.33px',
              height: '20.33px',
              bgcolor: '#E9E9E9',
              borderRadius: '28px',
            }}
          />
        </Stack>

        <Stack direction="row">
          <Stack
            width="50.7px"
            height="296.77px"
            gap="26.79px"
            sx={{
              alignItems: 'center',
              borderRight: '1.11628px solid #E9E9E9',
              p: '22px 0px 0px 0px',
              position: 'relative',
            }}>
            <Stack justifyContent={'center'} gap="17.86px">
              <Typography
                sx={{
                  width: '22.33px',
                  height: '22.33px',
                  bgcolor: corporateTheme.mainColor,
                  borderRadius: '28px',
                }}
              />
              <Typography
                sx={{
                  width: '22.33px',
                  height: '22.33px',
                  bgcolor: '#E9E9E9',
                  borderRadius: '28px',
                }}
              />
              <Typography
                sx={{
                  width: '22.33px',
                  height: '22.33px',
                  bgcolor: '#E9E9E9',
                  borderRadius: '28px',
                }}
              />
            </Stack>
            <Typography
              sx={{
                top: '16.7px',
                right: 0,
                position: 'absolute',
                width: '1.12px',
                height: '36.84px',
                bgcolor: corporateTheme.mainColor,
                borderRadius: '28px',
              }}
            />
          </Stack>
          <Stack
            justifyContent={'center'}
            alignItems="center"
            gap="9px"
            sx={{px: '26.79px'}}>
            {Array.from({length: SKELETON_LENGTH}, (item, index: number) => (
              <Typography
                key={index}
                sx={{
                  width: '467.72px',
                  height: '17.86px',
                  bgcolor: '#E9E9E9',
                }}
              />
            ))}
          </Stack>
        </Stack>
      </Stack>
    </Stack>
  );
};

export default RightSideBrand;
