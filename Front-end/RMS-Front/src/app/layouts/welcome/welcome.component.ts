import { Component, OnInit } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { UserService } from 'src/app/auth';
import { CompanyProfileService } from 'src/app/core';
import { getAssetPrefix } from '../../shared/utils/asset-util';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css'],
})
export class WelcomeComponent implements OnInit {
  isAuthenticated: boolean;
  isDone: boolean;
  isRoleDone: boolean;
  companyProfile: string;

  assetPrefix = getAssetPrefix();
  constructor(
    private auth: OAuthService,
    private userService: UserService,
    private company: CompanyProfileService,
  ) {
    this.userService.isAuthenticated$.subscribe(
      (b) => (this.isAuthenticated = b),
    );
    this.userService.isDoneLoading$.subscribe((b) => (this.isDone = b));
    this.userService.isRoleloaded$.subscribe((b) => (this.isRoleDone = b));
  }
  ngOnInit(): void {
    this.company
      .getData()
      .subscribe((x) => (this.companyProfile = x.description));
  }

  login(): void {
    this.auth.initCodeFlow();
  }
}
