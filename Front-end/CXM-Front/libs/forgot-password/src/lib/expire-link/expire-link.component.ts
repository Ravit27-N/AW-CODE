import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { combineDynamicVersion, CustomFooterModel } from '@cxm-smartflow/shared/common-typo';
import { Router } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-expire-link',
  templateUrl: './expire-link.component.html',
  styleUrls: ['./expire-link.component.scss']
})
export class ExpireLinkComponent{

  leftFooters: CustomFooterModel [] = [];
  rightFooters: CustomFooterModel [] = [];

  constructor(private translateService: TranslateService, private router: Router) {
    this.translateService.get('footerLogin').subscribe(response => {
      this.leftFooters = combineDynamicVersion(response?.left) || [];
      this.rightFooters = response?.right;
    });
  }

  backToLogin() {
    this.router.navigateByUrl('/login');
  }
}
