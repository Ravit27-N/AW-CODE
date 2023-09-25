import { Component, Input, OnChanges, SimpleChanges } from "@angular/core";
import { ProductionProgressModel } from '@cxm-smartflow/analytics/data-access';
import { TranslateService } from "@ngx-translate/core";

@Component({
  selector: 'cxm-smartflow-graph-production-progress',
  templateUrl: './graph-production-progress.component.html',
  styleUrls: ['./graph-production-progress.component.scss'],
})
export class GraphProductionProgressComponent implements OnChanges {

  @Input() productionProgress: {
    content: ProductionProgressModel[];
    isFetching: boolean;
  } = {
    content: [],
    isFetching: false,
  };
  currentElement: string;
  constructor(private translate: TranslateService) {}

  ngOnChanges(simpleChanges: SimpleChanges): void {
    if (simpleChanges.productionProgress) {
      localStorage.setItem('cxm_analytics.graph_volume', JSON.stringify(this.productionProgress.content));
    }
    this.translate.get('cxm_analytics.graph_volume_treaty').toPromise().then(response => {
      localStorage.setItem('cxm_analytics.graph_volume_treaty', response);
    });
  }

  getFormattedLabel(value: number): string {

    const numberFormatter = new Intl.NumberFormat(
      localStorage.getItem('locale') || 'fr',
      {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }
    );

    return `${numberFormatter.format(value)} %`;
  }

  get size(): number {

    if (window.outerWidth < 1781) {
      return 200;
    }

    return 260;
  }

  customTooltip(item: ProductionProgressModel): string {
    const numberFormatter = new Intl.NumberFormat(
      localStorage.getItem('locale') || 'fr',
      {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }
    );
    const graphVolumes = JSON.parse(localStorage.getItem('cxm_analytics.graph_volume') || '');
    const singleVolume = graphVolumes.filter((volume: any) => volume.label === item.label).map((volume: any) => volume);
    const title = (localStorage.getItem('cxm_analytics.graph_volume_treaty') || "").concat(" :");
    const tooltip = (singleVolume[0].volume || '').toString().concat(" (".concat(numberFormatter.format(item.value)
      .concat("%")).concat(")"));

    const tooltips = [title, tooltip].join('\n')
    return tooltips;
  }

  addClass(label: string): void {
    this.currentElement = label;
  }
  removeClass(): void {
    this.currentElement = '';
  }
}
