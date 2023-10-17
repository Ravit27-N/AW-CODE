import {TypeBoxInput, TypeHeaderEmail, TypeText} from './type';
import {useTranslation} from 'react-i18next';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Divider, Radio} from '@mui/material';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import Stack from '@mui/material/Stack';
import FormControlLabel from '@mui/material/FormControlLabel';
import {Box} from '@mui/system';
import {Center, VStack} from '@/theme';
import NGText from '@components/ng-text/NGText';

export const Text_Input = ({
  text,
  input,
  box,
  isInput = true,
  secondText,
  haveUnderline = true,
}: TypeText) => {
  const {t} = useTranslation();
  return (
    <Stack spacing={0.2}>
      <NGText
        text={t(text)}
        myStyle={{
          ...StyleConstant.textBold,
          ...StyleConstant.locate.left,
          fontSize: 14,
          fontWeight: 600,
          // color: 'red',
        }}
      />
      {!isInput && secondText}

      {isInput ? input : box}

      {!haveUnderline ? <Divider sx={{width: '100%'}} /> : undefined}
    </Stack>
  );
};
export const StyleSecondText = {
  ...StyleConstant.textBold,
  ...StyleConstant.locate.left,
  fontSize: 12,
  fontWeight: 400,
  color: 'black',
};

export const StyleMainTitle = {
  ...StyleConstant.textBold,
  ...StyleConstant.locate.left,
  fontSize: 14,
  fontWeight: 600,
  py: 3,
};
export const Box_Input = ({
  icon = <ImageOutlinedIcon sx={{fontSize: '30px'}} />,
  title,
  text,
  checked = false,
  onClick,
  haveIcon = true,
  textStyle,
  isDisable = false,
  padding = 0,
  radioColor = 'Primary.main',
  borderColorBox = 'Primary.main',
}: TypeBoxInput) => {
  const handleBorderColor = (isDisable: boolean) => {
    if (isDisable) {
      return 'bg.main';
    } else if (checked) {
      return borderColorBox;
    } else {
      return 'bg.main';
    }
  };
  return (
    <Stack
      onClick={e => {
        !isDisable && onClick!(e);
      }}
      direction={'row'}
      spacing={1}
      py={padding}
      border={1}
      borderColor={handleBorderColor(isDisable)}
      justifyContent={'flex-start'}
      sx={{cursor: 'pointer', opacity: isDisable ? 0.5 : 1}}
      borderRadius={2}>
      <FormControlLabel
        value="female"
        control={<Radio />}
        disabled={isDisable}
        label={''}
        style={{margin: 0}}
        sx={{
          '& .MuiRadio-colorPrimary.Mui-checked': {
            color: isDisable ? 'grey' : radioColor,
            opacity: isDisable ? 0.5 : 1,
          },
        }}
        checked={checked}
      />
      {haveIcon && (
        <Box
          bgcolor={'blue.light'}
          width={'40px'}
          height={'40px'}
          p={1}
          borderRadius={'50%'}>
          <Center sx={{height: '100%'}}>{icon}</Center>
        </Box>
      )}
      <VStack sx={{alignItems: 'flex-start'}}>
        <NGText
          text={title}
          myStyle={{...StyleConstant.textBold, fontSize: 12, fontWeight: 400}}
        />
        <NGText
          text={text}
          myStyle={{
            ...StyleConstant.textSmall,
            ...textStyle,
            color: 'text.disable',
            fontSize: 12,
            fontWeight: 400,
          }}
        />
      </VStack>
    </Stack>
  );
};

export function EmailHeader({option, position, email}: TypeHeaderEmail) {
  return (
    <Stack direction={'row'} spacing={0.5}>
      <NGText
        text={option}
        myStyle={{
          fontWeight: 400,
          fosntSize: 14,
          color: 'black.main',
        }}
      />
      <NGText
        text={': ' + position}
        myStyle={{fontWeight: 600, fosntSize: '14px', color: 'black.main'}}
      />
      <NGText
        text={email}
        myStyle={{
          fontWeight: 400,
          fosntSize: 14,
          color: '#525050',
        }}
      />
    </Stack>
  );
}
