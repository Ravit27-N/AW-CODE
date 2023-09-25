import { Component, Inject, OnInit } from '@angular/core';
import { MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { AwSnackbarModel } from './aw-snackbar.service';

@Component({
  selector: 'app-aw-snackbar',
  templateUrl: './aw-snackbar.component.html',
  styleUrls: ['./aw-snackbar.component.scss'],
})
export class AwSnackbarComponent implements OnInit {
  snackBar: AwSnackbarModel;

  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: any) {
    this.snackBar = data;
  }

  ngOnInit() {}

  close() {
    this.data?.preClose();
  }
}
