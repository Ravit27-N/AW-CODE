import { Component, OnInit } from '@angular/core';
import * as packageInfo from '../../../../../package.json';
import { combineDynamicVersion, CustomFooterModel } from '@cxm-smartflow/shared/common-typo';
import { TranslateService } from '@ngx-translate/core';
import { LoginGuard } from '@cxm-smartflow/auth/data-access';

@Component({
  selector: 'cxm-smartflow-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit {

  platformVersion = '';
  leftFooters: CustomFooterModel [] = [];
  rightFooters: CustomFooterModel [] = [];

  constructor(private translate: TranslateService, private loginGuard: LoginGuard) {
    if(this.loginGuard.isLogged()){
      this.translate.get('footerAll').subscribe(response => {
        this.leftFooters = combineDynamicVersion(response?.left) || [];
        this.rightFooters = response?.right;
      });
    }else{
      this.translate.get('footerLogin').subscribe(response => {
        this.leftFooters = combineDynamicVersion(response?.left) || [];
        this.rightFooters = response?.right;
      });
    }
  }

  ngOnInit(): void {
    const { version } = packageInfo;
    this.platformVersion = version;
  }

 }
