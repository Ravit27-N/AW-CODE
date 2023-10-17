import {DataGrid, GridColDef, GridValueGetterParams} from '@mui/x-data-grid';
import NGText from '@components/ng-text/NGText';
import Stack from '@mui/material/Stack';
import {pixelToRem} from '@/utils/common/pxToRem';
import NGGroupAvatar from '@components/ng-group-avatar/NGGroupAvatar';
import {shortName} from '@/utils/common/SortName';

export interface TableDocumentInterface {
  id: number;
  name: string;
  projects: number;
  percentage: number | string;
}

const NGDataGridDocuments = ({widthT, rows}: {widthT: number; rows: any}) => {
  const width = widthT / 3;
  const columns: GridColDef[] = [
    /* field that hide*/
    {field: 'id', headerName: 'ID', width: 0},
    {
      field: 'name',
      headerName: 'name',
      type: 'string',
      renderCell: param => {
        return <NGText text={param.row.id} />;
      },
      width: 0,
    },
    /* ====================== field that show in table*/
    /* header id and name as user*/
    {
      field: 'users',
      headerName: 'Nom',
      description: 'This column has a value getter and is not sortable.',
      sortable: true,
      headerAlign: 'left',
      align: 'left',
      type: 'number',
      sortingOrder: ['asc', 'desc'],
      renderCell: param => {
        return (
          <Stack
            direction={'row'}
            spacing={pixelToRem(20)}
            pl={pixelToRem(2)}
            py={2}
            alignItems={'center'}>
            <NGText
              text={param.id}
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
              <NGGroupAvatar character={[shortName(param.row.name)]} />
              <NGText
                text={param.row.name}
                myStyle={{
                  fontSize: pixelToRem(12),
                  lineHeight: pixelToRem(16),
                  fontWeight: 500,
                }}
              />
            </Stack>
          </Stack>
        );
      },
      valueGetter: (params: GridValueGetterParams) => `${params.row.id || 0} `,
      width,
    },
    /* header projects*/
    {
      field: 'projects',
      headerName: 'Projects',
      headerAlign: 'center',
      align: 'center',
      type: 'number',
      width,
    },
    /* header percentage*/
    {
      field: 'percentage',
      headerName: 'Percentage',
      headerAlign: 'right',
      align: 'right',
      type: 'number',
      width,
    },
  ];
  return (
    <DataGrid
      rows={rows}
      columns={columns}
      scrollbarSize={2}
      initialState={{
        columns: {
          columnVisibilityModel: {
            /*Hide field that you want example: id: false*/
            id: false,
            name: false,
          },
        },
      }}
      getCellClassName={() => {
        return 'styleRow';
      }}
      // pageSizeOptions={[5, 10, 15]}
      // checkboxSelection
      /* disable selected row*/
      onCellClick={data => {
        return data;
      }}
      disableColumnMenu
    />
  );
};

export default NGDataGridDocuments;
