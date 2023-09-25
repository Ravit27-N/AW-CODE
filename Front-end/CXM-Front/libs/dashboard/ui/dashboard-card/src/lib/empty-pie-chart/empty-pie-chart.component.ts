import { Component, Input } from "@angular/core";


@Component({
  selector: 'cxm-smartflow-empty-pie-chart',
  templateUrl: './empty-pie-chart.component.html',
  styleUrls: ['./empty-pie-chart.component.html']
})
export class EmptyPieChartComponent {

  @Input() view: any;
  @Input() doughnut = false;

  data: any[];
  schemas: any = {
    domain: ["#ececec"]
  }

  constructor() {
    Object.assign(this, { data: [{ name: 'empty', value: 1 }] });
  }

}
