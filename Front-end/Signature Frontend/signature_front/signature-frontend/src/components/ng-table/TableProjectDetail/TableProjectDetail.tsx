import NGGroupAvatar from '@/components/ng-group-avatar/NGGroupAvatar';
import {ProjectStatusInterfaces} from '@/components/ng-switch-case-status/interface';
import {NGProjectStatus} from '@/components/ng-switch-case-status/NGProjectStatus';
import NGText from '@/components/ng-text/NGText';
import {
  Descending,
  FilterBy,
  ProjectStatus,
  Sort,
  STOP_TYPING_TIMEOUT,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {ITableAllProject} from '@/pages/end-user/project-detail/empty/tabs/TabAllProject';
import {useAppSelector} from '@/redux/config/hooks';
import {useGetProjectsQuery} from '@/redux/slides/project-management/project';
import {convertUTCToLocalTimeCN} from '@/utils/common/ConvertDatetoSecond';
import {HandleException} from '@/utils/common/HandleException';
import {pixelToRem} from '@/utils/common/pxToRem';
import {$isarray} from '@/utils/request/common/type';
import {Signatory} from '@/utils/request/interface/Project.interface';
import {NGThreeDot} from '@assets/iconExport/Allicon';
import NgPopOver from '@components/ng-popover/NGPopOver';
import ButtonThreeDotDashboard from '@components/ng-table/TableDashboard/action/ButtonThreeDotDashboard';
import {
  HtmlTooltip,
  styleInTable,
} from '@components/ng-table/TableDashboard/resource/TCell';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import {
  Box,
  Checkbox,
  IconButton,
  Skeleton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  useMediaQuery,
} from '@mui/material';
import {visuallyHidden} from '@mui/utils';
import {t} from 'i18next';
import {useSnackbar} from 'notistack';
import React, {useState} from 'react';
import {Waypoint} from 'react-waypoint';
import HoverOnAvtar from '../TableDashboard/action/HoverOnAvatar';
import {Order} from '../TableDashboard/NGTableComponent';
import {handleCancelProject} from '@/utils/common/handleCancelProject';

type IDataRow = {
  id: string;
  draftSignature: string;
  model: string;
  document: number;
  participant: number;
  dParticipant: string[];
  completion: keyof ProjectStatusInterfaces;
  dCompletion: number;
  deadline: number;
  signatories: Signatory[];
  docId: string;
  createdAt: number;
  flowId: string;
};

interface HeadCell {
  disablePadding: boolean;
  id: keyof IDataRow;
  label: string;
  numeric: boolean;
}

interface EnhancedTableProps {
  numSelected: number;
  onRequestSort: (
    event: React.MouseEvent<unknown>,
    property: keyof IDataRow,
  ) => void;
  onSelectAllClick: (event: React.ChangeEvent<HTMLInputElement>) => void;
  order: Order;
  orderBy: string;
  rowCount: number;
}

function EnhancedTableHead(props: EnhancedTableProps) {
  const headCells: readonly HeadCell[] = [
    {
      id: 'createdAt',
      numeric: false,
      disablePadding: true,
      label: t(Localization('table', 'signature project')),
    },
    {
      id: 'model',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'model')),
    },
    {
      id: 'document',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'documents')),
    },
    {
      id: 'participant',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'participants')),
    },
    {
      id: 'completion',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'completions')),
    },
    {
      id: 'deadline',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'deadline')),
    },
  ];

  const {
    onSelectAllClick,
    order,
    orderBy,
    numSelected,
    rowCount,
    onRequestSort,
  } = props;
  const createSortHandler =
    (property: keyof IDataRow) => (event: React.MouseEvent<unknown>) => {
      onRequestSort(event, property);
    };
  const xl = useMediaQuery(`(min-width:1441px)`);
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
        {/* <TableCell padding="checkbox">
          <Checkbox
            color="primary"
            indeterminate={numSelected > 0 && numSelected < rowCount}
            checked={rowCount > 0 && numSelected === rowCount}
            onChange={onSelectAllClick}
            inputProps={{
              'aria-label': 'select all desserts',
            }}
          />
        </TableCell> */}
        {headCells.slice(0, 1).map(headCell => (
          <TableCell
            width={xl ? '300px' : '210px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              hideSortIcon={orderBy !== headCell.id}
              IconComponent={iconSort}
              onClick={createSortHandler(headCell.id)}>
              <NGText
                text={headCell.label}
                myStyle={{...styleInTable, cursor: 'pointer', mr: 2}}
              />
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
        {headCells.slice(1, 2).map(headCell => (
          <TableCell
            width={xl ? '300px' : '142px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              hideSortIcon={orderBy !== headCell.id}
              IconComponent={iconSort}
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              onClick={createSortHandler(headCell.id)}>
              <NGText
                text={headCell.label}
                myStyle={{...styleInTable, cursor: 'pointer', mr: 2}}
              />
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
        {headCells.slice(2, 3).map(headCell => (
          <TableCell
            width={xl ? '300px' : '112px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              hideSortIcon={orderBy !== headCell.id}
              IconComponent={iconSort}
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              onClick={createSortHandler(headCell.id)}>
              <NGText
                text={headCell.label}
                myStyle={{...styleInTable, cursor: 'pointer', mr: 2}}
              />
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
        {headCells.slice(3, 4).map(headCell => (
          <TableCell
            width={xl ? '300px' : '167px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              hideSortIcon={orderBy !== headCell.id}
              IconComponent={iconSort}
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              onClick={createSortHandler(headCell.id)}>
              <NGText
                text={headCell.label}
                myStyle={{...styleInTable, cursor: 'pointer', mr: 2}}
              />
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
        {headCells.slice(4, 5).map(headCell => (
          <TableCell
            width={xl ? '300px' : '280px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              hideSortIcon={orderBy !== headCell.id}
              IconComponent={iconSort}
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              onClick={createSortHandler(headCell.id)}>
              <NGText
                text={headCell.label}
                myStyle={{...styleInTable, cursor: 'pointer', mr: 2}}
              />
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
        {headCells.slice(5, 6).map(headCell => (
          <TableCell
            width={xl ? '200px' : '220px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}>
            <NGText
              text={headCell.label}
              myStyle={{...styleInTable, cursor: 'pointer', mr: 2}}
            />
          </TableCell>
        ))}
        {headCells.slice(6, 7).map(headCell => (
          <TableCell
            width={xl ? '300px' : '52px'}
            key={headCell.id}
            align={'right'}
            padding={headCell.disablePadding ? 'none' : 'normal'}>
            <NGText
              text={headCell.label}
              myStyle={{...styleInTable, cursor: 'pointer', mr: 2}}
            />
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
}

function descendingComparator<T>(a: T, b: T, orderBy: keyof T) {
  let res = 0;
  if (b[orderBy] < a[orderBy]) {
    res = -1;
  }
  if (b[orderBy] > a[orderBy]) {
    res = 1;
  }
  return res;
}

function getComparator<Key extends keyof any>(
  order: Sort,
  orderBy: Key,
): (
  a: {[key in Key]: number | string | any},
  b: {[key in Key]: number | string | any},
) => number {
  return order === Descending
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
}

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

const TableProjectDetail = (props: ITableAllProject & {search: string}) => {
  /** selectOption: When they change it data in table of project must change  **/
  const {tab, selectOption, search, arrayOfStatus} = props;
  const {theme} = useAppSelector(state => state.enterprise);
  const [order, setOrder] = useState<Sort>(Descending);
  const [orderBy, setOrderBy] = useState<keyof IDataRow>('createdAt');
  const [visibleRows, setVisibleRows] = useState<Array<IDataRow>>([]);
  const [selected, setSelected] = React.useState<readonly string[]>([]);
  const [page, setPage] = useState(1);
  const [rowsPerPage] = useState(10);
  const [status, setStatus] = useState<string[]>(['']);
  const [filter, setFilter] = useState<FilterBy>(FilterBy.ENDUSER);
  const startEndDate = useAppSelector(state => state.counter.startEndDate);
  /** searchProjectName use middle ware of search and fetching data again **/
  const [searchProjectName, setSearchProjectName] = useState('');
  /** fetch data for get projects  **/
  const {isLoading, currentData, error, data} = useGetProjectsQuery({
    page,
    pageSize: rowsPerPage,
    /**status: validate data when change tab **/
    statuses: status,
    search: searchProjectName,
    sortDirection: 'desc',
    sortByField: 'createdAt',
    /** filterBy: validate data when change select option (end-user/department/company)**/
    filterBy: filter,
    startDate:
      startEndDate.start === null || startEndDate.start === undefined
        ? ''
        : startEndDate.start.add(1, 'day').toISOString(),
    endDate:
      startEndDate.end === null || startEndDate.end === undefined
        ? ''
        : startEndDate.end.add(1, 'day').toISOString(),
  });
  /** when multiple filter by checked status has change **/
  React.useEffect(() => {
    setPage(1);
    setVisibleRows([]);
    setStatus([...arrayOfStatus]);
    if (arrayOfStatus.length === 0) {
      if (tab === 'ALL-PROJECTS') setStatus(prev => [...prev, '']);
      else setStatus(prev => [...prev, tab]);
    }
  }, [arrayOfStatus.length]);
  const {enqueueSnackbar, closeSnackbar} = useSnackbar();
  const checkStatus = (status: string) => {
    switch (status) {
      case 'IN_PROGRESS': {
        return 0;
      }
      case 'COMPLETED': {
        return 1;
      }
      default: {
        return -1;
      }
    }
  };
  const checkLengthDoc = (lengthDoc: number) => {
    if (lengthDoc === 1) {
      return lengthDoc + ' ' + 'document';
    } else if (lengthDoc > 1) {
      return lengthDoc + ' ' + 'documents';
    }
    return ' No document';
  };
  /** state store id and status **/
  const [fieldCancelProject, setFieldCancelProject] = React.useState<{
    id: number;
    status: keyof ProjectStatusInterfaces | 'NONE';
  }>({id: 0, status: 'NONE'});
  /** add data to visibleRows when id is different **/
  React.useMemo(() => {
    if (currentData) {
      const {contents} = currentData;
      const newMap: Array<IDataRow> = contents.map(item => {
        const {
          id,
          documents,
          name,
          createdAt,
          templateName,
          signatories,
          status,
          expireDate,
          flowId,
        } = item;
        const participants: string[] = [];
        if ($isarray(signatories)) {
          signatories.forEach(participant =>
            participants.push(
              participant.firstName?.charAt(0).toUpperCase() +
                participant.lastName?.charAt(0).toUpperCase(),
            ),
          );
        }
        return {
          id: id as unknown as string,
          draftSignature: name,
          model: templateName ?? '-',
          document: documents?.length,
          participant: participants.length,
          dParticipant: participants,
          completion: status,
          dCompletion: checkStatus(status),
          deadline: expireDate!,
          signatories,
          docId: documents.length > 0 ? documents[0].id : 0,
          createdAt,
          flowId,
        } as IDataRow;
      });
      /** if newMap is Array and visibleRows also Array **/
      if (
        $isarray(newMap) &&
        $isarray(visibleRows) &&
        visibleRows &&
        search === ''
      ) {
        const result = newMap.filter(obj1 =>
          visibleRows?.every(obj2 => obj1.id !== obj2.id),
        );
        /** set data to visibleRows when id different **/
        setVisibleRows(visibleRows => visibleRows.concat(result));
      } else {
        setPage(1);
        setVisibleRows([]);
        setVisibleRows(newMap);
      }
      if (startEndDate.end !== null || startEndDate.start !== null) {
        setPage(1);
        setVisibleRows([]);
        setVisibleRows(newMap);
      }
    }
  }, [
    page,
    rowsPerPage,
    currentData,
    searchProjectName,
    startEndDate,
    selectOption,
  ]);

  React.useMemo(() => {
    setPage(1);
    setVisibleRows([]);
  }, [selectOption?.selectedFilterBy]);
  /** filter will process when selectedOption change**/
  React.useEffect(() => {
    setFilter(selectOption?.selectedFilterBy!);
  }, [selectOption?.selectedFilterBy]);
  /** search will process after STOP TYPING TIMEOUT **/
  React.useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      setSearchProjectName(search);
    }, STOP_TYPING_TIMEOUT);
    return () => clearTimeout(delayDebounceFn);
  }, [search]);
  /** validate data when change tab **/
  React.useEffect(() => {
    /** if tab change tab page must change to page one **/
    setPage(1);
    /** if tab all project active set it to ''  for show all project else follow by tab that we active  **/
    if (tab === 'ALL-PROJECTS') {
      setStatus(['']);
    } else {
      setStatus([tab]);
    }
    setVisibleRows([]);
  }, [tab]);
  /** for sort data in table **/
  React.useMemo(() => {
    return setVisibleRows(
      stableSort(visibleRows, getComparator(order, orderBy)),
    );
  }, [order, orderBy]);
  /** handler when something error with backend **/
  React.useEffect(() => {
    if (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
    return () => closeSnackbar();
  }, [error]);
  const handleSelectAllClick = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.checked) {
      const newSelected = visibleRows.map(n => n.id);
      setSelected(newSelected);
      return;
    }
    setSelected([]);
  };

  const handleClick = (event: React.MouseEvent<unknown>, name: string) => {
    const selectedIndex = selected.indexOf(name);
    let newSelected: readonly string[] = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selected, name);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(selected.slice(1));
    } else if (selectedIndex === selected.length - 1) {
      newSelected = newSelected.concat(selected.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        selected.slice(0, selectedIndex),
        selected.slice(selectedIndex + 1),
      );
    }

    // setSelected(newSelected);
  };

  const isSelected = (name: string) => selected.indexOf(name) !== -1;

  const gotoNextPage = ({currentPosition}: Waypoint.CallbackArgs) => {
    if (currentPosition === 'inside') {
      setPage(p => p + 1);
    }
  };
  const handleRequestSort = (
    event: React.MouseEvent<unknown>,
    property: keyof IDataRow,
  ) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
  };
  /** process working when onChange on id or status **/
  React.useEffect(() => {
    if (fieldCancelProject.id !== 0 && fieldCancelProject.status !== 'NONE') {
      /** handle project cancel**/
      handleCancelProject(
        fieldCancelProject.status,
        setVisibleRows,
        visibleRows,
        fieldCancelProject.id,
      );
    }
  }, [fieldCancelProject.id, fieldCancelProject.status]);
  /** Skeleton for loading  **/
  if (isLoading) {
    return (
      <Stack p={2} height="290px">
        {Array.from({length: 8}, (_, index: number) => (
          <Skeleton
            key={index}
            variant="rectangular"
            sx={{
              borderRadius: '3px',
              mb: 0.5,
            }}
            animation="pulse"
            width={'auto'}
            height={'35px'}
          />
        ))}
      </Stack>
    );
  }

  return (
    <TableContainer
      sx={{
        height: `calc(100vh - 320px)`,
        '&::-webkit-scrollbar': {
          width: '0.1em',
        },

        '&::-webkit-scrollbar-thumb': {
          backgroundColor: 'grey',
        },
      }}>
      <Table stickyHeader aria-label="sticky table" size="small">
        <EnhancedTableHead
          numSelected={selected.length}
          order={order}
          orderBy={orderBy}
          onSelectAllClick={handleSelectAllClick}
          onRequestSort={handleRequestSort}
          rowCount={visibleRows.length}
        />
        <TableBody>
          {/** render to show data in table **/}
          {visibleRows.map((row, index: number) => {
            const isItemSelected = isSelected(row.id);
            const labelId = `enhanced-table-checkbox-${index}`;
            return (
              <TableRow
                hover
                onClick={event => handleClick(event, row.id)}
                role="checkbox"
                aria-checked={isItemSelected}
                tabIndex={-1}
                key={row.id}
                selected={isItemSelected}
                sx={{
                  '&.MuiTableRow-root': {
                    height: '60px',
                    '&:hover': {
                      /** when we hover on row will color main Color for them and opacity 10 **/
                      backgroundColor: theme[0].mainColor
                        ? theme[0].mainColor + 10
                        : 'white',
                      color: ' #fff !important',
                    },
                  },
                  cursor: 'pointer',
                }}>
                {/** Cell Checkbox **/}
                {/* <TableCell padding="checkbox">
                  <Checkbox
                    color="primary"
                    checked={isItemSelected}
                    inputProps={{
                      'aria-labelledby': labelId,
                    }}
                  />
                </TableCell> */}
                {/** Cell Signature Project **/}
                <TableCell
                  component="th"
                  id={labelId}
                  scope="row"
                  padding="none">
                  <Stack>
                    <NGText
                      text={row.draftSignature}
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 500,
                      }}
                    />
                    <NGText
                      myStyle={{...styleInTable, fontWeight: 300}}
                      text={new Date(row.createdAt).toLocaleDateString(
                        'zh-Hans-CN',
                        {
                          month: '2-digit',
                          day: '2-digit',
                          year: 'numeric',
                        },
                      )}
                    />
                  </Stack>
                </TableCell>
                {/** Cell Model **/}
                <TableCell align="left">
                  <NGText
                    text={row.model}
                    myStyle={{
                      fontSize: pixelToRem(12),
                      lineHeight: pixelToRem(16),
                      fontWeight: 300,
                    }}
                  />
                </TableCell>
                {/** Cell Documents **/}
                <TableCell align="left">
                  <Stack>
                    <NGText
                      myStyle={{...styleInTable, fontWeight: 300}}
                      text={checkLengthDoc(row.document)}
                    />
                  </Stack>
                </TableCell>
                {/** Cell Participants **/}
                <TableCell align="left">
                  <HtmlTooltip title={<HoverOnAvtar data={row.signatories} />}>
                    <IconButton
                      disableFocusRipple
                      disableRipple
                      disableTouchRipple>
                      <NGGroupAvatar character={row.dParticipant} />
                    </IconButton>
                  </HtmlTooltip>
                </TableCell>
                {/** Cell Completion **/}
                <TableCell align="left">
                  <NGProjectStatus
                    StatusName={row.completion}
                    signatories={row.signatories}
                  />
                </TableCell>
                {/** Cell Deadline and disable stop Propagation
                                 for make it not affect when we select on row **/}
                <TableCell
                  align="left"
                  onClick={e => {
                    e.stopPropagation();
                  }}>
                  <Stack direction={'row'} justifyContent={'space-between'}>
                    <NGText
                      text={
                        row.deadline === null
                          ? ''
                          : convertUTCToLocalTimeCN(
                              row.deadline,
                              'en-UK',
                            ).replace(/-/g, '/')
                      }
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 300,
                      }}
                    />
                    <NgPopOver
                      button={
                        <NGThreeDot
                          sx={{
                            color: 'primary.main',
                            fontSize: pixelToRem(14),
                          }}
                        />
                      }
                      contain={
                        <ButtonThreeDotDashboard
                          setFieldCancelProject={setFieldCancelProject}
                          data={{
                            docId: row.docId,
                            flowId: row.flowId,
                            id: row.id,
                            projectName: row.draftSignature,
                            completions: row.completion as any,
                          }}
                        />
                      }
                    />
                  </Stack>
                </TableCell>
              </TableRow>
            );
          })}
          {/** Waypoint will process when we scroll end of data in table for fetch more data **/}
          {visibleRows.length > 0 && data!.hasNext && (
            <TableRow>
              <TableCell>
                <Waypoint onEnter={gotoNextPage} />
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default TableProjectDetail;
