import { Component, Inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { SnackbarModel } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-icon-snackbar',
  templateUrl: './icon-snackbar.component.html',
  styleUrls: ['./icon-snackbar.component.scss']
})
export class IconSnackbarComponent {

  snackBar: SnackbarModel;
  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: any) {
    this.snackBar = data;
  }

  close(){
    this.data?.preClose();
  }
}
