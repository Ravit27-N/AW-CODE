import React from 'react';
import {Checkbox, Stack} from '@mui/material';
import NGText from '@components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {useTranslation} from 'react-i18next';
import {useAppSelector} from '@/redux/config/hooks';

function TypeDocument({
  setCheck,
  data = [
    {
      id: 'pdf',
      title: 'pdf',
    },
    {
      id: 'jpg',
      title: 'jpg',
    },
    {
      id: 'png',
      title: 'png',
    },
  ],
}: {
  setCheck?: any;
  data?: {id: string; title: string}[];
}) {
  const {t} = useTranslation();
  const {theme} = useAppSelector(state => state.enterprise);
  const [checked, setChecked] = React.useState<string[]>([data[0].id]);

  const handleChange = (id: string) => {
    const isHave = checked.find(item => item === id);
    setChecked([...checked, id]);
    if (isHave) {
      const newChecked = checked.filter(item => item !== id);
      setChecked(newChecked);
    }
  };
  React.useEffect(() => {
    setCheck(checked);
  }, [checked]);

  return (
    <Stack gap={'10px'} direction={'row'} flexWrap={'wrap'}>
      {data.map(item => (
        <Stack
          sx={{cursor: 'pointer'}}
          onClick={e => {
            handleChange(item.id.toString());
          }}
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
            // disabled={!active.includes(item.id)}
            sx={{
              color: checked.includes(item.id.toString())
                ? theme[0].mainColor + '99'
                : theme[0].mainColor + '20',
            }}
            checked={checked.includes(item.id.toString())}
          />
          <NGText
            text={t(Localization('setting', item.title.toLowerCase() as any))}
            myStyle={{...FigmaBody.BodySmall}}
          />
        </Stack>
      ))}
    </Stack>
  );
}

export default TypeDocument;
