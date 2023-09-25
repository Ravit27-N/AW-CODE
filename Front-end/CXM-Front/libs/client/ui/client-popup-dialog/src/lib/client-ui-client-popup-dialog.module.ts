import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MetaDataPopupComponent } from './meta-data-popup/meta-data-popup.component';
import { MetaDataDraggableComponent } from './meta-data-draggable/meta-data-draggable.component';
import { MetaDataPopupService } from './meta-data-popup/meta-data-popup.service';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedDirectivesDragDropModule } from '@cxm-smartflow/shared/directives/drag-drop';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { ServiceProviderPopupService } from './service-provider-popup/service-provider-popup.service';
import { ServiceProviderPopupComponent } from './service-provider-popup/service-provider-popup.component';
import { HubDistributePopupService } from './hub-distribute-popup/hub-distribute-popup.service';
import { HubDistributePopupComponent } from './hub-distribute-popup/hub-distribute-popup.component';
import { PopupReturnAddressComponent } from './popup-return-address/popup-return-address.component';
import { SharedFragmentsReturnAddressModule } from "@cxm-smartflow/shared/fragments/return-address";
import {PopupReturnAddressService} from "./popup-return-address/popup-return-address.service";

@NgModule({
  imports: [
    CommonModule,
    SharedCommonTypoModule,
    SharedPipesModule,
    SharedTranslateModule.forRoot(),
    SharedUiButtonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    SharedUiButtonModule,
    SharedDirectivesTooltipModule,
    SharedDirectivesDragDropModule,
    SharedUiFormInputSelectionModule,
    SharedFragmentsReturnAddressModule,
  ],
  declarations: [
    MetaDataPopupComponent,
    MetaDataDraggableComponent,
    ServiceProviderPopupComponent,
    HubDistributePopupComponent,
    PopupReturnAddressComponent,
  ],
  providers: [
    MetaDataPopupService,
    ServiceProviderPopupService,
    HubDistributePopupService,
    PopupReturnAddressService,
  ],
})
export class ClientUiClientPopupDialogModule {}
