import {sendMailForm} from '@cxm-smartflow/shared/data-access/model';
import { Store } from '@ngrx/store';
// eslint-disable-next-line @nrwl/nx/enforce-module-boundaries
import {getSetFeaturedSetting} from '@cxm-smartflow/manage-my-campaign/data-access';
import { Component } from '@angular/core';
import { pipe } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-show-comfirmation',
  templateUrl: './show-comfirmation.component.html',
  styleUrls: ['./show-comfirmation.component.scss']
})
export class ShowComfirmationComponent {

  responseData: sendMailForm;
  constructor(private store: Store) {
    this.store.select(pipe(getSetFeaturedSetting)).subscribe((response) => {
      this.responseData = response?.response;
    })
  }

}
