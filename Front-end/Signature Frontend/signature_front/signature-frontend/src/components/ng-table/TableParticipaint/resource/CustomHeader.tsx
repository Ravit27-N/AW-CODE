import {Ascending, Descending} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {pixelToRem} from '@/utils/common/pxToRem';
import {NGTableParticipantInterface} from '@components/ng-table/TableParticipaint/resource/THeader';
import {NGTableParticipantTypeCreateData} from '@components/ng-table/TableParticipaint/resource/Type';
import NGText from '@components/ng-text/NGText';
import Box from '@mui/material/Box';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import {visuallyHidden} from '@mui/utils';
import i18next from 'i18next';
import * as React from 'react';
import {useTranslation} from 'react-i18next';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';

export function EnhancedTableHead(props: NGTableParticipantInterface) {
  const {order, orderBy, onRequestSort, headCells} = props;
  const createSortHandler =
    (newOrderBy: keyof NGTableParticipantTypeCreateData) =>
    (event: React.MouseEvent<unknown>) => {
      onRequestSort(event, newOrderBy);
    };
  const {t} = useTranslation();
  const iconSort = () => {
    if (order === 'asc') {
      return <ArrowDropDownIcon sx={{fontSize: '15px'}} />;
    } else {
      return <ArrowDropUpIcon sx={{fontSize: '15px'}} />;
    }
  };
  return (
    <TableHead>
      <TableRow>
        {headCells.slice(0, 1).map(headCell => (
          <TableCell
            sx={{fontWeight: 900, textAlign: 'center'}}
            key={headCell.id}
            align={headCell.numeric ? 'left' : 'right'}
            padding={headCell.disablePadding ? 'none' : 'normal'}>
            <TableSortLabel
              hideSortIcon={orderBy !== headCell.id}
              IconComponent={iconSort}
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
                myStyle={{
                  fontWeight: 500,
                  fontSize: pixelToRem(12),
                  lineHeight: pixelToRem(24),
                }}
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
              hideSortIcon={orderBy !== headCell.id}
              IconComponent={iconSort}
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
                myStyle={{
                  fontWeight: 500,
                  fontSize: pixelToRem(12),
                  lineHeight: pixelToRem(24),
                }}
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
              myStyle={{
                fontWeight: 500,
                fontSize: pixelToRem(12),
                lineHeight: pixelToRem(24),
              }}
            />
          </TableCell>
        ))}

        {/*<TCell_header headCells={headCells} />*/}
      </TableRow>
    </TableHead>
  );
}
