import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { loadClientModule, ProfileStorageService, unloadClientModule } from '@cxm-smartflow/profile/data-access';

@Component({
  selector: 'cxm-smartflow-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnDestroy {

  constructor(
    private store: Store,
    private router: Router,
    private translate: TranslateService,
    private profileStorage: ProfileStorageService
  ) {}

  navigateTo() {
    this.router.navigateByUrl('/cxm-profile/users/list-user');
  }

  ngOnDestroy(): void {
    this.profileStorage.removeProfileListStorage();
    this.store.dispatch(unloadClientModule());
  }
}
