import { NgxFileDragDropModule } from 'ngx-file-drag-drop';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { TextMaskModule } from 'angular2-text-mask';
import { MatBadgeModule } from '@angular/material/badge';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { MaterialFileInputModule } from 'ngx-material-file-input';
import { MatExpansionModule } from '@angular/material/expansion';
import { NgModule } from '@angular/core';
// import module material
import {MatButtonModule} from '@angular/material/button';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { LayoutModule } from '@angular/cdk/layout';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTreeModule } from '@angular/material/tree';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import {
  MAT_DATE_LOCALE,
  MatNativeDateModule,
  MatRippleModule,
} from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatGridListModule } from '@angular/material/grid-list';
import {
  NGX_MAT_DATE_FORMATS,
  NgxMatDateAdapter,
  NgxMatDatetimePickerModule,
  NgxMatNativeDateModule,
} from '@angular-material-components/datetime-picker';
import { APP_DATE_FORMATS, CustomDatetimeFormat } from './date.adapter';
import { NGX_MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular-material-components/moment-adapter';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import {
  FullscreenOverlayContainer,
  OverlayContainer,
  OverlayModule,
} from '@angular/cdk/overlay';
import { A11yModule } from '@angular/cdk/a11y';
import { ReactiveFormsModule } from '@angular/forms';

const materialComponents = [
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatIconModule,
  MatMenuModule,
  MatToolbarModule,
  LayoutModule,
  MatFormFieldModule,
  MatDatepickerModule,
  MatCheckboxModule,
  MatDialogModule,
  MatInputModule,
  MatListModule,
  MatProgressBarModule,
  MatRadioModule,
  MatSelectModule,
  MatTableModule,
  MatTreeModule,
  MatSidenavModule,
  MatPaginatorModule,
  MatSortModule,
  MatNativeDateModule,
  MatTooltipModule,
  MatChipsModule,
  MatTabsModule,
  MatRippleModule,
  MatSlideToggleModule,
  MatGridListModule,
  NgxMatNativeDateModule,
  NgxMatDatetimePickerModule,
  MatExpansionModule,
  MatSnackBarModule,
  MatProgressSpinnerModule,
  MaterialFileInputModule,
  SlickCarouselModule,
  MatBadgeModule,
  NgxMatSelectSearchModule,
  TextMaskModule,
  MatAutocompleteModule,
  NgxFileDragDropModule,
  OverlayModule,
  ReactiveFormsModule,
  A11yModule,
];

@NgModule({
    imports: [
        materialComponents,
    ],
    exports: [
        materialComponents,
    ],
    providers: [
      {
        provide: NgxMatDateAdapter,
        useClass: CustomDatetimeFormat,
        deps: [MAT_DATE_LOCALE, NGX_MAT_MOMENT_DATE_ADAPTER_OPTIONS]
      },
      { provide: NGX_MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS },
      {provide: OverlayContainer, useClass: FullscreenOverlayContainer}
    ]
})

export class MaterialModule { }
