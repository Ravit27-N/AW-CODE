export interface UnloadModel {
  id: number;
  dayOfWeek: string;
  enabled: boolean;
  time: string;
  hour: number;
  minute: number;
  zoneId: string;
}

export interface DayOfWeekUnloadingModel {
  label: string;
  check: boolean;
  hours: string[];
}
