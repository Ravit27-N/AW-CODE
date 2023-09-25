import { Component, Input } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-progression-line',
  templateUrl: './progression-line.component.html',
  styleUrls: ['./progression-line.component.scss']
})
export class ProgressionLineComponent  {

  @Input() size = "10px";
  @Input() width = "50%";
  @Input() outsideBackground = "var(--unnamed-color-ffffff) 0% 0% no-repeat padding-box";
  @Input() radius = "45px";
  @Input() outsideBorder = "1px solid #4475C7";
  @Input() innerBackground = "var(--unnamed-color-3b82f6) 0% 0% no-repeat padding-box";
  @Input() transition = "all 0.5s";

}
