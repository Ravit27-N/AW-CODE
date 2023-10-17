import {NGFile} from '@/assets/iconExport/Allicon';
import {Localization} from '@/i18n/lan';
import bgLogo from '@assets/background/table-user-corporate/BgImage.svg';
import {Stack, Typography} from '@mui/material';
import {t} from 'i18next';

const ModelsHero = () => {
  return (
    <Stack
      p="40px 72px"
      gap="7px"
      sx={{
        backgroundImage: `url(${bgLogo})`,
        backgroundSize: 'cover',
        borderBottom: '1px solid #E9E9E9',
      }}>
      <Typography
        sx={{
          fontWeight: 600,
          fontSize: 27,
          lineHeight: '36px',
          fontFamily: 'Poppins',
        }}>
        {t(Localization('models-corporate', 'models'))}
      </Typography>
      <Stack direction="row">
        <NGFile
          sx={{
            color: 'Primary.main',
          }}
        />
        <Typography
          sx={{
            textTransform: 'lowercase',
            fontFamily: 'Poppins',
          }}>
          0 {t(Localization('models-corporate', 'models'))}
        </Typography>
      </Stack>
    </Stack>
  );
};

export default ModelsHero;
