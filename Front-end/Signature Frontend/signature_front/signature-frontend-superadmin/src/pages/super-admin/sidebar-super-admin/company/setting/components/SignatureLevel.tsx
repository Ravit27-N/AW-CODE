import React from 'react';
import {Box} from '@mui/system';
import {Checkbox, Stack} from '@mui/material';
import {useTheme} from '@mui/material/styles';
import NGText from '@components/ng-text/NGText';
import {SignatureLevelType} from '@constant/NGContant';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {ISignatureLevels} from '@pages/super-admin/sidebar-super-admin/company/setting/SettingSuper';

interface IType {
  checked: (keyof ISignatureLevels)[];
  setChecked: React.Dispatch<React.SetStateAction<(keyof ISignatureLevels)[]>>;
}

function SignatureLevel({checked, setChecked}: IType) {
  const data = [
    {
      id: SignatureLevelType.simple,
      title: 'Signature simple',
      description:
        'L’utilisateur doit fournir le code OTP reçu sur son téléphone pour signer.',
    },
    {
      id: SignatureLevelType.advance,
      title: 'Signature avancée',
      description:
        'L’utilisateur doit fournir le code OTP reçu sur son téléphone et une pièce d’identité valide pour signer.',
    },
    {
      id: SignatureLevelType.qualify,
      title: 'Signature qualifiée',
      description:
        'L’utilisateur doit fournir le code OTP reçu sur son téléphone et participer à un rendez-vous en face-à-face pour signer.',
    },
  ];

  const theme = useTheme();
  const handleChange = (
    id:
      | SignatureLevelType.qualify
      | SignatureLevelType.advance
      | SignatureLevelType.simple,
  ) => {
    const isHave = checked.find(item => item === id);
    if (!isHave) {
      setChecked([...checked, id]);
    }
    if (isHave && checked.length > 1) {
      const newChecked = checked.filter(item => item !== id);
      setChecked(newChecked);
    }
  };

  const getBorderColor = (id: SignatureLevelType): string => {
    const temp = theme.palette.primary.main;
    return checked.includes(id) ? `${temp}99` : `${temp}20`;
  };

  return (
    <Stack
      width={'1093px'}
      height={'240px'}
      p={'20px'}
      gap={'16px'}
      borderColor={'Light.main'}
      border={'0px 0px 1px 0px'}>
      <Stack spacing={'4px'}>
        <NGText
          text={'Niveaux de signature'}
          myStyle={{...FigmaBody.BodyLageBold}}
        />
        <NGText
          text={
            'Définissez les niveaux de signature autorisés pour l’entreprise.'
          }
          myStyle={{...FigmaBody.BodyMedium}}
        />
      </Stack>
      <Stack
        direction={'row'}
        spacing={'12px'}
        width={'1013px'}
        height={'128px'}>
        {data.map(item => (
          <Box
            sx={{cursor: 'pointer'}}
            onClick={() => handleChange(item.id)}
            key={item.id}
            width={'330px'}
            height={'128px'}
            borderRadius={'6px'}
            p={'20px'}
            border={1.2}
            borderColor={getBorderColor(item.id)}>
            <Stack
              width={'100%'}
              spacing={'5px'}
              height={'76px'}
              gap={'4px'}
              alignItems={'center'}
              justifyContent={'space-between'}
              direction={'row'}>
              <Stack>
                <NGText
                  text={item.title}
                  myStyle={{...FigmaBody.BodyMediumBold}}
                />
                <NGText
                  text={item.description}
                  myStyle={{...FigmaBody.BodySmallLight}}
                />
              </Stack>
              <Checkbox
                sx={{
                  color: getBorderColor(item.id),
                }}
                checked={checked.includes(item.id)}
              />
            </Stack>
          </Box>
        ))}
      </Stack>
    </Stack>
  );
}

export default SignatureLevel;
