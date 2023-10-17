import {AutoReminder, ChannelOptions} from '@/constant/NGContant';
import {TypeTableComponent} from '@/redux/counter/Type/Type';
import {NGTableComponentInterface} from '@components/ng-table/TableDashboard/NGTableComponent';
import {Dayjs} from 'dayjs';

export interface CounterState {
  valueT: number;
  optionT: {
    opt1: string;
    opt2: ChannelOptions;
    opt3: AutoReminder | null;
    opt4: Date;
    title: string;
    message: string;
    checkDate?: boolean;
    docName?: string;
  };
  userInfo: {
    name: string;
    projectName?: string;
    idProject?: number | string;
  };
  RecipientInfo: {
    name?: string;
    firstName?: string;
    role?: string;
    email?: string;
    phoneNumber?: string;
    id?: number | string;
    invitationStatus?: string;
    sortOrder?: number;
    projectId?: number | string;
  };
  dataTable: any;
  data: number[];
  dataTableComponent: TypeTableComponent[];
  visibleRows: NGTableComponentInterface[];
  startEndDate: {
    start?: Dayjs | null;
    end?: Dayjs | null;
    active?: number | string;
  };
}
