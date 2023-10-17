import {createSlice} from '@reduxjs/toolkit';
import type {PayloadAction} from '@reduxjs/toolkit';

import {initialState} from '@/redux/counter/Type/initialState';
import {
  TypeOption,
  TypeRecipientInfo,
  TypeUserInfo,
} from '@/redux/counter/Type/Type';
import {ProjectData} from '@/utils/request/interface/Project.interface';
import {NGTableComponentInterface} from '@components/ng-table/TableDashboard/NGTableComponent';
import {Dayjs} from 'dayjs';

export const counterSlice = createSlice({
  name: 'counter',
  initialState,
  reducers: {
    resetOptionT: state => {
      const {optionT} = initialState;

      state.optionT = optionT;
    },
    increment: state => {
      state.valueT += 1;
    },
    decrement: state => {
      state.valueT -= 1;
    },

    incrementByAmount: (state, action: PayloadAction<number>) => {
      state.valueT += action.payload;
    },
    setOption: (state, action: PayloadAction<TypeOption>) => {
      state.optionT = action.payload;
    },
    setUserInfo: (state, action: PayloadAction<TypeUserInfo>) => {
      state.userInfo = action.payload;
    },
    setRecipientInfo: (state, action: PayloadAction<TypeRecipientInfo>) => {
      state.RecipientInfo = action.payload;
    },
    setDataTable: (state, action: PayloadAction<ProjectData>) => {
      state.dataTable = action.payload;
    },
    setDataTableComponent: (state, action: PayloadAction<any>) => {
      state.dataTableComponent = action.payload;
    },
    setVisibleRows: (
      state,
      action: PayloadAction<NGTableComponentInterface[]>,
    ) => {
      state.visibleRows = action.payload;
    },
    setStartEndDate: (
      state,
      action: PayloadAction<{
        start?: Dayjs | null;
        end?: Dayjs | null;
        active?: number | string;
      }>,
    ) => {
      state.startEndDate = action.payload;
    },
  },
});
export const {setOption, setRecipientInfo, setStartEndDate} =
  counterSlice.actions;
// Other code such as selectors can use the imported `RootState` type

export default counterSlice.reducer;
