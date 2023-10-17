import {CounterState} from '@/redux/counter/Type/CouterState';

export const initialState: CounterState = {
  valueT: 0,
  optionT: {
    opt1: '',
    opt2: 1,
    opt3: null,
    opt4: new Date(),
    title: '',
    message: '',
    checkDate: false,
    docName: '',
  },
  userInfo: {
    name: 'Davis Juan',
  },
  dataTable: [],
  data: [12, 23, 34],
  RecipientInfo: {},
  dataTableComponent: [],
  visibleRows: [],
  startEndDate: {start: null, end: null, active: 0},
};
