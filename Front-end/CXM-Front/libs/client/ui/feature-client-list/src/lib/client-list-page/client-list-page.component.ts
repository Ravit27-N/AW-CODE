import {
  AfterContentChecked,
  ChangeDetectorRef,
  Component, HostListener
} from '@angular/core';
import { Router } from '@angular/router';
import {
  appLocalStorageConstant,
  appRoute,
} from '@cxm-smartflow/template/data-access';
import { TranslateService } from '@ngx-translate/core';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { filterHistory } from '@cxm-smartflow/client/data-access';

@Component({
  selector: 'cxm-smartflow-client-list-page',
  templateUrl: './client-list-page.component.html',
  styleUrls: ['./client-list-page.component.scss'],
})
export class ClientListPageComponent implements AfterContentChecked {
  isAdmin = UserUtil.isAdmin();

  createClientHandler() {
    this.router.navigateByUrl(appRoute.cxmClient.navigateToCreateClient).then();
  }

  constructor(
    private translate: TranslateService,
    private router: Router,
    private ref: ChangeDetectorRef
  ) {
    this.translate.use(
      localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) ||
        appLocalStorageConstant.Common.Locale.Fr
    );
  }

  ngAfterContentChecked() {
    this.ref.detectChanges();
  }

  @HostListener("window:beforeunload", ["$event"])
  unloadHandler() {
    localStorage.removeItem(filterHistory);
  }
}
