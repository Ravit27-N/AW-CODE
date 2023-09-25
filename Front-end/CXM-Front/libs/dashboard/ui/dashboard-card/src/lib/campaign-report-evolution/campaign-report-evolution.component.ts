import { Component, Input, OnInit } from "@angular/core";
import { graphActions, graphSelector } from '@cxm-smartflow/dashboard/data-access';
import { Store } from '@ngrx/store';
import { curveCatmullRom } from 'd3-shape';
import { pickColor } from '../label.utils';

interface SingleDuration {
  name: '',
  series: [
    {
      name: "",
      value: 0
    }
  ]
}

@Component({
  selector: 'cxm-smartflow-campaign-report-evolution',
  templateUrl: './campaign-report-evolution.component.html',
  styleUrls: ['./campaign-report-evolution.component.scss']
})
export class CampaignReportEvolutionComponent implements OnInit {

  multi: any[];
  // view: any = [800, 200];
  isError = false;
  @Input()
  defaultYAxisVolume = 10;

  // options
  legend = false;
  showLabels = true;
  animations = true;
  xAxis = false;
  yAxis = true;
  showYAxisLabel = false;
  showXAxisLabel = false;
  timeline = false;

  schemas: any = {
    domain: []
  };
  curve = curveCatmullRom;
  customVolume: number;
  view: [number, number];


  ngOnInit(): void {

    this.store.select(graphSelector.selectGraphEvolution).subscribe(graphEvoltion => {
      const { data, error, fetching, isError } = JSON.parse(JSON.stringify(graphEvoltion));
      if(data) {
        const schemas = {
          domain: pickColor(data.result.length)
        }
        this.defineChartVolume(data);

        Object.assign(this, { multi: data.result, schemas });
      }
      Object.assign(this, { fetching, isError });
    });

    this.store.dispatch(graphActions.fetchGraphEvolution());

  }

  private applyDestinationDate(dateRange: any) {
    dateRange.result.forEach((record: any, index: number) => {
        dateRange.result[index].series[1] = {
          name: record.series[0].name as string,
          value: 0
        };
    });
  }

  validateSingleDateRangeSelected(durations: Array<SingleDuration>) {
    return durations.every( record => record.series.length === 1);
  }

  getMaximumVolume(durations: any) {
    let maxVolume = 0;
    durations.result.forEach((records: any) => {
      records.series.forEach((subRecord: any) => {
        if (subRecord.value > maxVolume) {
          maxVolume = subRecord.value;
        }
      })
    })
    return maxVolume;
  }

  formatYAxisToInteger(value: number) {
    return Math.floor(value);
  }

  defineChartVolume(data: any) {
    if (this.validateSingleDateRangeSelected(data.result)) {
      this.applyDestinationDate(data);
    }
    this.customVolume = this.getMaximumVolume(data) > this.defaultYAxisVolume ? this.getMaximumVolume(data) : this.defaultYAxisVolume;
  }

  onResize(event: any) {
    this.view = [event.target.innerWidth / 2, 200];
  }

  constructor(private store: Store) {
    this.view = [innerWidth / 2, 200];
  }
}
