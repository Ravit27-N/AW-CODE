

export interface IGraphItem {
  name: string;
  value: string;
}

export interface IGraphCannelEnvoyResult {
  result: IGraphItem[]
}

export interface IGraphDepositModeResult {
  result: IGraphItem[]
}

interface IColumnMetadata {
  col: string;
  label: string;
  type: 'text' | 'number' | 'percent'
}

interface IGraphRowData {
  [key: string]: any
}

export interface IGraphFlowTrackingResult {
  metaData: IColumnMetadata[];
  result: IGraphRowData[];
}

export interface IGraphEvolution {
  name: string;
  series: Array<{ name: string, value: number }>;
}

export interface IGraphEvolutionResult {
  result: IGraphEvolution[];
}

export interface IAsyncLoader<T> {
  fetching: boolean;
  isError: boolean;
  error: any;
  data: T | null
}

export interface IUpdateUserObject {
  selectDateType: number,
  customStartDate: string,
  customEndDate: string
}

export interface DashboardGraphModel {
  requestedAt: Date
}
