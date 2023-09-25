import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { MailconfigService } from '../../core';
import {SystemConfigurationList} from '../../core/model/MailconfigFormModel';

@Injectable()
export class MailconfigResolver implements Resolve<SystemConfigurationList> {

  constructor(private mailconfigService: MailconfigService) {

  }

  resolve(): Observable<SystemConfigurationList> {
    return this.mailconfigService.getConfigList();
  }

}
