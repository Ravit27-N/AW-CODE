import {
  Ascending,
  Descending,
  EntryPerPage,
  InvitationStatus,
  Sort,
} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {EnhancedTableHead} from '@components/ng-table/TableParticipaint/resource/CustomHeader';
import TCell_participant from '@components/ng-table/TableParticipaint/resource/TCell';
import {headCells_participant} from '@components/ng-table/TableParticipaint/resource/THeader';
import {NGTableParticipantTypeCreateData} from '@components/ng-table/TableParticipaint/resource/Type';
import Box from '@mui/material/Box';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import * as React from 'react';
import {useTranslation} from 'react-i18next';

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

const DEFAULT_ORDER = Ascending;
const DEFAULT_ORDER_BY = 'order';
const DEFAULT_ROWS_PER_PAGE = EntryPerPage;

interface Type {
  data: NGTableParticipantTypeCreateData[];
  projectId: string;
}

export default function NGTableParticipant({data, projectId}: Type) {
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
  const {t} = useTranslation();
  // passing data to table rows
  const TableRows_participant: NGTableParticipantTypeCreateData[] = [];

  data?.length > 0 &&
    data.map((item: any) => {
      TableRows_participant.push({
        order: item.sortOrder,
        nom: item.firstName + ' ' + item.lastName,
        phone: item.phone,
        email: item.email,
        status: item.documentStatus,
        invitation: item.invitationStatus
          ? t(Localization('invitationStatus', item.invitationStatus))
          : t(Localization('invitationStatus', InvitationStatus.ON_HOLD)),
        role: t(Localization('table', item.role)),
        action: item.id,
        /** add comment refuse when they hover on icon status refused **/
        comment: item.comment,
        flowId: item.flowId,
      });
    });

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

  return (
    <Box sx={{width: '100%'}}>
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
                      role="checkbox"
                      aria-checked={isItemSelected}
                      tabIndex={-1}
                      key={row.nom + row.action + row.role}
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
      {/*</Paper>*/}
      <FormControlLabel
        sx={{display: 'none'}}
        control={<Switch checked={dense} onChange={handleChangeDense} />}
        label="Dense padding"
      />
    </Box>
  );
}
