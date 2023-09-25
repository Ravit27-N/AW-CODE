import { Component } from '@angular/core';
import { LegendPosition } from '@swimlane/ngx-charts';

@Component({
  selector: 'cxm-smartflow-campaign-report-processing-card',
  templateUrl: './campaign-report-processing-card.component.html',
  styleUrls: ['./campaign-report-processing-card.component.scss']
})
export class CampaignReportProcessingCardComponent {

  view: any = [210, 210];
  data: any[];

  // options
  gradient = false;
  showLegend = false;
  showLabels = false;
  legendPosition = LegendPosition.Right;

  colorScheme = [
    { name: 'BATCH', value: '#984FA3' },
    { name: 'ON-DEMAND', value: '#FF4E83' },
    { name: 'INTERACTIF', value: '#FF7F8D' },
    { name: 'EGRENE', value: '#0060AA' },
  ];

  constructor() {
    // No content
    const data = [
      { name: 'BATCH', value: 2 },
      { name: 'ON-DEMAND', value: 40 },
      { name: 'INTERACTIF', value: 25 },
      { name: 'EGRENE', value: 12 },
    ]
    Object.assign(this, { data } )
   }

}
