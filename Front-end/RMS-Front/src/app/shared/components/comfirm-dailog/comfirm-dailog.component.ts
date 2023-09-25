import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';

@Component({
  selector: 'app-comfirm-dailog',
  templateUrl: './comfirm-dailog.component.html',
  styleUrls: ['./comfirm-dailog.component.css']
})
export class ComfirmDailogComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public data, private dailogRef: MatDialogRef<ComfirmDailogComponent>) {
   }

  closeDailog(): void {
    this.dailogRef.close(false);
  }

}
