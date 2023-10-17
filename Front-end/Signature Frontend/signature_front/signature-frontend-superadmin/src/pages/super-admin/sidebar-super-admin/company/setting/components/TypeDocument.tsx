import {Localization} from '@/i18n/lan';
import NGText from '@components/ng-text/NGText';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {Checkbox, Stack} from '@mui/material';
import {useTheme} from '@mui/material/styles';
import {useTranslation} from 'react-i18next';
interface ITypeDocument {
  checked: string[];
  setChecked: any;
}
function TypeDocument({checked, setChecked}: ITypeDocument) {
  const {t} = useTranslation();
  const theme = useTheme();
  const data = [
    {
      id: 'PDF',
      title: 'pdf',
    },
    {
      id: 'JPG',
      title: 'jpg',
    },
    {
      id: 'PNG',
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

  const getBorderColor = (id: string): string => {
    const temp = theme.palette.primary.main;
    return checked.includes(id) ? `${temp}99` : `${temp}20`;
  };

  return (
    <Stack spacing={'10px'}>
      {data.map(item => (
        <Stack
          sx={{cursor: 'pointer'}}
          onClick={() => handleChange(item.id.toString())}
          border={1}
          borderColor={getBorderColor(item.id.toString())}
          key={item.id}
          direction={'row'}
          alignItems={'center'}
          width={'296px'}
          height={'40px'}
          borderRadius={'6px'}
          p={'12px'}>
          <Checkbox
            sx={{
              color: getBorderColor(item.id.toString()),
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
