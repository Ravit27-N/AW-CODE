import { Component, Input } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-progression-circle',
  templateUrl: './progression-circle.component.html',
  styleUrls: ['./progression-circle.component.scss']
})
export class ProgressionCircleComponent {


  @Input() progression: number;

  @Input() label: string|string[];

  @Input() color: string;

}
