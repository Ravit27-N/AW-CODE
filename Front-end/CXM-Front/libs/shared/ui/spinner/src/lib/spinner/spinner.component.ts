import { Component, Input } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss'],
})
export class SpinnerComponent {

  @Input() inline = false;

}
