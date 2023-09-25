import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import {
  Confirmable,
  getDefinitionDirectoryDetail,
  selectDefinitionDirectoryFormHasChanged
} from "@cxm-smartflow/definition-directory/data-access";
import {take} from "rxjs/operators";
import {TranslateService} from "@ngx-translate/core";
import {ConfirmationMessageService} from "@cxm-smartflow/shared/ui/comfirmation-message";

@Component({
  selector: 'cxm-smartflow-feature-edit-directory',
  templateUrl: './feature-edit-directory.component.html',
  styleUrls: ['./feature-edit-directory.component.scss'],
})
export class FeatureEditDirectoryComponent implements OnInit, Confirmable, OnDestroy {
  #subscription: Subscription = new Subscription();

  constructor(private store: Store,
              private translateService: TranslateService,
              private confirmationMessageService: ConfirmationMessageService,
              private activateRoute: ActivatedRoute) {}

  ngOnInit(): void {
    const subscription = this.activateRoute.queryParams.subscribe((data) => {
      this.store.dispatch(getDefinitionDirectoryDetail({ id: data.id }));
    });
    this.#subscription.add(subscription);
  }

  ngOnDestroy(): void {
    this.#subscription.unsubscribe();
  }

  async isLocked(): Promise<boolean> {
    const hasChanged = await this.store.select(selectDefinitionDirectoryFormHasChanged).pipe(take(1)).toPromise();
    const message = await this.translateService.get('directory.definition_directory_confirm_leave').toPromise();

    if (hasChanged) {
      return !await this.confirmationMessageService.showConfirmationPopup({
        type: 'Warning',
        title: message.title,
        message: message.message,
        confirmButton: message.quit,
        cancelButton: message.cancelBtn,
      }).toPromise();
    }

    return false;
  }
}
