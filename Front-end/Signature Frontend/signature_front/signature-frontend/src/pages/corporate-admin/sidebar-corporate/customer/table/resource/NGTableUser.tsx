import {NGThreeDot} from '@/assets/iconExport/Allicon';
import NgPopOver from '@/components/ng-popover/NGPopOver';
import {
  IGetUsersContent,
  IGetUsersQuery,
} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGText from '@components/ng-text/NGText';
import {Stack, useMediaQuery} from '@mui/material';
import Box from '@mui/material/Box';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import {SxProps} from '@mui/system';
import {visuallyHidden} from '@mui/utils';
import * as React from 'react';
import {Waypoint} from 'react-waypoint';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import {useAppSelector} from '@/redux/config/hooks';
import ButtonThreeDotParticipant from '../action/ButtonThreeDot';

type Order = 'asc' | 'desc';

interface HeadCell {
  disablePadding: boolean;
  id: IGetUsersQuery['sortField'];
  label: string;
  numeric: boolean;
}

const headCells: readonly HeadCell[] = [
  {
    id: 'lastName',
    numeric: false,
    disablePadding: true,
    label: 'Nom',
  },
  {
    id: 'firstName',
    numeric: true,
    disablePadding: false,
    label: 'Prénom',
  },
  {
    id: 'functional',
    numeric: true,
    disablePadding: false,
    label: 'Fonction',
  },
  {
    id: 'businessUnit.unitName',
    numeric: true,
    disablePadding: false,
    label: 'Département',
  },
  {
    id: 'userAccess.name',
    numeric: true,
    disablePadding: false,
    label: 'Droits d’accès',
  },
  {
    id: 'id',
    numeric: true,
    disablePadding: false,
    label: 'Actions',
  },
];

interface EnhancedTableProps {
  xl: boolean;
  numSelected: number;
  onRequestSort: (
    event: React.MouseEvent<unknown>,
    property: IGetUsersQuery['sortField'],
  ) => void;
  onSelectAllClick: (event: React.ChangeEvent<HTMLInputElement>) => void;
  order: Order;
  orderBy: string;
  rowCount: number;
}

function EnhancedTableHead(props: EnhancedTableProps) {
  const {
    onSelectAllClick,
    order,
    orderBy,
    numSelected,
    rowCount,
    xl,
    onRequestSort,
  } = props;
  const createSortHandler =
    (property: IGetUsersQuery['sortField']) =>
    (event: React.MouseEvent<unknown>) => {
      onRequestSort(event, property);
    };

  return (
    <TableHead>
      <TableRow>
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
            width={xl ? '300px' : '224px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              IconComponent={ArrowDropDownIcon}
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
        {headCells.slice(1, 2).map(headCell => (
          <TableCell
            width={xl ? '300px' : '222px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              IconComponent={ArrowDropDownIcon}
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
        {headCells.slice(2, 3).map(headCell => (
          <TableCell
            width={xl ? '300px' : '214px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              IconComponent={ArrowDropDownIcon}
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
        {headCells.slice(3, 4).map(headCell => (
          <TableCell
            width={xl ? '300px' : '190px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              IconComponent={ArrowDropDownIcon}
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
        {headCells.slice(4, 5).map(headCell => (
          <TableCell
            width={xl ? '300px' : '226px'}
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}>
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              IconComponent={ArrowDropDownIcon}
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
        {headCells.slice(5, 6).map(headCell => (
          <TableCell
            key={headCell.id}
            align={'right'}
            padding={headCell.disablePadding ? 'none' : 'normal'}>
            <NGText
              text={headCell.label}
              myStyle={{
                fontSize: pixelToRem(12),
                lineHeight: pixelToRem(24),
                fontWeight: 500,
              }}
            />
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
}

export default function NGTableUser({
  isLoading,
  Sx,
  userList = [],
  onUpdate,
  onDelete,
  onActionOpen,
  onActionClose,
  userFilter,
  selected,
  onSelectCheckbox,
  onSelectAllCheckbox,
  hasNext,
  onLastItem,
  onRequestSort,
}: {
  isLoading?: boolean;
  Sx: SxProps;
  onActionOpen?: (data: IGetUsersContent) => void;
  onActionClose?: () => void;
  userList: Array<IGetUsersContent>;
  onUpdate?: () => void;
  onDelete?: () => void;
  userFilter: IGetUsersQuery;
  selected: readonly string[];
  onSelectCheckbox: (event: React.MouseEvent<unknown>, name: string) => void;
  onSelectAllCheckbox: (event: React.ChangeEvent<HTMLInputElement>) => void;
  hasNext: boolean;
  onLastItem: (props: Waypoint.CallbackArgs) => void;
  onRequestSort: (
    event: React.MouseEvent<unknown>,
    property: IGetUsersQuery['sortField'],
  ) => void;
}) {
  const {theme} = useAppSelector(state => state.enterprise);
  const xlUp = useMediaQuery(`(min-width:1441px)`);
  const [open, setOpen] = React.useState<boolean>(false);

  const isSelected = (name: string) => selected.indexOf(name) !== -1;

  return (
    <Box>
      <TableContainer
        sx={{
          ...Sx,
          '&::-webkit-scrollbar': {
            width: '0.1em',
          },

          '&::-webkit-scrollbar-thumb': {
            backgroundColor: 'grey',
          },
        }}>
        <Table stickyHeader aria-label="sticky table" size={'medium'}>
          <EnhancedTableHead
            numSelected={selected.length}
            order={userFilter.sortDirection}
            xl={xlUp}
            orderBy={userFilter.sortField}
            onSelectAllClick={onSelectAllCheckbox}
            onRequestSort={onRequestSort}
            rowCount={userList.length}
          />

          <TableBody>
            {userList.map((row, index: number) => {
              const isItemSelected = isSelected(row.id.toString());
              const labelId = `enhanced-table-checkbox-${index}`;

              return isLoading ? (
                <>loading...</>
              ) : (
                <TableRow
                  hover
                  onClick={event => onSelectCheckbox(event, row.id.toString())}
                  role="checkbox"
                  aria-checked={isItemSelected}
                  tabIndex={-1}
                  key={row.id.toString()}
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
                  {/* <TableCell padding="checkbox">
                    <Checkbox
                      color="primary"
                      checked={isItemSelected}
                      inputProps={{
                        'aria-labelledby': labelId,
                      }}
                    />
                  </TableCell> */}
                  <TableCell
                    component="th"
                    id={labelId}
                    scope="row"
                    padding="none">
                    <NGText
                      text={row.lastName}
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 500,
                      }}
                    />
                  </TableCell>
                  <TableCell align="left">
                    <NGText
                      text={row.firstName}
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 300,
                      }}
                    />
                  </TableCell>
                  <TableCell align="left">
                    <NGText
                      text={row.functional}
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 300,
                      }}
                    />
                  </TableCell>
                  <TableCell align="left">
                    <NGText
                      text={row.department?.unitName}
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 300,
                      }}
                    />
                  </TableCell>
                  <TableCell align="left">
                    <NGText
                      text={row.userAccess?.name}
                      myStyle={{
                        fontSize: pixelToRem(12),
                        lineHeight: pixelToRem(16),
                        fontWeight: 300,
                      }}
                    />
                  </TableCell>
                  <TableCell
                    align="right"
                    onClick={e => {
                      e.stopPropagation();
                    }}>
                    <Stack
                      direction={'row'}
                      justifyContent={'right'}
                      spacing={3}
                      alignItems={'center'}>
                      <NgPopOver
                        open={open}
                        onClick={() => {
                          // temporarily disable action popup
                          // setOpen(true);
                          if (typeof onActionOpen === 'function') {
                            onActionOpen(row);
                          }
                        }}
                        onClose={() => {
                          if (typeof onActionClose === 'function') {
                            onActionClose();
                          }
                        }}
                        button={
                          <NGThreeDot
                            sx={{color: 'primary.main', fontSize: 15}}
                            onClick={() => setOpen(true)}
                          />
                        }
                        contain={
                          <ButtonThreeDotParticipant
                            userId={row.userId}
                            onDelete={() => {
                              setOpen(false);
                              if (typeof onDelete === 'function') onDelete();
                            }}
                          />
                        }
                      />
                    </Stack>
                  </TableCell>
                </TableRow>
              );
            })}
            {userList.length > 0 && hasNext && (
              <TableRow>
                <TableCell>
                  <Waypoint onEnter={onLastItem} />
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
