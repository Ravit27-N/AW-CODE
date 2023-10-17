import NGGroupAvatar from '@/components/ng-group-avatar/NGGroupAvatar';
import {ProjectStatusInterfaces} from '@/components/ng-switch-case-status/interface';
import {NGProjectStatus} from '@/components/ng-switch-case-status/NGProjectStatus';
import NGText from '@/components/ng-text/NGText';
import {Descending, Sort, UNKOWNERROR} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {ITableAllProject} from '@/pages/end-user/project-detail/empty/tabs/TabAllProject';
import {useAppSelector} from '@/redux/config/hooks';
import {useLazyGetCorporateProjectsQuery} from '@/redux/slides/project-management/project';
import {HandleException} from '@/utils/common/HandleException';
import {pixelToRem} from '@/utils/common/pxToRem';
import {$isarray} from '@/utils/request/common/type';
import {Signatory} from '@/utils/request/interface/Project.interface';
import {NGThreeDot} from '@assets/iconExport/Allicon';
import NgPopOver from '@components/ng-popover/NGPopOver';
import ButtonThreeDotDashboard from '@pages/corporate-admin/sidebar-corporate/signature-projects/components/TableProjectDetail/ButtonThreeDotDashboard';
import {
  HtmlTooltip,
  styleInTable,
} from '@components/ng-table/TableDashboard/resource/TCell';
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
import {useTranslation} from 'react-i18next';
import {useSnackbar} from 'notistack';
import React, {useState} from 'react';
import {Waypoint} from 'react-waypoint';
import HoverOnAvtar from '@components/ng-table/TableDashboard/action/HoverOnAvatar';
import {Order} from '@components/ng-table/TableDashboard/NGTableComponent';
import {convertUTCToLocalTimeCN} from '@/utils/common/ConvertDatetoSecond';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import {useParams} from 'react-router-dom';
import {FigmaBody} from '@/constant/style/themFigma/Body';

type IDataRow = {
  id: string;
  name: string;
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
  templateName: string;
  documents: object[];
  status: string;
  expireDate: number;
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
  const {t} = useTranslation();
  const headCells: readonly HeadCell[] = [
    {
      id: 'name',
      numeric: false,
      disablePadding: true,
      label: t(Localization('table', 'signature project')),
    },
    {
      id: 'templateName',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'model')),
    },
    {
      id: 'documents',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'documents')),
    },
    {
      id: 'signatories',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'participants')),
    },
    {
      id: 'status',
      numeric: true,
      disablePadding: false,
      label: t(Localization('table', 'completions')),
    },
    {
      id: 'expireDate',
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

const TableProjectDetail = (props: ITableAllProject & {search: string}) => {
  /** selectOption: When they change it data in table of project must change  **/
  const {tab, selectOption, search, arrayOfStatus} = props;
  const {t} = useTranslation();
  const {theme} = useAppSelector(state => state.enterprise);
  const {enqueueSnackbar, closeSnackbar} = useSnackbar();
  const [order, setOrder] = useState<Sort>(Descending);
  const [orderBy, setOrderBy] = useState<keyof IDataRow>('createdAt');
  const [visibleRows, setVisibleRows] = useState<Array<IDataRow>>([]);
  const [selected, setSelected] = React.useState<readonly string[]>([]);
  const [page, setPage] = useState(1);
  const [rowsPerPage] = useState(10);
  const [status, setStatus] = useState<string[]>(['']);
  const startEndDate = useAppSelector(state => state.counter.startEndDate);
  /** searchProjectName use middle ware of search and fetching data again **/
  const {userId} = useParams();

  /** fetch data for get projects  **/
  const [trigger, result] = useLazyGetCorporateProjectsQuery();

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

  // handle get and set value to row
  const handleSetVisibleRows = async ({reset = false}) => {
    const resData = await trigger({
      page,
      pageSize: rowsPerPage,
      userId,
      statuses: status,
      search,
      sortDirection: order,
      sortField: orderBy,
      startDate:
        startEndDate.start === null || startEndDate.start === undefined
          ? ''
          : startEndDate.start.add(1, 'day').toISOString(),
      endDate:
        startEndDate.end === null || startEndDate.end === undefined
          ? ''
          : startEndDate.end.add(1, 'day').toISOString(),
    }).unwrap();

    if (resData.contents) {
      const {contents} = resData;
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
          createdBy,
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
          name,
          templateName: templateName ?? '-',
          document: documents?.length,
          participant: participants.length,
          dParticipant: participants,
          completion: status,
          dCompletion: checkStatus(status),
          deadline: expireDate!,
          signatories,
          docId: documents.length > 0 ? documents[0].id : 0,
          createdAt,
          status,
          expireDate,
          flowId,
          documents,
          createdBy,
        } as IDataRow;
      });

      if (reset) {
        // reset select
        setSelected([]);
        // don't append data
        setVisibleRows(newMap);
        return;
      }

      /** if newMap is Array and visibleRows also Array **/
      setVisibleRows([...visibleRows, ...newMap]);
    }
  };

  /** when multiple filter by checked status has change **/
  React.useEffect(() => {
    setPage(1);
    setStatus([...arrayOfStatus]);
    if (arrayOfStatus.length === 0) {
      if (tab === 'ALL-PROJECTS') setStatus(prev => [...prev, '']);
      else setStatus(prev => [...prev, tab]);
    }
  }, [arrayOfStatus.length]);

  /** validate data when change tab **/
  React.useEffect(() => {
    setPage(1);
    handleSetVisibleRows({reset: true});
  }, [search, userId, startEndDate, status]);

  /** validate data when change tab **/
  React.useEffect(() => {
    /** if tab change tab page must change to page one **/
    /** if tab all project active set it to ''  for show all project else follow by tab that we active  **/
    if (tab === 'ALL-PROJECTS') {
      setStatus(['']);
    } else {
      setStatus([tab]);
    }
  }, [tab]);

  React.useEffect(() => {
    if (page <= 1) {
      handleSetVisibleRows({reset: true});
    } else {
      handleSetVisibleRows({reset: false});
    }
  }, [page, rowsPerPage, order, orderBy, selectOption]);

  /** handler when something error with backend **/
  React.useEffect(() => {
    if (result.error) {
      enqueueSnackbar(
        HandleException((result.error as any).status) ?? UNKOWNERROR,
        {
          variant: 'errorSnackbar',
        },
      );
    }
    return () => closeSnackbar();
  }, [result.error]);

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
    setPage(1);
  };
  /** Skeleton for loading  **/
  if (result.isLoading) {
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
          {visibleRows.length <= 0 ? (
            <TableRow>
              <TableCell sx={{border: 'none', textAlign: 'center'}} colSpan={7}>
                <NGText
                  myStyle={{...FigmaBody.BodySmallBold}}
                  text={t(Localization('project-detail', 'no-result'))}
                />
              </TableCell>
            </TableRow>
          ) : (
            /** render to show data in table **/
            <>
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
                          text={row.name}
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
                        text={row.templateName}
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
                      <HtmlTooltip
                        title={<HoverOnAvtar data={row.signatories} />}>
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
                          contain={<ButtonThreeDotDashboard data={row} />}
                        />
                      </Stack>
                    </TableCell>
                  </TableRow>
                );
              })}
              {/** Waypoint will process when we scroll end of data in table for fetch more data **/}
              {visibleRows.length > 0 && result?.data?.hasNext && (
                <TableRow>
                  <TableCell>
                    <Waypoint onEnter={gotoNextPage} />
                  </TableCell>
                </TableRow>
              )}
            </>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default TableProjectDetail;
