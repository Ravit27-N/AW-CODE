import { CalendarOptionModel } from './calendar.model';
import { FillerGroupsModel } from './filler-groups.model';

export interface FilterOptionModel {
  channels: string[];
  categories: string[];
  standAloneCategory: string[];
  calendar: CalendarOptionModel;
  fillers: string[];
  fillerSearchTerm: string;
  fillersGroup: FillerGroupsModel[];
}

export interface FilterOptionParam {
  exportingType?: string;
  channels: string[];
  categories: string[];
  startDate: string;
  endDate: string;
  fillers: string[];
  searchByFiller: string;
  requestedAt: string;
}

export interface ReportingPostalParams {
  timeZone?: string;
  exportingType?: string;
  channels: string[];
  requestedAt: string;
  categories: string[];
  startDate: string;
  endDate: string;
  fillers: string | string[];
  searchByFiller: string;
  secondFillerKey: string;
  secondFillerText: string;
  thirdFillerKey: string;
  thirdFillerText: string;
}
