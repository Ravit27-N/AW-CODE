import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';

export interface DeleteDialogData {
  id: number;
  fullName: string;
}

@Component({
  selector: 'app-delete-candidate',
  templateUrl: './delete-candidate.component.html',
  styleUrls: ['./delete-candidate.component.css']
})
export class DeleteCandidateComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public candidate: DeleteDialogData
  ) { }
}
