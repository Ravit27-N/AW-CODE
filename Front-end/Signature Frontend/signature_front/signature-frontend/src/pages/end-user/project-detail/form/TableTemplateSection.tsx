import {NGPlus} from '@/assets/Icon';
import {FONT_TYPE} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {storeSignatureTemplate} from '@/redux/slides/authentication/authenticationSlide';
import {TemplateInterface} from '@/redux/slides/profile/template/templateSlide';
import {
  Box,
  IconButton,
  InputAdornment,
  Paper,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import {GridSearchIcon} from '@mui/x-data-grid';
import {t} from 'i18next';
import React from 'react';
import {useOutletContext} from 'react-router-dom';

type ITableSection = {
  activeFolder: number | null;
  currentData: Array<TemplateInterface>;
  setSearch: React.Dispatch<React.SetStateAction<string>>;
  search: string;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

const TableTemplateSection = (props: ITableSection) => {
  const {currentData, search, setSearch, setOpen} = props;
  const [dTable, setDTable] = React.useState<Array<DTable>>([]);

  React.useMemo(() => {
    if (currentData) {
      const tArray: Array<DTable> = [];
      currentData.forEach(item => {
        const {id, createdAt, createdByFullName, name, signature, approval} =
          item;

        const nameSplit = createdByFullName.split(' ');

        return tArray.push({
          id: id.toString(),
          name,
          category: '',
          createdBy: nameSplit[0] + ' ' + (nameSplit[1] ?? ' '),
          createdOn: createdAt,
          signatories: signature,
          approvals: approval,
        } as DTable);
      });
      setDTable(tArray);
    }
  }, [currentData]);

  const handleSearch = (
    e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => {
    setSearch(e.target.value);
  };
  return (
    <Stack>
      <Tittle search={search} handleSearch={handleSearch} />
      <TableGridView
        data={dTable}
        setOpen={setOpen}
        search={search}
        currentData={currentData}
      />
    </Stack>
  );
};
type ITableGridView = {
  data: DTable[];
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
  search: string;
  currentData: TemplateInterface[];
};

const TableGridView = (props: ITableGridView) => {
  const {data, setOpen, search, currentData} = props;
  const {setPopup} = useOutletContext<{
    setPopup: React.Dispatch<React.SetStateAction<boolean>>;
  }>();
  return data ? (
    <Stack
      direction="row"
      flexWrap="wrap"
      rowGap="0px"
      sx={{
        p: '0px 24px',
        height: '350px',
        overflow: 'scroll',
        overflowX: 'hidden',
        // '&::-webkit-scrollbar': {
        //   width: '0.1em',
        // },
        '&::-webkit-scrollbar-thumb': {
          backgroundColor: 'grey',
        },
      }}>
      {data
        .filter(item =>
          search !== ''
            ? item.name.toLowerCase().includes(search.toLowerCase())
            : item,
        )
        .map((i, index: number) => (
          <Box
            key={i.id}
            sx={{
              display: 'flex',
              flexWrap: 'wrap',
              borderRadius: 0,
              '& > :not(style)': {
                m: 1,
                width: 182,
                height: 162,
              },
            }}>
            <Paper
              onClick={() => {
                store.dispatch(
                  storeSignatureTemplate({
                    template: currentData.find(ii => ii.id === Number(i.id))!,
                  }),
                );
                setOpen(false);
                setPopup(true);
              }}
              elevation={1}
              sx={{
                cursor: 'pointer',
                p: '16px',
                gap: '8px',
                '&.MuiPaper-root.MuiPaper-elevation': {
                  borderRadius: '6px',
                },
              }}>
              <Stack justifyContent="space-between" height="100%">
                <Stack direction="row" justifyContent="space-between">
                  <Typography
                    sx={{
                      fontFamily: 'Poppins',
                      fontSize: '8px',
                      fontWeight: 700,
                      border: '1px solid #000000',
                      p: '4px 8px',
                    }}>
                    {t(Localization('text', 'MODÈLE'))}
                  </Typography>

                  <IconButton sx={{p: 0}}>
                    <NGPlus
                      sx={{
                        color: 'Primary.main',
                        fontSize: '12px',
                      }}
                    />
                  </IconButton>
                </Stack>
                <Stack gap="4px">
                  <Typography
                    sx={{
                      fontFamily: 'Poppins',
                      fontSize: '14px',
                      fontWeight: 600,
                    }}>
                    {i.name}
                  </Typography>
                  <Typography sx={{fontFamily: 'Poppins', fontSize: '12px'}}>
                    {`${i.approvals} ${t(
                      Localization('text', 'approbateur.trice.s'),
                    )}`}
                  </Typography>
                  <Typography sx={{fontFamily: 'Poppins', fontSize: '12px'}}>
                    {`${i.signatories} ${t(
                      Localization('text', 'signataire.trice.s'),
                    )}`}
                  </Typography>
                </Stack>
              </Stack>
            </Paper>
          </Box>
        ))}
    </Stack>
  ) : (
    <></>
  );
};

type ITittle = {
  search: string;
  handleSearch: (
    e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => void;
};

type DTable = {
  id: string;
  name: string;
  category: string;
  signatories: number;
  approvals: number;
  createdBy: string;
  createdOn: number;
};

const Tittle = (props: ITittle) => {
  const {search, handleSearch} = props;

  return (
    <Stack justifyContent="center" gap="20px" p="0px 30px 24px 30px">
      <Typography
        sx={{
          fontWeight: 600,
          fontSize: '14px',
          fontFamily: FONT_TYPE,
        }}>
        {t(Localization('project-detail', 'start-with-project-template'))}
      </Typography>
      <TextField
        size={'small'}
        placeholder={t(Localization('models-corporate', 'search-for-model'))!}
        sx={{
          width: '800px',
          justifyContent: 'center',
          border: 'none',
        }}
        type="search"
        value={search}
        onChange={handleSearch}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start" sx={{mr: 2}}>
              <GridSearchIcon />
            </InputAdornment>
          ),
          sx: {
            fontSize: '12px',
            height: '36px',
          },
        }}
      />
    </Stack>
  );
};

export default TableTemplateSection;
