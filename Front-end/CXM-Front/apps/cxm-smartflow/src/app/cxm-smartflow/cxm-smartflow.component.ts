import { Component, OnInit } from '@angular/core';
import {
  CanAccessibilityService,
} from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import {
  combineDynamicVersion,
  CustomFooterModel,
} from '@cxm-smartflow/shared/common-typo';
import { CxmSmartflowService } from '@cxm-smartflow/shared/data-access/api';
import { FileSaverUtil, Base64Model } from '@cxm-smartflow/shared/utils';
import {BehaviorSubject } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-cxm-smartflow',
  templateUrl: './cxm-smartflow.component.html',
  styleUrls: ['./cxm-smartflow.component.scss'],
})
export class CxmSmartflowComponent implements OnInit {
  leftFooters: CustomFooterModel[] = [];
  rightFooters: CustomFooterModel[] = [];
  hasActiveAccount$ = new BehaviorSubject<boolean>(false);
  constructor(
    private translate: TranslateService,
    private canAccess: CanAccessibilityService,
    private cxmService: CxmSmartflowService,
    private base64Converter: FileSaverUtil
  ) {
    this.hasActiveAccount$.next(canAccess.hasActiveAccount())
  }

  ngOnInit(): void {
    //For Translation
    this.translate.use(localStorage.getItem('locale') || 'fr' );

    this.translate.get('footerAll').toPromise().then(response => {
      this.leftFooters = combineDynamicVersion(response?.left) || [];
      this.rightFooters = response?.right;
    });
  }

  async downloadPrivacyDoc($event: boolean): Promise<void> {
    if ($event) {
      const base64 = await this.cxmService.downloadPrivacyDoc() as Base64Model;
      if (!base64) return;
      // Download file.
      this.base64Converter.downloadBase64(base64.content, base64.filename);
    }
  }
}
