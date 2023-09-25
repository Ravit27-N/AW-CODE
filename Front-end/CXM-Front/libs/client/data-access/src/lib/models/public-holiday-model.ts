export interface PublicHolidayModel {
  id: number;
  label: string;
  isFixedDate: boolean;
  eventDate: string;
  publicHolidayDetails: Array<PublicHolidayDetails>;
}

export interface PublicHolidayDetails {
  id: number;
  date: Date;
}
