import { Injectable } from '@angular/core';
// import { smartflowEnv as env } from '@env-cxm-smartflow';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface IServiceVersion {
  serviceName: string;
  instances: Array<{
    id: string;
    version: string;
    description: string;
  }>;
}

@Injectable({
  providedIn: 'root',
})
export class CxmSmartflowService {
  // private baseURL = env.apiURL;

  getVersion(): Observable<IServiceVersion[]> {
    return this.apiService.get<IServiceVersion[]>(
      '/microservice-info'
    );
  }

  downloadPrivacyDoc(): Promise<{
    content: string;
    fileSize: number;
    filename: string;
  }> {
    return this.apiService
      .get<{ content: string; fileSize: number; filename: string }>(
        '/cxm-profile/api/v1/storage/download'
      )
      .toPromise();
  }

  validateTicket(ticket: string) {
    return this.apiService.post('/cxm-process-control/api/v1/public/iv/token', { hash: ticket });
  }

  constructor(private apiService: ApiService) {}
}
