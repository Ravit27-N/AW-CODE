import { Component, Input, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectNameForm } from '@cxm-smartflow/profile/data-access';

export declare type  ProfileHeaderType =
  | 'listProfiles'
  | 'createNewProfile'
  | 'modifyProfile';

@Component({
  selector: 'cxm-smartflow-profile-header',
  templateUrl: './profile-header.component.html',
  styleUrls: ['./profile-header.component.scss']
})
export class ProfileHeaderComponent implements OnDestroy {
  @Input() type: ProfileHeaderType = 'listProfiles';
  @Input() modifyValue: string;

  constructor(private store: Store) {
    this.store.select(selectNameForm).subscribe(response => {
      const { name, displayName } = response;
      this.modifyValue = name;
    });
  }

  ngOnDestroy(): void {
    this.store.complete();
  }
}
