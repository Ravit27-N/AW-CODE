import { Component, OnInit } from '@angular/core';
import { CxmSmartflowService, IServiceVersion } from '@cxm-smartflow/shared/data-access/api';
import { BehaviorSubject, Observable } from 'rxjs';
import { LoginGuard } from '@cxm-smartflow/auth/data-access';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'cxm-smartflow-version',
  templateUrl: './version.component.html',
  styleUrls: ['./version.component.scss']
})

export class VersionComponent implements OnInit {

  services$ = new BehaviorSubject<IServiceVersion[]>([]);
  displayColumns: string [] = ['service', 'version'];

  ngOnInit(): void {

    if (this.loginGuard.isLogged()) {
      this.cxmSmartflowService.getVersion().subscribe(response => this.services$.next(response));
    } else {
      this.oAuthService.logOut(false);
      this.cxmSmartflowService.getVersion().subscribe(response => this.services$.next(response));
    }
  }

  constructor(private cxmSmartflowService: CxmSmartflowService, private loginGuard: LoginGuard, private oAuthService: OAuthService) {}

}
