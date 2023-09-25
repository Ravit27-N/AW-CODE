export interface MailconfigFormModel {
  provider: string;
  adminEmail: string;
  config: {
    apikey?: string;
    server?: string;
    port?: number;
    username?: string;
    password?: string;
  };
}

export interface SystemConfiguration
{
  id?: number;
  configKey: string;
  configValue: string;
  description?: string;
  dateActivity?: string;
}

export interface SystemConfigurationList {
  contents: SystemConfiguration[];
  total: number;
  page: number;
  pageSize: number;
}
