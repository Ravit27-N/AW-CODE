import {
  Component,
  ElementRef,
  Input,
  OnChanges,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import * as ApexCharts from 'apexcharts';
import { ChartOptions } from '@cxm-smartflow/analytics/data-access';
import {TranslateService} from "@ngx-translate/core";
import {dark} from "@material-ui/core/styles/createPalette";

@Component({
  selector: 'cxm-smartflow-graph-donut',
  templateUrl: './graph-donut.component.html',
  styleUrls: ['./graph-donut.component.scss'],
})
export class GraphDonutComponent implements OnChanges {
  @Input() title = '';
  @Input() spinner = false;
  @Input() graph: ChartOptions;
  @Input() chartType: "SMS" | "EMAIL" | string;
  _chart: ApexCharts;

  hiddenAll = false;
  constructor(private translate: TranslateService) {}
  @ViewChild('chart', { static: true }) chartRef: ElementRef;

  async ngOnChanges(changes: SimpleChanges): Promise<void> {
    if(changes?.spinner || changes.graph){
      if(this.graph.isHidden){
        this.hiddenAll = true;
      }else{
        this.hiddenAll = this.spinner;
      }
    }
    if (changes.graph) {

      const nonReadonly = JSON.parse(JSON.stringify(this.graph));

      let title = "";
      if(this.chartType==="SMS"){
        this.translate.get('cxm_analytics.graph_status_sms').subscribe(value => title = value);
      }else if(this.chartType==="EMAIL"){
        this.translate.get('cxm_analytics.graph_status_mail').subscribe(value => title = value);
      }else{
        this.translate.get('cxm_analytics.graph_motif_pnd').subscribe(value => title = value);
      }

      nonReadonly.tooltip = {
        enabled: true,
        marker: {
          show: false,
        },
        style: {
          fontSize: '12px',
          fontFamily: 'Rubik',
        },
        custom: function (seriesData: any) {
          const {series, seriesIndex} = seriesData;
          let total = 0;
          for (const sery of series) {
            total += sery;
          }
          let percentage = ((series[seriesIndex] / total) * 100).toFixed(2);
          percentage = percentage.replace('.',",");
          return '<div class="arrow_box">' +
            `<span> ${title} : <br> ${series[seriesIndex]} (${percentage}%)` +
            '</div>';
        }
      }

      if (!changes.graph.firstChange) {
        if (this._chart) {
          await this._chart.updateOptions(nonReadonly);
        } else {
          this._chart = new ApexCharts(this.chartRef.nativeElement, nonReadonly);
          await this._chart.render();
        }
      } else {
        this._chart = new ApexCharts(this.chartRef.nativeElement, nonReadonly);
        await this._chart.render();
      }

    }
  }
}
