import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  deleteResourceOrTemptFile,
  fetchListResourceCriteria,
  filterTypeBoxChange, getTranslationMsg,
  resetCreationForm,
  searchBoxChange, selectIsCloseModal,
  selectIsSearchBoxError,
  selectResourceCriteria
} from '@cxm-smartflow/setting/data-access';
import {BehaviorSubject, Observable, Subscription} from 'rxjs';
import { ManageResourcePopupService } from './feature-manage-resource-popup/manage-resource-popup.service';
import { filter, take } from 'rxjs/operators';
import { CanAccessibilityService } from '@cxm-smartflow/shared/data-access/services';
import { LibraryResourceManagement } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-featured-list-resource',
  templateUrl: './featured-list-resource.component.html',
  styleUrls: ['./featured-list-resource.component.scss'],
})
export class FeaturedListResourceComponent {
  isFilterBoxError$: Observable<any>;
  resourceCriteria$: Observable<any>;
  private _unsubscription: Subscription;
  isCanCreate = false;
  searchBoxValue$ = new BehaviorSubject<string>('');
  constructor(
    private readonly _store$: Store,
    private readonly canAccessibilityService: CanAccessibilityService,
    private readonly _managePopup: ManageResourcePopupService
  ) {
    this.isCanCreate = this.canAccessibilityService
      .getUserRight(LibraryResourceManagement.CXM_MANAGEMENT_LIBRARY_RESOURCE, LibraryResourceManagement.CREATE, true);
    this._store$.dispatch(getTranslationMsg());
    this.isFilterBoxError$ = this._store$.select(selectIsSearchBoxError);
    this._store$.dispatch(fetchListResourceCriteria());
    this.resourceCriteria$ = this._store$.select(selectResourceCriteria);
    this._unsubscription = this._store$.select(selectIsCloseModal)
      .pipe(filter(e => e))
      .subscribe(() => {
      this._managePopup.close();
    });
  }

  createUserEvent() {
    this._managePopup
      .show({ formType: 'create' })
      .pipe(take(1))
      .subscribe((data) => {
        if (data === false) {
          this._store$.dispatch(deleteResourceOrTemptFile({ deleteType: 'temp' }));
        }
        this._store$.dispatch(resetCreationForm());
      });
  }

  filterClientBoxChange($event: any[]) {
    this._store$.dispatch(filterTypeBoxChange({ types: $event }));
  }

  searchTermChanged(filter: string) {
    this.searchBoxValue$.next(filter);
    this._store$.dispatch(searchBoxChange({ filter }));
  }
}
