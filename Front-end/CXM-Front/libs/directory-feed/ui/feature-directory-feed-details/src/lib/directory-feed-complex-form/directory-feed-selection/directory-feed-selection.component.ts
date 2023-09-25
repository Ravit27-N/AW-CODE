import { Component, Inject, OnInit } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { DirectoryFeedSelection } from './directory-feed-selection-service.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-directory-feed-selection',
  templateUrl: './directory-feed-selection.component.html',
  styleUrls: ['./directory-feed-selection.component.scss']
})
export class DirectoryFeedSelectionComponent implements OnInit{
  selectionDelete$: Observable<number>;
  selectionModified$: Observable<number>;

  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: DirectoryFeedSelection,
              private snackBarRef: MatSnackBarRef<DirectoryFeedSelectionComponent>) {
  }

  onClose() {
    this.snackBarRef?.dismiss();
  }

  ngOnInit(): void {
    this.selectionDelete$ = this.data.selectedDelete();
    this.selectionModified$ = this.data.selectedModified();
  }
}
