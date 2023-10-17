import {NGBurgerMenu, NGView} from '@/assets/Icon';
import {NGCirclePlus} from '@/assets/iconExport/Allicon';
import NGGroupAvatar from '@/components/ng-group-avatar/NGGroupAvatar';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {pixelToRem} from '@/utils/common/pxToRem';
import {
  Button,
  InputAdornment,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  TextField,
} from '@mui/material';
import {GridSearchIcon} from '@mui/x-data-grid';
import {t} from 'i18next';
import React from 'react';
import CreateModel from '../../form/CreateModel';

type ITableSection = {
  activeFolder: number | null;
};

const TableSection = (props: ITableSection) => {
  const [search, setSearch] = React.useState<string>('');
  const [dTable] = React.useState<Array<DTable>>(
    Array.from<any, DTable>({length: 20}, (_, index) => ({
      id: index.toString(),
      name: 'A',
      category: 'category',
      createdBy: 'createdBy',
      createdOn: 'cratedOn',
      signatories: 5,
    })),
  );
  const handleSearch = (
    e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => {
    setSearch(e.target.value);
  };
  return (
    <Stack p="0 24px 40px 24px">
      <Tittle search={search} handleSearch={handleSearch} />
      <TableContainer
        sx={{
          height: `calc(100vh - 300px)`,
          '&::-webkit-scrollbar': {
            width: '0.1em',
          },

          '&::-webkit-scrollbar-thumb': {
            backgroundColor: 'grey',
          },
        }}>
        <Table aria-label="sticky table" size={'small'} stickyHeader>
          <ModelTableHead />

          <TableBody>
            {dTable.map(item => {
              return (
                <TableRow
                  hover
                  key={item.id}
                  tabIndex={-1}
                  sx={{
                    '&.MuiTableRow-root': {
                      height: '48px',
                    },
                    cursor: 'pointer',
                  }}>
                  <TableCell
                    component="th"
                    sx={{
                      px: '16px',
                    }}
                    id={item.id}
                    scope="row"
                    padding="none">
                    <NGText
                      text={item.name}
                      myStyle={{
                        fontSize: '11px',
                        fontWeight: 500,
                      }}
                    />
                  </TableCell>
                  <TableCell
                    component="th"
                    sx={{
                      px: '16px',
                    }}
                    id={item.id}
                    scope="row"
                    padding="none">
                    <NGText
                      text={item.category}
                      myStyle={{
                        fontSize: '11px',
                      }}
                    />
                  </TableCell>
                  <TableCell
                    component="th"
                    sx={{
                      px: '16px',
                    }}
                    id={item.id}
                    scope="row"
                    padding="none">
                    <NGText
                      text={`${item.signatories} ${t(
                        Localization('models-corporate', 'signatories'),
                      )}`}
                      myStyle={{
                        fontSize: '11px',
                      }}
                    />
                  </TableCell>
                  <TableCell
                    component="th"
                    sx={{
                      px: '16px',
                    }}
                    id={item.id}
                    scope="row"
                    padding="none">
                    <Stack direction={'row'} alignItems="center" gap="10px">
                      <NGGroupAvatar character={['AB']} />
                      <NGText
                        text={'Alberto'}
                        myStyle={{
                          fontSize: '11px',
                        }}
                      />
                    </Stack>
                  </TableCell>
                  <TableCell
                    component="th"
                    sx={{
                      px: '16px',
                    }}
                    id={item.id}
                    scope="row"
                    padding="none">
                    <NGText
                      text={'10/02/2023'}
                      myStyle={{
                        fontSize: '11px',
                      }}
                    />
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
    </Stack>
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
  createdBy: string;
  createdOn: string;
};

const Tittle = (props: ITittle) => {
  const {search, handleSearch} = props;
  const [trigger, setTrigger] = React.useState<boolean>(false);

  return (
    <Stack
      direction="row"
      alignItems="center"
      justifyContent="space-between"
      sx={{
        p: '24px 0px 20px 0px',
      }}>
      <TextField
        size={'small'}
        placeholder={t(Localization('models-corporate', 'search-for-model'))!}
        sx={{
          width: '390px',
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
      <Stack direction="row" gap="10px" alignItems="center">
        <NGBurgerMenu sx={{color: 'Primary.main', mt: '5px'}} />
        <NGView sx={{mt: '2px'}} />
        <Button
          onClick={() => setTrigger(true)}
          startIcon={
            <NGCirclePlus
              sx={{
                mt: '-3px',
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
      <CreateModel trigger={trigger} setTrigger={setTrigger} />
    </Stack>
  );
};

type ITableHeadId =
  | 'model-name'
  | 'category'
  | 'signatories'
  | 'created-by'
  | 'created-on'
  | 'actions';

type ITableHead = {
  id: ITableHeadId;
  numeric: boolean;
  label: string;
  disablePadding: boolean;
};

const ModelTableHead = () => {
  const headCells: readonly ITableHead[] = [
    {
      id: 'model-name',
      numeric: false,
      disablePadding: true,
      label: t(Localization('models-corporate', 'model-name')),
    },
    {
      id: 'category',
      numeric: true,
      disablePadding: false,
      label: t(Localization('models-corporate', 'category')),
    },
    {
      id: 'signatories',
      numeric: true,
      disablePadding: false,
      label: t(Localization('models-corporate', 'signatories')),
    },
    {
      id: 'created-by',
      numeric: true,
      disablePadding: false,
      label: t(Localization('models-corporate', 'created-by')),
    },
    {
      id: 'created-on',
      numeric: true,
      disablePadding: false,
      label: t(Localization('models-corporate', 'created-on')),
    },
    {
      id: 'actions',
      numeric: true,
      disablePadding: false,
      label: t(Localization('models-corporate', 'actions')),
    },
  ];
  return (
    <TableHead>
      <TableRow style={{height: '48px'}}>
        {headCells.map(headCell => (
          <TableCell
            // width={xl ? '300px' : '210px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            // sortDirection={orderBy === headCell.id ? order : false}
          >
            <TableSortLabel
              // hideSortIcon={headCell.id !== sortService.sortField}
              direction={'desc'}
              // IconComponent={() =>
              //   sortService.sortDirection === 'asc' ? (
              //     <ArrowDropDownIcon sx={{fontSize: '15px'}} />
              //   ) : (
              //     <ArrowDropUpIcon sx={{fontSize: '15px'}} />
              //   )
              // }
            >
              <NGText
                text={headCell.label}
                myStyle={{
                  fontSize: pixelToRem(12),
                  lineHeight: pixelToRem(24),
                  fontWeight: 500,
                }}
              />
            </TableSortLabel>
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
};

export default TableSection;
