import * as React from 'react';
import Box from '@mui/material/Box';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import {visuallyHidden} from '@mui/utils';
import NGText from '@components/ng-text/NGText';
import {pixelToRem} from '@/utils/common/pxToRem';
import TablePagination from '@mui/material/TablePagination';
import {SxProps} from '@mui/system';
import Stack from '@mui/material/Stack';
import NGGroupAvatar from '@components/ng-group-avatar/NGGroupAvatar';
import {shortName} from '@/utils/common/SortName';

interface Data {
  id: string;
  utilisateurs: string;
  projets: string | number;
  pourcentage: string | number;
}

function createData(
  id: string,
  utilisateurs: string,
  projets: string | number,
  pourcentage: string | number,
): Data {
  return {
    id,
    utilisateurs,
    projets,
    pourcentage,
  };
}

const rows = [
  createData('1', 'Arthur Bonnet-Morel', 16, '5%'),
  createData('2', 'Hortense Buisson', 15, '5%'),
  createData('3', 'Thérèse Chartier', 12, '5%'),
  createData('4', 'Mathilde Coste', 12, '5%'),
  createData('5', 'Louis-Michel Delmas', 10, '4%'),
];

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

type Order = 'asc' | 'desc';

function getComparator<Key extends keyof any>(
  order: Order,
  orderBy: Key,
): (
  a: {[key in Key]: number | string},
  b: {[key in Key]: number | string},
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
  id: keyof Data;
  label: string;
  numeric: boolean;
}

const headCells: readonly HeadCell[] = [
  {
    id: 'utilisateurs',
    numeric: false,
    disablePadding: true,
    label: 'Utilisateurs',
  },
  {
    id: 'projets',
    numeric: true,
    disablePadding: false,
    label: 'Projets',
  },
  {
    id: 'pourcentage',
    numeric: true,
    disablePadding: false,
    label: 'Pourcentage',
  },
];

interface EnhancedTableProps {
  numSelected: number;
  onRequestSort: (
    event: React.MouseEvent<unknown>,
    property: keyof Data,
  ) => void;
  onSelectAllClick: (event: React.ChangeEvent<HTMLInputElement>) => void;
  order: Order;
  orderBy: string;
  rowCount: number;
}

function EnhancedTableHead(props: EnhancedTableProps) {
  const {
    order,
    orderBy,

    onRequestSort,
  } = props;
  const createSortHandler =
    (property: keyof Data) => (event: React.MouseEvent<unknown>) => {
      onRequestSort(event, property);
    };

  return (
    <TableHead>
      <TableRow>
        {headCells.slice(0, 1).map(headCell => (
          <TableCell
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sx={{pl: 1}}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              onClick={createSortHandler(headCell.id)}>
              <NGText
                text={headCell.label}
                myStyle={{
                  fontSize: pixelToRem(12),
                  lineHeight: pixelToRem(24),
                  fontWeight: 500,
                }}
              />
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
        {headCells.slice(1, 4).map(headCell => (
          <TableCell
            key={headCell.id}
            align={'center'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              onClick={createSortHandler(headCell.id)}>
              <NGText
                text={headCell.label}
                myStyle={{
                  fontSize: pixelToRem(12),
                  lineHeight: pixelToRem(24),
                  fontWeight: 500,
                }}
              />
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
}

export default function NGTableDocument({Sx}: {Sx: SxProps}) {
  const [order, setOrder] = React.useState<Order>('asc');
  const [orderBy, setOrderBy] = React.useState<keyof Data>('utilisateurs');
  const [selected, setSelected] = React.useState<readonly string[]>([]);
  const [page, setPage] = React.useState(0);
  const [dense] = React.useState(false);
  const [rowsPerPage, setRowsPerPage] = React.useState(5);

  const handleRequestSort = (
    event: React.MouseEvent<unknown>,
    property: keyof Data,
  ) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
  };

  const handleSelectAllClick = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.checked) {
      const newSelected = rows.map(n => n.utilisateurs);
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

    setSelected(newSelected);
  };

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const isSelected = (name: string) => selected.indexOf(name) !== -1;

  // Avoid a layout jump when reaching the last page with empty rows.
  const emptyRows =
    page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rows.length) : 0;

  const visibleRows = React.useMemo(
    () =>
      stableSort(rows, getComparator(order, orderBy)).slice(
        page * rowsPerPage,
        page * rowsPerPage + rowsPerPage,
      ),
    [order, orderBy, page, rowsPerPage],
  );

  return (
    <Box
      sx={{
        width: '100%',
        mb: 2,
        height: pixelToRem(100),

        ...Sx,
      }}>
      {/*<EnhancedTableToolbar numSelected={selected.length} />*/}
      <TableContainer>
        <Table
          sx={{minWidth: 750}}
          aria-labelledby="tableTitle"
          size={dense ? 'small' : 'medium'}>
          <EnhancedTableHead
            numSelected={selected.length}
            order={order}
            orderBy={orderBy}
            onSelectAllClick={handleSelectAllClick}
            onRequestSort={handleRequestSort}
            rowCount={rows.length}
          />
          <TableBody>
            {visibleRows.map((row, index) => {
              const isItemSelected = isSelected(row.utilisateurs);
              const labelId = `enhanced-table-checkbox-${index}`;

              return (
                <TableRow
                  hover
                  onClick={event => handleClick(event, row.utilisateurs)}
                  role="checkbox"
                  aria-checked={isItemSelected}
                  tabIndex={-1}
                  key={row.utilisateurs}
                  selected={isItemSelected}
                  sx={{cursor: 'pointer'}}>
                  <TableCell
                    component="th"
                    id={labelId}
                    scope="row"
                    sx={{pl: 1}}
                    padding="none">
                    <Stack
                      direction={'row'}
                      spacing={pixelToRem(20)}
                      pl={pixelToRem(2)}
                      py={2}
                      // justifyContent={'center'}
                      alignItems={'center'}>
                      <NGText
                        text={row.id}
                        myStyle={{
                          fontSize: pixelToRem(12),
                          lineHeight: pixelToRem(16),
                          fontWeight: 500,
                        }}
                      />
                      <Stack
                        direction={'row'}
                        alignItems={'center'}
                        spacing={pixelToRem(10)}>
                        <NGGroupAvatar
                          character={[shortName(row.utilisateurs)]}
                        />
                        <NGText
                          text={row.utilisateurs}
                          myStyle={{
                            fontSize: pixelToRem(12),
                            lineHeight: pixelToRem(16),
                            fontWeight: 500,
                          }}
                        />
                      </Stack>
                    </Stack>
                  </TableCell>
                  <TableCell align="center">
                    <NGText
                      text={row.projets}
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 300,
                      }}
                    />
                  </TableCell>

                  <TableCell align="center">
                    <NGText
                      text={row.pourcentage}
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 300,
                      }}
                    />
                  </TableCell>
                </TableRow>
              );
            })}
            {emptyRows > 0 && (
              <TableRow
                style={{
                  height: (dense ? 33 : 53) * emptyRows,
                }}>
                <TableCell colSpan={6} />
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        sx={{display: rowsPerPage > 5 ? 'flex' : 'none'}}
        rowsPerPageOptions={[5, 10, 25]}
        component="div"
        count={rows.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />
    </Box>
  );
}
