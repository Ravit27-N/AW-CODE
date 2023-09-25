import { Injectable } from '@angular/core';
import { ConfigurationService } from '@cxm-smartflow/shared/data-access/api';
import { templateEnv as env } from '@env-cxm-template';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  serverUrl: string;

  public connect() {
    const socket = new SockJS(this.serverUrl);
    const stompClient = Stomp.over(socket);
    return stompClient;
  }

  constructor(configuration: ConfigurationService) {
    const settings = configuration.getAppSettings();
    this.serverUrl = settings.apiGateway + env.socketPrefix;
  }
}
