import {EntryPerPage} from '@/constant/NGContant';
import {useGetProjectsQuery} from '@/redux/slides/project-management/project';
import {$count, $isarray} from '@/utils/request/common/type';
import NGText from '@components/ng-text/NGText';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import {Stack, SxProps, useMediaQuery} from '@mui/material';
import Box from '@mui/material/Box';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import {visuallyHidden} from '@mui/utils';
import {t} from 'i18next';
import * as React from 'react';
import {Waypoint} from 'react-waypoint';

import {Localization} from '@/i18n/lan';
import {convertUTCToLocalTimeCN} from '@/utils/common/ConvertDatetoSecond';
import NGGroupAvatar from '@components/ng-group-avatar/NGGroupAvatar';
import NGPopOver from '@components/ng-popover/NGPopOver';
import {NGProjectStatus} from '@components/ng-switch-case-status/NGProjectStatus';
import ButtonThreeDotDashboard from '@components/ng-table/TableDashboard/action/ButtonThreeDotDashboard';
import HoverOnAvtar from '@components/ng-table/TableDashboard/action/HoverOnAvatar';
import NGDefaultNoProject from '@components/ng-table/TableDashboard/components/NGDefaultNoProject';
import {
  HtmlTooltip,
  styleInTable,
} from '@components/ng-table/TableDashboard/resource/TCell';
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import IconButton from '@mui/material/IconButton';
import {ProjectStatusInterfaces} from '@components/ng-switch-case-status/interface';
import {handleCancelProject} from '@/utils/common/handleCancelProject';

const DEFAULT_ROWS_PER_PAGE = EntryPerPage;

export interface NGTableComponentInterface {
  id: string;
  docId: string;
  project: string;
  createdAt: number;
  model: string;
  participants: number;
  arrayParticipant: string[];
  signatories: any[];
  arraySignatories: number;
  document: number;
  completions: string;
  statusCompletion: number;
  deadline: number;
  actions: string;
  flowId: string;
}

function descendingComparator<T>(a: T, b: T, orderBy: keyof T) {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

export type Order = 'asc' | 'desc';

function getComparator<Key extends keyof any>(
  order: Order,
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

interface HeadCell {
  disablePadding: boolean;
  id: keyof NGTableComponentInterface;
  label:
    | 'model'
    | 'recipient'
    | 'completions'
    | 'deadline'
    | 'actions'
    | 'signature project'
    | 'documents'
    | 'title';
  numeric: boolean;
}

interface EnhancedTableProps {
  numSelected: number;
  onRequestSort: (
    event: React.MouseEvent<unknown>,
    property: keyof NGTableComponentInterface,
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
      id: 'participants',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'participants')),
    },
    {
      id: 'statusCompletion',
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
    {
      id: 'actions',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'actions')),
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
    (property: keyof NGTableComponentInterface) =>
    (event: React.MouseEvent<unknown>) => {
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
        {headCells.slice(2, 3).map(headCell => (
          <TableCell
            width={xl ? '300px' : '112px'}
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
        {headCells.slice(3, 4).map(headCell => (
          <TableCell
            width={xl ? '300px' : '167px'}
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
        {headCells.slice(4, 5).map(headCell => (
          <TableCell
            width={xl ? '300px' : '280px'}
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
        {headCells.slice(5, 6).map(headCell => (
          <TableCell
            width={xl ? '120px' : '158px'}
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

export default function NGTableComponent({Sx}: {Sx?: SxProps}) {
  const [order, setOrder] = React.useState<Order>('desc');
  const [page, setPage] = React.useState(1);
  const [rowsPerPage] = React.useState(DEFAULT_ROWS_PER_PAGE);
  const [orderBy, setOrderBy] =
    React.useState<keyof NGTableComponentInterface>('createdAt');
  const [selected, setSelected] = React.useState<readonly string[]>([]);
  const [visibleRows, setVisibleRows] = React.useState<
    Array<NGTableComponentInterface>
  >([]);
  const {isLoading, currentData, data} = useGetProjectsQuery({
    page,
    pageSize: rowsPerPage,
    sortDirection: 'desc',
    sortByField: 'createdAt',
    search: '',
  });

  const checkLengthDoc = (lengthDoc: number) => {
    if (lengthDoc === 1) {
      return lengthDoc + ' ' + 'document';
    } else if (lengthDoc > 1) {
      return lengthDoc + ' ' + 'documents';
    }
    return ' No document';
  };
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
  /** state store id and status **/
  const [fieldCancelProject, setFieldCancelProject] = React.useState<{
    id: number;
    status: keyof ProjectStatusInterfaces | 'NONE';
  }>({id: 0, status: 'NONE'});
  React.useMemo(() => {
    if (currentData) {
      const {contents} = currentData;
      const newMap: Array<NGTableComponentInterface> = contents.map(item => {
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
          createdAt,
          docId: documents?.length > 0 ? documents[0].id : '',
          project: name,
          model: templateName ? templateName : '-',
          participants: participants.length,
          arrayParticipant: participants,
          signatories,
          arraySignatories: $count(signatories)!,
          document: documents?.length,
          completions: status,
          statusCompletion: checkStatus(status),
          deadline: expireDate!,
          actions: 'action',
          flowId,
        };
      });
      /** handle when fetch data the same id **/
      const newArray = newMap.filter(obj2 => {
        return !visibleRows.some(obj1 => obj1.id === obj2.id);
      });
      return visibleRows.push(
        ...stableSort([...newArray], getComparator(order, orderBy)),
      );
    }
    return [];
  }, [page, rowsPerPage, currentData, fieldCancelProject.id, visibleRows]);

  React.useMemo(() => {
    return setVisibleRows(
      stableSort(visibleRows, getComparator(order, orderBy)),
    );
  }, [order, orderBy]);

  const handleRequestSort = (
    _: React.MouseEvent<unknown>,
    property: keyof NGTableComponentInterface,
  ) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
  };

  const handleSelectAllClick = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.checked) {
      const newSelected = visibleRows.map(n => n.id);
      setSelected(newSelected);
      return;
    }
    setSelected([]);
  };

  const handleClick = (_: React.MouseEvent<unknown>, name: string) => {
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
  return visibleRows.length > 0 ? (
    <TableContainer
      sx={{
        mb: 2,
        height: `calc(100vh - 450px)`,
        '&::-webkit-scrollbar': {
          width: '0.1em',
        },

        '&::-webkit-scrollbar-thumb': {
          backgroundColor: 'grey',
        },
      }}>
      <Table stickyHeader aria-label="sticky table" size={'small'}>
        <EnhancedTableHead
          numSelected={selected.length}
          order={order}
          orderBy={orderBy}
          onSelectAllClick={handleSelectAllClick}
          onRequestSort={handleRequestSort}
          rowCount={visibleRows.length}
        />
        <TableBody>
          {visibleRows.map(row => {
            const isItemSelected = isSelected(row.id.toString());
            const labelId = `enhanced-table-checkbox-${row.id.toString()}`;

            return isLoading ? (
              <>loading...</>
            ) : (
              <TableRow
                hover
                onClick={event => handleClick(event, row.id.toString())}
                role="checkbox"
                aria-checked={isItemSelected}
                tabIndex={-1}
                key={row.id}
                selected={isItemSelected}
                sx={{
                  cursor: 'pointer',
                  '&.MuiTableRow-root': {
                    height: '60px',
                  },
                }}>
                <TableCell
                  component="th"
                  id={labelId}
                  scope="row"
                  padding="none">
                  <Stack>
                    <NGText myStyle={{...styleInTable}} text={row.project} />
                    <NGText
                      myStyle={{...styleInTable, fontWeight: 300}}
                      text={new Date(row.createdAt)
                        .toLocaleDateString('zh-Hans-CN', {
                          month: '2-digit',
                          day: '2-digit',
                          year: 'numeric',
                        })
                        .replace(/\//g, '-')}
                    />
                  </Stack>
                </TableCell>
                <TableCell align="left">
                  <NGText
                    myStyle={{
                      ...styleInTable,
                      fontWeight: 400,
                      color: '#676767',
                    }}
                    text={row.model}
                  />
                </TableCell>
                <TableCell align="left">
                  <Stack>
                    <NGText
                      myStyle={{...styleInTable, fontWeight: 300}}
                      text={checkLengthDoc(row.document)}
                    />
                  </Stack>
                </TableCell>

                <TableCell align="left">
                  <HtmlTooltip title={<HoverOnAvtar data={row.signatories} />}>
                    <IconButton
                      disableFocusRipple
                      disableRipple
                      disableTouchRipple>
                      <NGGroupAvatar character={row.arrayParticipant} />
                    </IconButton>
                  </HtmlTooltip>
                </TableCell>
                <TableCell align="left">
                  <NGProjectStatus
                    StatusName={row.completions as any}
                    signatories={row.signatories}
                  />
                </TableCell>

                <TableCell align="left">
                  <NGText
                    myStyle={{...styleInTable, fontWeight: 300}}
                    text={
                      row.deadline === null
                        ? ''
                        : convertUTCToLocalTimeCN(
                            row.deadline,
                            'en-UK',
                          ).replace(/-/g, '/')
                    }
                  />
                </TableCell>
                <TableCell
                  align="right"
                  sx={{pr: '40px'}}
                  aria-haspopup="true"
                  onClick={e => {
                    e.stopPropagation();
                  }}>
                  <IconButton>
                    <NGPopOver
                      contain={
                        <ButtonThreeDotDashboard
                          setFieldCancelProject={setFieldCancelProject}
                          data={{
                            docId: row.docId,
                            flowId: row.flowId,
                            id: row.id.toString(),
                            projectName: row.project,
                            completions: row.completions as any,
                          }}
                        />
                      }
                      button={
                        <MoreHorizIcon sx={{opacity: '70%', fontSize: 20}} />
                      }
                    />
                  </IconButton>
                </TableCell>
              </TableRow>
            );
          })}
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
  ) : (
    <Stack alignItems={'center'} justifyContent={'center'} height={'50vh'}>
      <NGDefaultNoProject />
    </Stack>
  );
}
