import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmationMessageComponent } from './confirmation-message.component';
import { ConfirmationMessageService } from './confirmation-message.service';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { ConfirmationPopupComponent } from './confirmation-popup/confirmation-popup.component';
import {
  SelectionCommentSnackbarComponent,
  SelectionSnackbarComponent,
  SelectionSnackbarService,
} from './selection-snackbar/selection-snackbar.component';
import {InformationPopUpComponent} from "./information-popup/information-pop-up.component";
import {ReactiveFormsModule} from "@angular/forms";
import {SharedDirectivesTooltipModule} from "@cxm-smartflow/shared/directives/tooltip";

@NgModule({
  imports: [
    CommonModule,
    SharedUiButtonModule,
    MaterialModule,
    TranslateModule,
    ReactiveFormsModule,
    SharedDirectivesTooltipModule,
  ],
  declarations: [
    ConfirmationMessageComponent,
    ConfirmationPopupComponent,
    InformationPopUpComponent,
    SelectionSnackbarComponent,
    SelectionCommentSnackbarComponent
  ],
  exports: [
    ConfirmationMessageComponent
  ],
  providers: [ConfirmationMessageService, SelectionSnackbarService]
})
export class SharedUiComfirmationMessageModule {}
