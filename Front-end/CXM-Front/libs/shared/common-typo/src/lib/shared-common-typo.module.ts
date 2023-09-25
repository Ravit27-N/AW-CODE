import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardHeaderTitleComponent } from './card-header-title/card-header-title.component';
import { PageHeaderComponent } from './page-header/page-header.component';
import { PageSubHeaderComponent } from './page-sub-header/page-sub-header.component';
import { HeaderButtonComponent } from './header-button/header-button.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { AnimatedConfirmationPageComponent } from './animated-confirmation-page/animated-confirmation-page.component';
import { ListPaginatorComponent } from './list-paginator/list-paginator.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { CommonListFilterComponent } from './common-list-filter/common-list-filter.component';
import { ToggleSwitchComponent } from './toggle-switch/toggle-switch.component';
import { CommonButtonComponent } from './common-button/common-button.component';
import { LangSwitcherComponent } from './lang-switcher/lang-switcher.component';
import { RouterModule } from '@angular/router';
import { CommonCheckboxComponent } from './common-checkbox/common-checkbox.component';
import { CommonSearchListComponent } from './common-search-list/common-search-list.component';
import { CommonMultipleBrowseInputComponent } from './common-multiple-browse-input/common-multiple-browse-input.component';
import { SharedDirectivesDragDropModule } from '@cxm-smartflow/shared/directives/drag-drop';
import { SharedUtilsModule } from '@cxm-smartflow/shared/utils';
import { CommonSlideToggleComponent } from './common-slide-toggle/common-slide-toggle.component';
import { CommonTimePickerComponent } from './common-time-picker/common-time-picker.component';
import { InlineInputComponent } from './InplaceInput/InplaceInput';
import {
  CommonRenderHtmlTemplateComponent,
  PreviewHtmlTemplateComponent,
} from './common-render-html-template/common-render-html-template.component';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';

const components = [
  CardHeaderTitleComponent,
  PageHeaderComponent,
  PageSubHeaderComponent,
  HeaderButtonComponent,
  AnimatedConfirmationPageComponent,
  ListPaginatorComponent,
  CommonListFilterComponent,
  ToggleSwitchComponent,
  CommonButtonComponent,
  LangSwitcherComponent,
  CommonCheckboxComponent,
  CommonSearchListComponent,
  CommonMultipleBrowseInputComponent,
  CommonSlideToggleComponent,
  CommonTimePickerComponent,
  CommonRenderHtmlTemplateComponent,
];

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    ReactiveFormsModule,
    FormsModule,
    TranslateModule,
    RouterModule,
    SharedDirectivesDragDropModule,
    SharedUtilsModule,
    SharedUiSpinnerModule,
    SharedPipesModule,
  ],
  declarations: [
    ...components,
    InlineInputComponent,
    PreviewHtmlTemplateComponent,
  ],
  exports: [...components],
})
export class SharedCommonTypoModule {}
