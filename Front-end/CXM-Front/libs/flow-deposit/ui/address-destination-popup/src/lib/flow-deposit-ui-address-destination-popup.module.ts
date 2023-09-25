import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AddressDestinationPopupComponent} from './address-destination-popup.component';
import {AddressDestinationPopupService} from "./address-destination-popup.service";
import {MatDialogModule} from "@angular/material/dialog";
import {MatIconModule} from "@angular/material/icon";
import {TranslateModule} from "@ngx-translate/core";
import {SharedUiButtonModule} from "@cxm-smartflow/shared/ui/button";
import {SharedDirectivesTooltipModule} from "@cxm-smartflow/shared/directives/tooltip";
import {ReactiveFormsModule} from "@angular/forms";

@NgModule({
  imports: [CommonModule,
    MatDialogModule, MatIconModule, TranslateModule, SharedUiButtonModule, SharedDirectivesTooltipModule, ReactiveFormsModule],
  declarations: [
    AddressDestinationPopupComponent
  ],
  providers: [AddressDestinationPopupService],
})
export class FlowDepositUiAddressDestinationPopupModule {
}
