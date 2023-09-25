import { Component, OnInit } from '@angular/core';
import { DashboardService, IInterviewGraphModel } from '../../core';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Label } from 'ng2-charts';
import { IsLoadingService } from '@service-work/is-loading';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard-interview-graph',
  templateUrl: './dashboard-interview-graph.component.html',
  styleUrls: ['./dashboard-interview-graph.component.scss'],
})
export class DashboardInterviewGraphComponent implements OnInit {
  month: string;
  chartData: IInterviewGraphModel[] = [];
  total: IInterviewGraphModel;
  public barChartData: ChartDataSets[] = [];
  public barChartLabels: Label[] = [
    'Jan',
    'Feb',
    'Mar',
    'Apr',
    'May',
    'Jun',
    'Jul',
    'Aug',
    'Sep',
    'Oct',
    'Nov',
    'Dec',
  ];
  public barChartType: ChartType = 'bar';
  public barChartOptions: ChartOptions;

  constructor(
    private service: DashboardService,
    private isLoadingService: IsLoadingService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const year = new Date().getFullYear();
    const subscription = this.service
      .getInterviewGraph(year)
      .subscribe((data) => {
        this.total = data.reportInterviews.total;
        this.total.fill = false;
        this.total.type = 'line';
        this.chartData.push(this.total);
        data.reportInterviews.graph.forEach((graphData) => {
          this.chartData.push(graphData);
        });
        this.barChartData = this.chartData;
      });
    this.isLoadingService.add(subscription, {
      key: 'InterviewsGraphsComponent',
      unique: 'InterviewsGraphsComponent',
    });
    const graphClickEvent = (event: any, array) => {
      if (array.length > 0) {
        const datasetIndex = array[0]._datasetIndex;
        const dataObject = this.barChartData[datasetIndex].label;
        this.router.navigate([
          `/admin/interview/list/${array[0]._index}/${dataObject}`,
        ]);
      }
    };
    const mouseHover = (event, chartElement) => {
      event.target.style.cursor = chartElement[0] ? 'pointer' : 'default';
    };
    this.barChartOptions = {
      responsive: true,
      plugins: {
        datalabels: {
          anchor: 'end',
          align: 'end',
        },
      },
      hover: {
        mode: 'nearest',
        intersect: true,
      },
      scales: {
        xAxes: [
          {
            ticks: {
              beginAtZero: true,
            },
          },
        ],
        yAxes: [
          {
            ticks: {
              beginAtZero: true,
            },
          },
        ],
      },
      onClick: graphClickEvent,
      onHover: mouseHover,
    };
  }
}
