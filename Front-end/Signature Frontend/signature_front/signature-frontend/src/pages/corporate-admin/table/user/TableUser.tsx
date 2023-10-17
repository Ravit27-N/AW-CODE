import NGGroupAvatar from '@/components/ng-group-avatar/NGGroupAvatar';
import {Localization} from '@/i18n/lan';
import {
  DocumentTableInterface,
  ISortFieldUser,
} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGText from '@components/ng-text/NGText';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import {Stack, TableBody, TableSortLabel} from '@mui/material';
import Table from '@mui/material/Table';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import {t} from 'i18next';
import React from 'react';
import {ISortFieldAndDirUser} from '../../Home.corporate';

export interface TableServiceInterface {
  id: string;
  user: string;
  totalProjects: number;
  percentage: number | string;
}

interface HeadCell {
  id: keyof TableServiceInterface;
  label: string;
}

type IEnhancedTableHead = {
  setSortUser: React.Dispatch<React.SetStateAction<ISortFieldAndDirUser>>;
  sortUser: ISortFieldAndDirUser;
};

function EnhancedTableHead(props: IEnhancedTableHead) {
  const {setSortUser, sortUser} = props;
  const headCells: readonly HeadCell[] = [
    {
      id: 'user',
      label: t(Localization('table', 'users')),
    },
    {
      id: 'totalProjects',
      label: t(Localization('table', 'project')),
    },
    {
      id: 'percentage',
      label: t(Localization('table', 'percentage')),
    },
  ];

  const handleSortAndDir = (
    sortField: ISortFieldUser,
    sortDirection: 'asc' | 'desc',
  ) => {
    setSortUser(prev => ({
      ...prev,
      sortField,
      sortDirection,
    }));
  };
  const handleWidthTableUser = (
    id:
      | keyof TableServiceInterface
      | 'totalProjects'
      | 'percentage'
      | 'employees',
  ) => {
    switch (id) {
      case 'totalProjects': {
        return '107px';
      }
      case 'percentage': {
        return '127px';
      }
      case 'employees': {
        return '184px';
      }
      default: {
        return 'auto';
      }
    }
  };

  const handleSortUser = () =>
    sortUser.sortDirection === 'asc' ? (
      <ArrowDropDownIcon sx={{fontSize: '15px'}} />
    ) : (
      <ArrowDropUpIcon sx={{fontSize: '15px'}} />
    );
  /** handle sort field **/
  const handleSortField = ({headCell}: {headCell: HeadCell}) => {
    if (headCell.id === 'user') {
      return 'firstName';
    } else return headCell.id;
  };
  /** handle sort direction **/
  const handleSortDirection = ({
    sortUser,
  }: {
    sortUser: ISortFieldAndDirUser;
  }) => {
    if (sortUser.sortDirection === 'asc') {
      return 'desc';
    } else return 'asc';
  };
  return (
    <TableHead>
      <TableRow sx={{p: '8px 16px'}}>
        {headCells.map(headCell => (
          <TableCell
            key={headCell.id}
            width={handleWidthTableUser(headCell.id)}
            onClick={e =>
              ['totalProjects', 'percentage'].indexOf(headCell.id) > -1
                ? e.stopPropagation()
                : handleSortAndDir(
                    handleSortField({headCell}),
                    handleSortDirection({sortUser}),
                  )
            }
            align={'left'}
            sx={{
              cursor: 'pointer',
            }}>
            <TableSortLabel
              hideSortIcon={
                headCell.id !==
                (['firstName', 'lastName'].indexOf(sortUser.sortField) > -1
                  ? 'user'
                  : sortUser.sortField)
              }
              direction={'desc'}
              IconComponent={handleSortUser}>
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
}

type ITable = {
  id: string;
  user: string;
  project: number;
  percentage: number;
};

type ITableUser = {
  currentData: DocumentTableInterface | undefined;
  setSortUser: React.Dispatch<React.SetStateAction<ISortFieldAndDirUser>>;
  sortUser: ISortFieldAndDirUser;
};

export default function TableUsers(props: ITableUser) {
  const {currentData, setSortUser, sortUser} = props;
  const [table, setTable] = React.useState<Array<ITable>>([]);

  React.useMemo(() => {
    if (currentData) {
      const newMap: Array<ITable> = currentData.contents.map((item, index) => {
        const {firstName, lastName, percentage, totalProjects} = item;

        return {
          id: (index + 1).toString(),
          user: `${firstName} ${lastName ?? ''}`,
          project: totalProjects,
          percentage,
        };
      });

      setTable(newMap);
    }
  }, [currentData]);

  return (
    <TableContainer
      sx={{
        height: '290px',
        '&::-webkit-scrollbar': {
          width: '0.1em',
        },

        '&::-webkit-scrollbar-thumb': {
          backgroundColor: 'grey',
        },
      }}>
      <Table aria-labelledby="tableTitle" size={'small'} stickyHeader>
        <EnhancedTableHead sortUser={sortUser} setSortUser={setSortUser} />

        <TableBody>
          {table.map(item => {
            return (
              <TableRow
                hover
                key={item.id}
                role="checkbox"
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
                  <Stack direction={'row'} alignItems={'center'} gap="15px">
                    <NGText
                      text={item.id}
                      myStyle={{
                        fontWeight: 500,
                        fontSize: '11px',
                        width: '16px',
                      }}
                    />
                    <NGGroupAvatar
                      character={[
                        `${item.user.split(' ')[0].charAt(0)}${item.user
                          .split(' ')[1]
                          .charAt(0)}`,
                      ]}
                    />
                    <NGText
                      text={item.user}
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
                  id={`${item.project}-${item.id}`}
                  scope="row"
                  padding="none">
                  <Stack direction={'row'}>
                    <NGText
                      text={item.project}
                      myStyle={{
                        fontWeight: 500,
                        fontSize: '11px',
                        width: '16px',
                      }}
                    />
                  </Stack>
                </TableCell>
                <TableCell
                  component="th"
                  sx={{
                    px: '16px',
                  }}
                  id={`${item.project}-${item.id}`}
                  scope="row"
                  padding="none">
                  <Stack direction={'row'}>
                    <NGText
                      text={
                        (typeof item.percentage === 'string'
                          ? 0
                          : item.percentage.toFixed(0)) + '%'
                      }
                      myStyle={{
                        fontWeight: 500,
                        fontSize: '11px',
                        width: '16px',
                      }}
                    />
                  </Stack>
                </TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
