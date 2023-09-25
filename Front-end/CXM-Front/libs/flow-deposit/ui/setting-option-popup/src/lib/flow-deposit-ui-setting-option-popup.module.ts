import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingOptionPopupComponent } from './setting-option-popup.component';
import { SettingOptionPopupService } from './setting-option-popup.service';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule } from '@ngx-translate/core';
import { MatDividerModule } from '@angular/material/divider';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SharedUiUploadingFragmentModule } from '@cxm-smartflow/shared/ui/uploading-fragment';
import { SettingOptionPreviewFileComponent } from './setting-option-preview-file/setting-option-preview-file.component';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import {ReactiveFormsModule} from "@angular/forms";
import {SharedDirectivesTooltipModule} from "@cxm-smartflow/shared/directives/tooltip";

@NgModule({
  imports: [
    CommonModule,
    MatDialogModule,
    MatIconModule,
    TranslateModule,
    MatDividerModule,
    SharedUiButtonModule,
    SharedUiFormInputSelectionModule,
    SharedUiUploadingFragmentModule,
    SharedPipesModule,
    ReactiveFormsModule,
    SharedDirectivesTooltipModule,
  ],
  declarations: [SettingOptionPopupComponent, SettingOptionPreviewFileComponent],
  providers: [SettingOptionPopupService],
})
export class FlowDepositUiSettingOptionPopupModule {}
