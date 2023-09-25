import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CxmDateTimePickerComponent } from './cxm-date-time-picker.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  NgxMatDatetimePickerModule,
  NgxMatNativeDateModule,
  NgxMatTimepickerModule
} from '@angular-material-components/datetime-picker';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { TranslateModule } from '@ngx-translate/core';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MAT_DATE_FORMATS } from '@angular/material/core';
  import { MomentDateAdapter } from '@angular/material-moment-adapter';

const MY_FORMATS = {
  parse: {
    dateInput: 'LL',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'YYYY',
  },
};


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatDatepickerModule,
    MatInputModule,
    NgxMatDatetimePickerModule,
    NgxMatTimepickerModule,
    NgxMatNativeDateModule,
    MatButtonModule,
    MatDividerModule,
    TranslateModule
  ],
  declarations: [
    CxmDateTimePickerComponent
  ],
  exports: [CxmDateTimePickerComponent],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},
  ]
})
export class SharedUiCxmDatetimePickerModule {
}
