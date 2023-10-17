import {InputAdornment, TextField} from '@mui/material';
import {useState} from 'react';
import SearchIcon from '@mui/icons-material/Search';
import {useTranslation} from 'react-i18next';
import {Localization} from '@/i18n/lan';
import {FONT_TYPE} from '@/constant/NGContant';
import {pixelToRem} from '@/utils/common/pxToRem';
import {StyleConstant} from '@/constant/style/StyleConstant';
export default function NGSearch() {
  const {t} = useTranslation();
  const [searchTerm, setSearchTerm] = useState('');
  const handleChange = (event: any) => {
    setSearchTerm(event.target.value);
  };

  return (
    <TextField
      size={'small'}
      placeholder={
        t(
          Localization(
            'text',
            'Rechercher un document, un signataire, un contact',
          ),
        )!
      }
      type="search"
      value={searchTerm}
      onChange={handleChange}
      sx={{
        ...StyleConstant.inputStyleLogin,
        width: pixelToRem(500),
        height: pixelToRem(36),
        borderRadius: pixelToRem(6),
      }}
      InputProps={{
        startAdornment: (
          <InputAdornment position="end" sx={{mr: 2}}>
            <SearchIcon />
          </InputAdornment>
        ),
        sx: {fontFamily: FONT_TYPE.POPPINS, fontSize: 14},
      }}
    />
  );
}
