import { Component, OnInit } from '@angular/core';
import {
  Confirmable,
  selectDefinitionDirectoryFormHasChanged,
} from '@cxm-smartflow/definition-directory/data-access';
import { Store } from '@ngrx/store';
import { take } from "rxjs/operators";
import {ConfirmationMessageService} from "@cxm-smartflow/shared/ui/comfirmation-message";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'cxm-smartflow-feature-create-directory',
  templateUrl: './feature-create-directory.component.html',
  styleUrls: ['./feature-create-directory.component.scss'],
})
export class FeatureCreateDirectoryComponent implements OnInit, Confirmable {
  constructor(private store: Store,
              private translateService: TranslateService,
              private confirmationMessageService: ConfirmationMessageService) {}

  ngOnInit(): void {}

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
