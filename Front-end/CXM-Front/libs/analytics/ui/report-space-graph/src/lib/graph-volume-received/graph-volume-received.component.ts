import { Component, Input, OnChanges } from "@angular/core";
import { VolumeReceiveGraph } from '@cxm-smartflow/analytics/data-access';
import { ScaleType } from '@swimlane/ngx-charts';
import { TranslateService } from "@ngx-translate/core";

@Component({
  selector: 'cxm-smartflow-graph-volume-received',
  templateUrl: './graph-volume-received.component.html',
  styleUrls: ['./graph-volume-received.component.scss'],
})
export class GraphVolumeReceivedComponent implements OnChanges {
  // Graph.
  @Input() graph: VolumeReceiveGraph | null;
  @Input() title:string = '';
  displayGraph: VolumeReceiveGraph;
  view: [number, number] = [0, 0];
  @Input() disabledTooltip: boolean = false;

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  constructor(private translate: TranslateService) {}
  ngOnChanges(): void {
    if (this.graph) {
      this.displayGraph = this.graph;
      this.view = [240, 240];
    } else {
      this.graph = {
        results: [],
        scheme: {
          domain: [],
          name: '',
          group: ScaleType.Time,
          selectable: true,
        },
        graphLabels: [],
        color: '',
        doughnut: false,
        name: '',
        labels: true,
        legend: false,
        view: [200, 200],
        tooltipText: ``,
        fetching: false,
        empty: false,
      };
    }
    if (this.graph && this.graph.results.length) {
      localStorage.setItem('graph-result', JSON.stringify(this.graph.results))
    }
    this.translate.get('cxm_analytics.graph_volume_received').toPromise().then(response => {
      localStorage.setItem('cxm_analytics.graph_volume_received', response)
    });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------


  customFormatTooltip(item: any): string {
    const numberFormatter = new Intl.NumberFormat(
      localStorage.getItem('locale') || 'fr',
      {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }
    );
    const graphVolumes = JSON.parse(localStorage.getItem('graph-result') || '');
    const singleVolume = graphVolumes.filter((result: any) => item.data.label === result.name)
      .map((volume: any) => volume);
    const title = (localStorage.getItem('cxm_analytics.graph_volume_received') || "");
    const tooltip = (singleVolume[0].volume || '').toString().concat(" (")
      .concat(numberFormatter.format(item.data.value) || "").concat("%)");

    return `
          <div>
            <div>${title} :</div>
            <div></div>
            <div>${tooltip}</div>
          </div>
        `;
  }

  labelFormatting(name: string): string {
    const numberFormatter = new Intl.NumberFormat(
      localStorage.getItem('locale') || 'fr',
      {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }
    );

    const self = this as any;
    const data = self.series.filter((x: any) => x.name == name);

    if(data.length > 0) {
      return `${numberFormatter.format(data[0].value)} %`;
    } else {
      return name;
    }
  }
}
