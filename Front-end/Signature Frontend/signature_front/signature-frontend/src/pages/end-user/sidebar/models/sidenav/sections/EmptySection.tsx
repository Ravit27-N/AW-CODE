import {NGCirclePlus} from '@/assets/iconExport/Allicon';
import {Localization} from '@/i18n/lan';
import img from '@assets/image/illu-placeholder.png';
import {Button, Stack, Typography} from '@mui/material';
import {t} from 'i18next';
export type IEmptySection = {
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

const EmptySection = (props: IEmptySection) => {
  const {setTrigger} = props;
  return (
    <Stack
      gap="28px"
      alignItems="center"
      height={`calc(100vh - 221px)`}
      justifyContent="center">
      {/* <NGModelEmptySection /> */}
      <img src={img} width="159px" height="127px" />
      <Stack>
        <Typography
          sx={{
            fontWeight: 500,
            fontSize: 20,
            fontFamily: 'Poppins',
          }}>
          {t(Localization('models-corporate', 'empty-model'))}
        </Typography>
        <Typography
          sx={{
            fontWeight: 400,
            fontSize: 14,
            fontFamily: 'Poppins',
            color: '#676767',
          }}>
          {t(Localization('models-corporate', 'obvious-template'))}
        </Typography>
      </Stack>

      <Button
        onClick={() => setTrigger(true)}
        startIcon={
          <NGCirclePlus
            sx={{
              mt: '-1px',
            }}
          />
        }
        variant="contained"
        sx={{
          p: '8px 16px',
          textTransform: 'none',
          fontFamily: 'Poppins',
          fontSize: 11,
        }}>
        {t(Localization('models-corporate', 'create-model'))}
      </Button>
    </Stack>
  );
};

export default EmptySection;
