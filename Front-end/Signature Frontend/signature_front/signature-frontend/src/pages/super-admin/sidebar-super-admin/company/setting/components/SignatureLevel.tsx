import React from 'react';
import {Checkbox, Stack} from '@mui/material';
import NGText from '@components/ng-text/NGText';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {Box} from '@mui/system';

import {useAppSelector} from '@/redux/config/hooks';

interface type {
  checked: string[];
  setChecked: React.Dispatch<React.SetStateAction<string[]>>;
}
function SignatureLevel({checked, setChecked}: type) {
  const data = [
    {
      id: 0,
      title: 'Signature simple',
      description:
        'L’utilisateur doit fournir le code OTP reçu sur son téléphone pour signer.',
    },
    {
      id: 1,
      title: 'Signature avancée',
      description:
        'L’utilisateur doit fournir le code OTP reçu sur son téléphone et une pièce d’identité valide pour signer.',
    },
    {
      id: 2,
      title: 'Signature qualifiée',
      description:
        'L’utilisateur doit fournir le code OTP reçu sur son téléphone et participer à un rendez-vous en face-à-face pour signer.',
    },
  ];
  const {theme} = useAppSelector(state => state.enterprise);

  const handleChange = (id: string) => {
    const isHave = checked.find(item => item === id);
    setChecked([...checked, id]);
    if (isHave) {
      const newChecked = checked.filter(item => item !== id);
      setChecked(newChecked);
    }
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
            onClick={() => handleChange(item.id.toString())}
            key={item.id}
            width={'330px'}
            height={'128px'}
            borderRadius={'6px'}
            p={'20px'}
            border={1.2}
            borderColor={
              checked.includes(item.id.toString())
                ? theme[0].mainColor + '99'
                : theme[0].mainColor + '20'
            }>
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
                  color: checked.includes(item.id.toString())
                    ? theme[0].mainColor + '99'
                    : theme[0].mainColor + '20',
                }}
                checked={checked.includes(item.id.toString())}
              />
            </Stack>
          </Box>
        ))}
      </Stack>
    </Stack>
  );
}

export default SignatureLevel;
