import {NGBurgerMenu, NGPlus, NGView} from '@/assets/Icon';
import {NGCirclePlus} from '@/assets/iconExport/Allicon';
import NGGroupAvatar from '@/components/ng-group-avatar/NGGroupAvatar';
import NGText from '@/components/ng-text/NGText';
import {SIGNING_PROCESS, Sort} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppDispatch} from '@/redux/config/hooks';
import {
  storeCreateModel,
  storeModel,
} from '@/redux/slides/authentication/authenticationSlide';
import {
  FolderTemplateInterface,
  TemplateInterface,
  useGetTemplateByIdQuery,
} from '@/redux/slides/profile/template/templateSlide';
import {pixelToRem} from '@/utils/common/pxToRem';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import {
  Button,
  IconButton,
  InputAdornment,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  TextField,
  Typography,
} from '@mui/material';
import {GridSearchIcon} from '@mui/x-data-grid';
import {t} from 'i18next';
import {closeSnackbar} from 'notistack';
import React from 'react';
import {useDispatch} from 'react-redux';
import {useParams} from 'react-router-dom';
import CreateModel from '../../form/create-model/CreateModel';
import CreateProject from '../../form/create-project-by-template/CreateProject';

function descendingComparator<T>(a: T, b: T, orderBy: keyof T) {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

function getComparator<Key extends keyof any>(
  order: Sort,
  orderBy: Key,
): (
  a: {[key in Key]: number | string | any},
  b: {[key in Key]: number | string | any},
) => number {
  return order === 'desc'
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
}

// Since 2020 all major browsers ensure sort stability with Array.prototype.sort().
// stableSort() brings sort stability to non-modern browsers (notably IE11). If you
// only support modern browsers you can replace stableSort(exampleArray, exampleComparator)
// with exampleArray.slice().sort(exampleComparator)
function stableSort<T>(
  array: readonly T[],
  comparator: (a: T, b: T) => number,
) {
  const stabilizedThis = array.map((el, index) => [el, index] as [T, number]);
  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);
    if (order !== 0) {
      return order;
    }
    return a[1] - b[1];
  });
  return stabilizedThis.map(el => el[0]);
}

type ITableSection = {
  activeFolder: number | null;
  currentData: Array<FolderTemplateInterface>;
  setSearch: React.Dispatch<React.SetStateAction<string>>;
  search: string;
  trigger: boolean;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

const TableSection = (props: ITableSection) => {
  const param = useParams();
  const dispatch = useAppDispatch();
  const [triggerProject, setTriggerProject] = React.useState<boolean>(false);
  const [view, setView] = React.useState<'list' | 'grid'>('list');
  const [order, setOrder] = React.useState<Sort>('asc');
  const [sortField, setSortField] = React.useState<keyof DTable>('name');

  const {currentData, search, setSearch, activeFolder} = props;
  const {currentData: templateData} = useGetTemplateByIdQuery(
    {
      id: Number(param.templateId),
    },
    {skip: !Object.keys(param).length},
  );
  const [dTable, setDTable] = React.useState<Array<DTable>>([]);

  React.useEffect(() => {
    if (templateData) {
      if (!templateData.templateMessage) {
        dispatch(storeCreateModel(templateData));
        if (store.getState().authentication.createModel) {
          if (!('projectId' in param) && 'templateId' in param) {
            props.setTrigger(true);
            closeSnackbar();
          }
        }
      }

      if ('projectId' in param && 'templateId' in param) {
        const {
          approval,
          signature,
          id,
          name,
          createdBy,
          createdAt,
          recipient,
          folderId,
          notificationService,
          templateMessage,
          signProcess,
          viewer,
        } = templateData;
        dispatch(
          storeModel({
            id: id.toString(),
            templateMessage,
            notificationService,
            name,
            createdBy: createdBy.toString(),
            category: '',
            approvals: approval,
            signatories: signature,
            createdOn: createdAt,
            recipient,
            folderId,
            actions: '',
            signProcess,
            viewer,
          }),
        );
        if (store.getState().authentication.storeModel) {
          setTriggerProject(true);
          closeSnackbar();
        }
      }
    }
  }, [templateData]);

  React.useMemo(() => {
    if (currentData) {
      const tArray: Array<DTable> = [];
      currentData.forEach(item => {
        const {unitName, templates} = item;
        templates.forEach(t => {
          const {
            id,
            createdAt,
            createdByFullName,
            name,
            signature,
            approval,
            folderId,
            recipient,
            viewer,
            signProcess,
            templateMessage,
            notificationService,
          } = t;
          const nameSplit = createdByFullName.split(' ');

          return tArray.push({
            id: id.toString(),
            name: name.toLowerCase(),
            category: unitName,
            createdBy: nameSplit[0] + ' ' + (nameSplit[1] ?? ' '),
            createdOn: createdAt,
            signatories: signature,
            approvals: approval,
            folderId,
            actions: '',
            recipient,
            viewer,
            signProcess,
            templateMessage,
            notificationService,
          } as DTable);
        });
      });
      return setDTable(
        stableSort([...tArray], getComparator(order, sortField)),
      );
    }
  }, [currentData]);

  React.useMemo(() => {
    if (dTable.length) {
      return setDTable(stableSort(dTable, getComparator(order, sortField)));
    }
  }, [order, sortField]);

  const handleRequestSort = (
    _: React.MouseEvent<unknown>,
    property: ITableHeadId,
  ) => {
    const isAsc = sortField === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setSortField(property);
  };

  const handleSearch = (
    e: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => {
    setSearch(e.target.value);
  };
  return (
    <Stack p="0 40px 40px 40px">
      <Tittle
        view={view}
        setView={setView}
        search={search}
        handleSearch={handleSearch}
        trigger={props.trigger}
        setTrigger={props.setTrigger}
        triggerProject={triggerProject}
        setTriggerProject={setTriggerProject}
      />
      {view === 'list' ? (
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
            <ModelTableHead
              order={order}
              sortField={sortField}
              handleRequestSort={handleRequestSort}
            />

            <TableBody>
              {dTable
                .filter(
                  x => x.folderId === activeFolder || activeFolder === null,
                )
                .map(item => {
                  return (
                    <TableRow
                      hover
                      onClick={() => {
                        dispatch(storeModel(item));
                        setTriggerProject(true);
                      }}
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
                        id={item.id}
                        scope="row"
                        padding="none">
                        <NGText
                          text={`${item.name
                            .charAt(0)
                            .toUpperCase()}${item.name.substring(1)}`}
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
                          <NGGroupAvatar
                            character={[
                              `${item.createdBy
                                .split(' ')[0]
                                .charAt(0)}${item.createdBy
                                .split(' ')[1]
                                .charAt(0)}`,
                            ]}
                          />
                          <NGText
                            text={item.createdBy}
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
                          text={new Date(item.createdOn).toLocaleDateString(
                            'en-US',
                            {
                              month: '2-digit',
                              day: '2-digit',
                              year: 'numeric',
                            },
                          )}
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
                        <Button
                          variant="outlined"
                          sx={{
                            fontWeight: 600,
                            fontSize: 11,
                            fontFamily: 'Poppins',
                            textTransform: 'none',
                            color: '#000000',
                            border: '1px solid #000000',
                            '&:hover': {
                              color: 'Primary.main',
                            },
                          }}>
                          {t(
                            Localization(
                              'models-corporate',
                              'create-a-project',
                            ),
                          )}
                        </Button>
                      </TableCell>
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table>
        </TableContainer>
      ) : (
        <TableGridView
          data={dTable}
          activeFolder={activeFolder}
          setTriggerProject={setTriggerProject}
        />
      )}
    </Stack>
  );
};
type ITableGridView = {
  data: DTable[];
  activeFolder: number | null;
  setTriggerProject: React.Dispatch<React.SetStateAction<boolean>>;
};

const TableGridView = (props: ITableGridView) => {
  const {data, activeFolder, setTriggerProject} = props;
  const dispatch = useDispatch();
  return data ? (
    <Stack
      direction="row"
      flexWrap="wrap"
      rowGap={2}
      columnGap="16px"
      sx={{
        py: 1,
        maxHeight: `calc(100vh - 260px)`,
        overflow: 'scroll',
        '&::-webkit-scrollbar': {
          width: '0.1em',
        },

        '&::-webkit-scrollbar-thumb': {
          backgroundColor: 'grey',
        },
      }}>
      {data
        .filter(x => x.folderId === activeFolder || activeFolder === null)
        .map(i => (
          <Paper
            key={i.id}
            elevation={1}
            sx={{p: '16px', gap: '8px', width: 213, height: 162}}>
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
                  {i.category}
                </Typography>

                <IconButton
                  sx={{p: 0}}
                  onClick={() => {
                    dispatch(storeModel(i));
                    setTriggerProject(true);
                  }}>
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
  trigger: boolean;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
  triggerProject: boolean;
  setTriggerProject: React.Dispatch<React.SetStateAction<boolean>>;
  view: 'list' | 'grid';
  setView: React.Dispatch<React.SetStateAction<'list' | 'grid'>>;
};

export type DTable = {
  id: string;
  name: string;
  category: string;
  signatories: number;
  viewer: number;
  approvals: number;
  createdBy: string;
  createdOn: number;
  folderId: number;
  actions: string;
  recipient: number;
  notificationService: 'sms_email' | 'email' | 'sms';
  templateMessage: TemplateInterface['templateMessage'];
  signProcess: SIGNING_PROCESS;
};

const Tittle = (props: ITittle) => {
  const {
    search,
    handleSearch,
    setTrigger,
    trigger,
    setView,
    view,
    setTriggerProject,
    triggerProject,
  } = props;

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
        <IconButton
          onClick={() => setView('list')}
          sx={{p: 0}}
          disableFocusRipple
          disableRipple
          disableTouchRipple>
          <NGBurgerMenu
            sx={{
              color: view === 'list' ? 'Primary.main' : '#000000',
              mt: '5px',
            }}
          />
        </IconButton>

        <IconButton
          onClick={() => setView('grid')}
          sx={{p: 0}}
          disableFocusRipple
          disableRipple
          disableTouchRipple>
          <NGView
            sx={{
              color: view === 'grid' ? 'Primary.main' : '#000000',
              mt: '2px',
            }}
          />
        </IconButton>
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
      <CreateProject trigger={triggerProject} setTrigger={setTriggerProject} />
    </Stack>
  );
};

type ITableHeadId = keyof DTable;

type ITableHead = {
  id: ITableHeadId;
  numeric: boolean;
  label: string;
  disablePadding: boolean;
};

type IModelTableHead = {
  sortField: ITableHeadId;
  order: 'asc' | 'desc';
  handleRequestSort: (
    e: React.MouseEvent<unknown>,
    property: ITableHeadId,
  ) => void;
};

const ModelTableHead = ({
  order,
  sortField,
  handleRequestSort,
}: IModelTableHead) => {
  const headCells: readonly ITableHead[] = [
    {
      id: 'name',
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
      id: 'createdBy',
      numeric: true,
      disablePadding: false,
      label: t(Localization('models-corporate', 'created-by')),
    },
    {
      id: 'createdOn',
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
  const iconSort = () => {
    if (order === 'asc') {
      return <ArrowDropDownIcon sx={{fontSize: '15px'}} />;
    } else {
      return <ArrowDropUpIcon sx={{fontSize: '15px'}} />;
    }
  };

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
              hideSortIcon={headCell.id !== sortField}
              direction={'desc'}
              IconComponent={iconSort}
              onClick={(event: React.MouseEvent<unknown>) =>
                handleRequestSort(event, headCell.id)
              }>
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
