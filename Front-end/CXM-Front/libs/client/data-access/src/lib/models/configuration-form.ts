export interface ConfigurationForm {
  name: string;
  order: number;
  content: string;
  entries: Entries[];
  modifiable: boolean;
  removable: boolean;
  draggable: boolean;
}

export interface Entries {
  key: string,
  value: string,
}

export interface ConfigurationFileModel {
  order: number;
  name: string;
  content: string;
}

export interface OrderModelPayload {
  clientName: string;
  models: ConfigurationFileModel[];
}

export interface ConfigurationsModel {
  name: string;
  order: number;
  entries: Entries[];
}
