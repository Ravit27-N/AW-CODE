import { Component, OnDestroy } from '@angular/core';
import { unloadUserlist } from '@cxm-smartflow/user/data-access';
import { Store } from '@ngrx/store';

@Component({
  selector: 'cxm-smartflow-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnDestroy {

  constructor(private store: Store) { }

  ngOnDestroy(): void {
    this.store.dispatch(unloadUserlist())
  }

}
