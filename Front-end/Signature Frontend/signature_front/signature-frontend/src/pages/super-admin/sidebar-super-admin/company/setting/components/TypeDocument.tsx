import React from 'react';
import {Checkbox, Stack} from '@mui/material';
import NGText from '@components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {useTranslation} from 'react-i18next';
import {useAppSelector} from '@/redux/config/hooks';

function TypeDocument() {
  const {t} = useTranslation();
  const {theme} = useAppSelector(state => state.enterprise);
  const [checked, setChecked] = React.useState<string[]>([]);
  const data = [
    {
      id: 0,
      title: 'pdf',
    },
    {
      id: 1,
      title: 'jpg',
    },
    {
      id: 2,
      title: 'png',
    },
  ];

  const handleChange = (id: string) => {
    const isHave = checked.find(item => item === id);
    setChecked([...checked, id]);
    if (isHave) {
      const newChecked = checked.filter(item => item !== id);
      setChecked(newChecked);
    }
  };
  return (
    <Stack spacing={'10px'}>
      {data.map(item => (
        <Stack
          sx={{cursor: 'pointer'}}
          onClick={() => handleChange(item.id.toString())}
          border={1}
          borderColor={
            checked.includes(item.id.toString())
              ? theme[0].mainColor + '99'
              : theme[0].mainColor + '20'
          }
          key={item.id}
          direction={'row'}
          alignItems={'center'}
          width={'296px'}
          height={'40px'}
          borderRadius={'6px'}
          p={'12px'}>
          <Checkbox
            sx={{
              color: checked.includes(item.id.toString())
                ? theme[0].mainColor + '99'
                : theme[0].mainColor + '20',
            }}
            checked={checked.includes(item.id.toString())}
          />
          <NGText
            text={t(Localization('setting', item.title as any))}
            myStyle={{...FigmaBody.BodySmall}}
          />
        </Stack>
      ))}
    </Stack>
  );
}

export default TypeDocument;
