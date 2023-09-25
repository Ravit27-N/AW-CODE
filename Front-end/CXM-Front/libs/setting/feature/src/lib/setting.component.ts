import { Component, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { clearAllStatesInResource, getTranslationMsg } from '@cxm-smartflow/setting/data-access';

@Component({
  selector: 'cxm-smartflow-setting',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.scss'],
})
export class SettingComponent implements OnDestroy {
  constructor(private _store: Store) {
    this._store.dispatch(getTranslationMsg());
  }

  ngOnDestroy(): void {
    this._store.dispatch(clearAllStatesInResource());
  }
}
