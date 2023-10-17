import {Ascending, Descending, EntryPerPage, Sort} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import ButtonThreeDotDashboard from '@components/ng-table/TableDashboard/action/ButtonThreeDotDashboard';
import TCell_participant from '@components/ng-table/TableParticipaint/resource/TCell';
import {repairData_Participant} from '@components/ng-table/TableParticipaint/resource/TData';
import {
  NGTableParticipantInterface,
  headCells_participant,
} from '@components/ng-table/TableParticipaint/resource/THeader';
import {NGTableParticipantTypeCreateData} from '@components/ng-table/TableParticipaint/resource/Type';
import NGText from '@components/ng-text/NGText';
import {Menu, MenuProps, SxProps} from '@mui/material';
import Box from '@mui/material/Box';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import {alpha, styled} from '@mui/material/styles';
import {visuallyHidden} from '@mui/utils';
import i18next from 'i18next';
import * as React from 'react';
import {useTranslation} from 'react-i18next';

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

const DEFAULT_ORDER = Ascending;
const DEFAULT_ORDER_BY = 'order';
const DEFAULT_ROWS_PER_PAGE = EntryPerPage;

function EnhancedTableHead(props: NGTableParticipantInterface) {
  const {
    // onSelectAllClick,
    order,
    orderBy,
    // numSelected,
    // rowCount,
    onRequestSort,
    headCells,
  } = props;
  const createSortHandler =
    (newOrderBy: keyof NGTableParticipantTypeCreateData) =>
    (event: React.MouseEvent<unknown>) => {
      onRequestSort(event, newOrderBy);
    };
  const {t} = useTranslation();
  return (
    <TableHead>
      <TableRow>
        {headCells.slice(0, 1).map(headCell => (
          <TableCell
            sx={{fontWeight: 600, textAlign: 'center'}}
            key={headCell.id}
            align={headCell.numeric ? 'left' : 'right'}
            padding={headCell.disablePadding ? 'none' : 'normal'}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : Ascending}
              onClick={createSortHandler(headCell.id)}>
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === Descending
                    ? 'sorted descending'
                    : 'sorted ascending'}
                </Box>
              ) : null}
              <NGText
                text={t(Localization('table', headCell.label))}
                myStyle={{fontWeight: 600}}
              />
            </TableSortLabel>
          </TableCell>
        ))}
        {headCells.slice(1, 5).map(headCell => (
          <TableCell
            sx={{fontWeight: 600, textAlign: 'left'}}
            key={headCell.id}
            align={headCell.numeric ? 'left' : 'right'}
            padding={headCell.disablePadding ? 'none' : 'normal'}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : Ascending}
              onClick={createSortHandler(headCell.id)}>
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === Descending
                    ? 'sorted descending'
                    : 'sorted ascending'}
                </Box>
              ) : null}
              <NGText
                text={i18next.t(Localization('table', headCell.label))}
                myStyle={{fontWeight: 600}}
              />
            </TableSortLabel>
          </TableCell>
        ))}
        {headCells.slice(5, 6).map(headCell => (
          <TableCell
            sx={{fontWeight: 600, textAlign: 'center'}}
            key={headCell.id}
            align={headCell.numeric ? 'left' : 'right'}
            padding={headCell.disablePadding ? 'none' : 'normal'}>
            <NGText
              text={i18next.t(Localization('table', headCell.label))}
              myStyle={{fontWeight: 600}}
            />
          </TableCell>
        ))}

        {/*<TCell_header headCells={headCells} />*/}
      </TableRow>
    </TableHead>
  );
}

const StyledMenu = styled((props: MenuProps) => (
  <Menu
    elevation={0}
    anchorOrigin={{
      vertical: 'bottom',
      horizontal: 'right',
    }}
    transformOrigin={{
      vertical: 'top',
      horizontal: 'right',
    }}
    {...props}
  />
))(({theme}) => ({
  '& .MuiPaper-root': {
    borderRadius: 6,
    marginTop: theme.spacing(1),
    minWidth: 180,
    color:
      theme.palette.mode === 'light'
        ? 'rgb(55, 65, 81)'
        : theme.palette.grey[300],
    boxShadow:
      'rgb(255, 255, 255) 0px 0px 0px 0px, rgba(0, 0, 0, 0.05) 0px 0px 0px 1px, rgba(0, 0, 0, 0.1) 0px 10px 15px -3px, rgba(0, 0, 0, 0.05) 0px 4px 6px -2px',
    '& .MuiMenu-list': {
      padding: '4px 0',
    },
    '& .MuiMenuItem-root': {
      '& .MuiSvgIcon-root': {
        fontSize: 18,
        color: theme.palette.text.secondary,
        marginRight: theme.spacing(1.5),
      },
      '&:active': {
        backgroundColor: alpha(
          theme.palette.primary.main,
          theme.palette.action.selectedOpacity,
        ),
      },
    },
  },
}));

interface Type {
  styleTable?: SxProps;
  data: any;
  projectId: string;
}

export default function NGTableCorporate({styleTable, data, projectId}: Type) {
  const [order, setOrder] = React.useState<Sort>(DEFAULT_ORDER);
  const [orderBy, setOrderBy] = React.useState<string>(DEFAULT_ORDER_BY);
  const [selected, setSelected] = React.useState<readonly string[]>([]);
  const [page] = React.useState(0);
  const [dense, setDense] = React.useState(false);
  const [visibleRows, setVisibleRows] = React.useState<
    NGTableParticipantTypeCreateData[] | null
  >([]);
  const [rowsPerPage] = React.useState(DEFAULT_ROWS_PER_PAGE);
  const [paddingHeight] = React.useState(0);
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  /** passing data to table rows **/
  const TableRows_participant: NGTableParticipantTypeCreateData[] =
    repairData_Participant(data);
  React.useEffect(() => {
    let rowsOnMount = stableSort(
      TableRows_participant,
      getComparator(DEFAULT_ORDER, DEFAULT_ORDER_BY),
    );
    rowsOnMount = rowsOnMount.slice(0, +DEFAULT_ROWS_PER_PAGE);
    setVisibleRows(rowsOnMount);
  }, []);

  const handleRequestSort = React.useCallback(
    (event: React.MouseEvent<unknown>, newOrderBy: string) => {
      const isAsc = orderBy === newOrderBy && order === Ascending;
      const toggledOrder = isAsc ? Descending : Ascending;
      setOrder(toggledOrder);
      setOrderBy(newOrderBy);

      const sortedRows = stableSort(
        TableRows_participant,
        getComparator(toggledOrder, newOrderBy),
      );
      const updatedRows = sortedRows.slice(
        page * rowsPerPage,
        page * rowsPerPage + rowsPerPage,
      );
      setVisibleRows(updatedRows);
    },
    [order, orderBy, page, rowsPerPage],
  );

  const handleSelectAllClick = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.checked) {
      const newSelected = TableRows_participant.map(n => n.order);
      setSelected(newSelected);
      return;
    }
    setSelected([]);
  };

  const handleChangeDense = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDense(event.target.checked);
  };

  const isSelected = (name: string) => selected.indexOf(name) !== -1;

  const open = Boolean(anchorEl);
  const handleClickDot = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const handleClickDetail = (
    event: React.MouseEvent<unknown>,
    name: string,
    row: NGTableParticipantTypeCreateData,
    index: number,
  ) => {
    return {name, row, index};
  };
  return (
    <Box sx={{width: '100%'}}>
      <Box sx={{width: '100%', mb: 2, ...styleTable}}>
        {/* <EnhancedTableToolbar numSelected={selected.length} /> */}
        <TableContainer>
          <Table
            sx={{minWidth: 750}}
            aria-labelledby="tableTitle"
            size={'medium'}>
            <EnhancedTableHead
              headCells={headCells_participant}
              numSelected={selected.length}
              order={order}
              orderBy={orderBy}
              onSelectAllClick={handleSelectAllClick}
              onRequestSort={handleRequestSort}
              rowCount={TableRows_participant.length}
            />
            <TableBody>
              {visibleRows
                ? visibleRows.map((row, index) => {
                    const isItemSelected = isSelected(row.order);
                    const labelId = `enhanced-table-checkbox-${index}`;
                    return (
                      <TableRow
                        hover
                        onClick={event =>
                          handleClickDetail(event, row.order, row, index)
                        }
                        role="checkbox"
                        aria-checked={isItemSelected}
                        tabIndex={-1}
                        key={row.order}
                        selected={isItemSelected}
                        sx={{cursor: 'pointer'}}>
                        <TCell_participant
                          projectId={projectId}
                          row={row}
                          open={open}
                          isItemSelected={isItemSelected}
                          labelId={labelId}
                          handleClickDot={handleClickDot}
                        />
                      </TableRow>
                    );
                  })
                : null}
              {paddingHeight > 0 && (
                <TableRow
                  style={{
                    height: paddingHeight,
                  }}>
                  <TableCell colSpan={6} />
                </TableRow>
              )}
              <StyledMenu
                id="demo-customized-menu"
                MenuListProps={{
                  'aria-labelledby': 'demo-customized-button',
                }}
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}>
                <ButtonThreeDotDashboard />
              </StyledMenu>
            </TableBody>
          </Table>
        </TableContainer>
        {/*<TablePagination*/}
        {/*  rowsPerPageOptions={[5, 10, 25]}*/}
        {/*  component="div"*/}
        {/*  count={TableRows_participant.length}*/}
        {/*  rowsPerPage={rowsPerPage}*/}
        {/*  page={page}*/}
        {/*  onPageChange={handleChangePage}*/}
        {/*  onRowsPerPageChange={handleChangeRowsPerPage}*/}
        {/*/>*/}
      </Box>
      <FormControlLabel
        sx={{display: 'none'}}
        control={<Switch checked={dense} onChange={handleChangeDense} />}
        label="Dense padding"
      />
    </Box>
  );
}
