import { MatDialogRef} from '@angular/material/dialog';
import { Component } from '@angular/core';

@Component({
  selector: 'app-remove-file',
  templateUrl: './remove-file.component.html',
  styleUrls: ['./remove-file.component.css']
})
export class RemoveFileComponent {

  constructor(private dailogRef: MatDialogRef<RemoveFileComponent>) {
  }

 closeDailog(): void {
   this.dailogRef.close(false);
 }
}
