import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { CustomMatPaginatorIntl } from './custom-mat-paginator-intl.service';
import { PaginatorComponent } from './paginator.component';

@NgModule({
  imports: [CommonModule, MaterialModule],
  declarations: [PaginatorComponent],
  exports: [PaginatorComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [
    { provide: MatPaginatorIntl, useClass: CustomMatPaginatorIntl}
  ]
})
export class SharedUiPaginatorModule {}
