import {Localization} from '@/i18n/lan';
import {FolderTemplateInterface} from '@/redux/slides/profile/template/templateSlide';
import {IconButton, Stack, Typography} from '@mui/material';
import {t} from 'i18next';

type ISideNav = {
  currentData: Array<FolderTemplateInterface>;
  activeFolder: number | null;
  setActiveFolder: React.Dispatch<React.SetStateAction<number | null>>;
};

const SideNav = (props: ISideNav) => {
  const {currentData, activeFolder, setActiveFolder} = props;

  return (
    <Stack
      p="24px 24px 24px 72px"
      gap="24px"
      height="100%"
      sx={{
        borderRight: '1px solid #E9E9E9',
      }}>
      <Typography
        sx={{
          fontSize: '14px',
          fontWeight: 600,
          fontFamily: 'Poppins',
        }}>
        {t(Localization('models-corporate', 'model-type'))}
      </Typography>
      <Stack gap="8px">
        <IconButton
          disableFocusRipple
          disableRipple
          disableTouchRipple
          sx={{
            p: 0,
          }}
          onClick={() => setActiveFolder(null)}>
          <Stack
            width="219px"
            direction="row"
            sx={{
              bgcolor: !activeFolder ? '#E9E9E9' : undefined,
              p: '6px',
              borderRadius: '6px',
            }}
            justifyContent="space-between"
            alignItems="center">
            <Typography
              sx={{
                fontSize: '12px',
                fontWeight: 500,
                fontFamily: 'Poppins',
                color: '#000000',
              }}>
              {t(Localization('models-corporate', 'all-model'))}
            </Typography>
            <Typography
              sx={{
                color: 'Primary.main',
                fontSize: '11px',
                fontWeight: 600,
                fontFamily: 'Poppins',
              }}>
              {currentData
                .map(item => item.countTemplates)
                .reduce((partialSum, a) => partialSum + a, 0)}
            </Typography>
          </Stack>
        </IconButton>
        {currentData.map(item => (
          <IconButton
            disableFocusRipple
            disableRipple
            disableTouchRipple
            sx={{
              p: 0,
            }}
            onClick={() => setActiveFolder(item.id)}
            key={item.id}>
            <Stack
              width="219px"
              direction="row"
              sx={{
                bgcolor: activeFolder === item.id ? '#E9E9E9' : undefined,
                p: '6px',
                borderRadius: '6px',
              }}
              justifyContent="space-between"
              alignItems="center">
              <Typography
                sx={{
                  fontSize: '12px',
                  fontWeight: 500,
                  fontFamily: 'Poppins',
                  color: '#000000',
                }}>
                {item.unitName}
              </Typography>
              <Typography
                sx={{
                  color: 'Primary.main',
                  fontSize: '11px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {item.countTemplates}
              </Typography>
            </Stack>
          </IconButton>
        ))}
      </Stack>
    </Stack>
  );
};

export default SideNav;
