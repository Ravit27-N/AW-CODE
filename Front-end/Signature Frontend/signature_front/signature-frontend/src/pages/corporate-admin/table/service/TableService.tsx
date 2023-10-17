import NGGroupAvatar from '@/components/ng-group-avatar/NGGroupAvatar';
import {Localization} from '@/i18n/lan';
import {
  ISortField,
  ServiceTemplatesInterface,
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
import {ISortFieldAndDirService} from '../../Home.corporate';

export interface TableServiceInterface {
  id: string;
  unitName: string;
  employees: string[];
  totalProjects: number;
  percentage: number | string;
}

interface HeadCell {
  id: keyof TableServiceInterface;
  label: string;
}

type IEnhancedTableHead = {
  setSortService: React.Dispatch<React.SetStateAction<ISortFieldAndDirService>>;
  sortService: ISortFieldAndDirService;
};

function EnhancedTableHead(props: IEnhancedTableHead) {
  const {setSortService, sortService} = props;
  const headCells: readonly HeadCell[] = [
    {
      id: 'unitName',
      label: t(Localization('table', 'service')),
    },
    {
      id: 'employees',
      label: t(Localization('table', 'members')),
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
    sortField: ISortField,
    sortDirection: 'asc' | 'desc',
  ) => {
    setSortService(prev => ({
      ...prev,
      sortField,
      sortDirection,
    }));
  };
  const handleWidthTableService = (
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
  const handleSortService = () => {
    return sortService.sortDirection === 'asc' ? 'desc' : 'asc';
  };
  const handleIconSort = () =>
    sortService.sortDirection === 'asc' ? (
      <ArrowDropDownIcon sx={{fontSize: '15px'}} />
    ) : (
      <ArrowDropUpIcon sx={{fontSize: '15px'}} />
    );
  return (
    <TableHead>
      <TableRow sx={{p: '8px 16px'}}>
        {headCells.map(headCell => (
          <TableCell
            width={handleWidthTableService(headCell.id)}
            onClick={e =>
              ['totalProjects', 'percentage', 'employees'].indexOf(
                headCell.id,
              ) > -1
                ? e.stopPropagation()
                : handleSortAndDir(headCell.id, handleSortService())
            }
            key={headCell.id}
            align={'left'}
            sx={{
              cursor: 'pointer',
            }}>
            <TableSortLabel
              hideSortIcon={headCell.id !== sortService.sortField}
              direction={'desc'}
              IconComponent={handleIconSort}>
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
  service: string;
  members: string[];
  project: number;
  percentage: string | number;
};

type ITableService = {
  currentData: ServiceTemplatesInterface | undefined;
  setSortService: React.Dispatch<React.SetStateAction<ISortFieldAndDirService>>;
  sortService: ISortFieldAndDirService;
};

export default function TableService(props: ITableService) {
  const {currentData, setSortService, sortService} = props;
  const [table, setTable] = React.useState<Array<ITable>>([]);

  React.useMemo(() => {
    if (currentData) {
      const newMap: Array<ITable> = currentData.contents.map((item, index) => {
        const {employees, totalProjects, percentage, unitName} = item;

        return {
          id: (index + 1).toString(),
          service: unitName,
          members: employees.map(i => {
            const {firstName, lastName} = i;

            return `${firstName} ${lastName ?? ''}`;
          }),
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
      <Table aria-label="sticky table" size={'small'} stickyHeader>
        <EnhancedTableHead
          setSortService={setSortService}
          sortService={sortService}
        />

        <TableBody>
          {table.map(item => {
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
                  <Stack direction={'row'}>
                    <NGText
                      text={item.id}
                      myStyle={{
                        fontWeight: 500,
                        fontSize: '11px',
                        width: '16px',
                      }}
                    />
                    <NGText
                      text={item.service}
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
                  id={`${item.members.length}-${item.id}`}
                  scope="row"
                  padding="none">
                  <Stack direction={'row'}>
                    <NGGroupAvatar
                      character={item.members.map(
                        name =>
                          `${name.split(' ')[0].charAt(0)}${name
                            .split(' ')[1]
                            .charAt(0)}`,
                      )}
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
