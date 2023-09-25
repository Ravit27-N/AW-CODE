import {Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import * as ApexCharts from 'apexcharts';
import {ProcessedMailGraph} from "@cxm-smartflow/analytics/data-access";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'cxm-smartflow-graph-pnd-mail',
  templateUrl: './graph-pnd-mail.component.html',
  styleUrls: ['./graph-pnd-mail.component.scss']
})
export class GraphPndMailComponent implements OnChanges {

  @Input() graph: ProcessedMailGraph | null;

  @Input() spinning = true;
  @Input() title = '';
  private _chart: ApexCharts;
  @ViewChild("chart", {static: true}) chartRef: ElementRef;

  constructor(private translate: TranslateService) {}

  async ngOnChanges(changes: SimpleChanges): Promise<void> {
    if (changes.graph) {
      const nonReadonly = JSON.parse(JSON.stringify(this.graph));

      nonReadonly.yaxis = {
        labels: {
          formatter: function (value: string) {
            return value + '%';
          }
        }
      };
      nonReadonly.dataLabels= {
        enabled: true,
        style: {
          fontSize: "15px",
          fontWeight: '500',
          fontFamily: 'Rubik',
          colors: nonReadonly.colors.map(() => "#000"),

        },
        formatter: function (val:string, opt:any) {
          return opt.w.config.series[opt.seriesIndex].data[0];
        }
      };
      let title = "";
      this.translate.get('cxm_analytics.graph_volume_treaty').subscribe(value => title = value);

      nonReadonly.tooltip = {
        enabled: true,
        followCursor: false,
        theme:false,
        marker: {
          show: false,
        },
        style: {
          fontFamily: 'Rubik',
        },
        custom: function (seriesData: any) {
          const {series, seriesIndex} = seriesData;
          const total = series[0][0] + series[1][0];
          let percentage = ((series[seriesIndex][0] / total) * 100).toFixed(2);
          percentage = percentage.replace('.',",");
          return '<div class="arrow_box" >' +
            `<span> ${title} : <br> ${series[seriesIndex][0]} (${percentage}%)` +
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
