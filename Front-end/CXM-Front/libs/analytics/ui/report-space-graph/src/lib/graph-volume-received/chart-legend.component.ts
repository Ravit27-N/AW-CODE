import { Component, Input } from "@angular/core";

@Component({
  selector: 'cxm-smartflow-chart-legends',
  styles: [
    `span.bloc { display: inline-block; font-weight: 600; color: rgba(0, 0, 0, 0.6); }`
  ],
  template: `
    <span class="align-middle flex flex-row items-center">
      <span [style.background]="color"class="inline-block rounded-full p-2 mr-2"></span>
      <span class="bloc"><ng-content></ng-content></span>
    </span>

  `
})
export class ChartLegendsComponent {

  @Input() name: string;
  @Input() color: string;

}
