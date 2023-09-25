export interface ServiceProviderFormModel {
  MAIL: Array<ServiceProvider>;
  SMS: Array<ServiceProvider>;
}


export interface ServiceProviderDto {
  customer: string;
  MAIL: Array<ServiceProvider>;
  SMS: Array<ServiceProvider>;
}

export interface ServiceProviderRequest {
  customer: string;
  MAIL: Array<ServiceProviderChannelRequest>;
  SMS: Array<ServiceProviderChannelRequest>;
}

export interface ServiceProviderChannelRequest {
  id: number;
  priority: number;
}


export interface ServiceProvider {
  id: number;
  priority: number;
  label: string;
  deletable: boolean;
}
