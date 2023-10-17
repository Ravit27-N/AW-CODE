import {Localization} from '@/i18n/lan';
import img from '@assets/image/illu-placeholder.png';
import {Stack, Typography} from '@mui/material';
import {t} from 'i18next';

const EmptySection = () => {
  return (
    <Stack
      gap="28px"
      alignItems="center"
      height="calc(100vh - 221px)"
      justifyContent="center">
      {/* <NGModelEmptySection /> */}
      <img src={img} width="159px" height="127px" />
      <Stack alignItems="center">
        <Typography
          sx={{
            fontWeight: 500,
            fontSize: 20,
            fontFamily: 'Poppins',
          }}>
          {t(Localization('end-user-assigned-project', 'no-project-assigned'))}
        </Typography>
        <Typography
          sx={{
            fontWeight: 400,
            fontSize: 14,
            fontFamily: 'Poppins',
            color: '#676767',
          }}>
          {t(
            Localization('end-user-assigned-project', 'obviously-all-project'),
          )}
        </Typography>
      </Stack>
    </Stack>
  );
};

export default EmptySection;
