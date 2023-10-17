import * as React from 'react';
import {Sort} from '@/constant/NGContant';
import {NGTableParticipantTypeCreateData} from '@components/ng-table/TableParticipaint/resource/Type';

export interface NGTableParticipantInterface {
  numSelected: number;
  onRequestSort: (event: React.MouseEvent<unknown>, newOrderBy: string) => void;

  onSelectAllClick: (event: React.ChangeEvent<HTMLInputElement>) => void;
  order: Sort;
  orderBy: string;
  rowCount: number;
  headCells: readonly HeadCell[];
}

export interface HeadCell {
  disablePadding: boolean;
  id: keyof NGTableParticipantTypeCreateData;
  label:
    | 'order'
    | 'name'
    | 'role'
    | 'invitation-status'
    | 'file-status'
    | 'actions';
  numeric: boolean;
}
export const headCells_participant: readonly HeadCell[] = [
  {
    id: 'order',
    numeric: false,
    disablePadding: true,
    label: 'order',
  },
  {
    id: 'nom',
    numeric: true,
    disablePadding: false,
    label: 'name',
  },
  {
    id: 'role',
    numeric: true,
    disablePadding: false,
    label: 'role',
  },
  {
    id: 'invitation',
    numeric: true,
    disablePadding: false,
    label: 'invitation-status',
  },
  {
    id: 'status',
    numeric: true,
    disablePadding: false,
    label: 'file-status',
  },
  {
    id: 'action',
    numeric: true,
    disablePadding: false,
    label: 'actions',
  },
];
