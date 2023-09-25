import {Component, OnInit} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType} from 'chart.js';
import {MultiDataSet} from 'ng2-charts';
import {DashboardService} from '../../core';
import {IsLoadingService} from '@service-work/is-loading';
import {Router} from '@angular/router';

@Component({
  selector: 'app-dashboard-candidate-graph',
  templateUrl: './dashboard-candidate-graph.component.html',
  styleUrls: ['./dashboard-candidate-graph.component.scss'],
})
export class DashboardCandidateGraphComponent implements OnInit {
  gender = ' ';
  status = 'all';
  genderName: string[] = [];
  genderCount: number[] = [];
  statusName: string[] = [];
  statusCount: number[] = [];
  candidateStatusName: string[] = [];
  public doughnutChartOptions: ChartOptions;
  public doughnutChartData: MultiDataSet = [this.genderCount];
  public doughnutChartType: ChartType = 'doughnut';
  public doughnutChartColors: Array<any> = [
    {
      backgroundColor: ['#ff4e83', '#0253ad'],
      borderColor: ['#fff', '#fff'],
    },
  ];

  public barChartOptions: ChartOptions;
  public barChartType: ChartType = 'bar';
  public barChartData: ChartDataSets[] = [{data: this.statusCount}];

  constructor(
    private service: DashboardService,
    private isLoadingService: IsLoadingService,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    const subscription = this.service.getCandidateCount().subscribe((data) => {
      const genders = data.gender.json_agg;
      if (genders) {
        genders.forEach((jsonAgg, i) => {
          this.genderName.push(genders[i].name);
          this.genderCount.push(genders[i].number);
        });
        const statuses = data.status.json_agg;
        statuses.forEach((_jsonAgg, i) => {
          this.statusName.push(statuses[i].label);
          this.statusCount.push(statuses[i].number);
        });
        this.candidateStatusName = this.statusName;
      }
    });
    this.isLoadingService.add(subscription, {
      key: 'DashboardCandidateGraphComponent',
      unique: 'DashboardCandidateGraphComponent',
    });

    const graphClickEvent = (event: any, array: any[]) => {
      if (array[0] !== undefined) {
        this.status = this.statusName[array[0]._index];
        this.goToCandidateList();
      }
    };

    const doughnutClickEvent = (event: any, array: any[]) => {
      if (array[0] !== undefined) {
        this.gender = this.genderName[array[0]._index];
        this.goToCandidateList();
      }
    };

    const mouseHover = (event, chartElement) => {
      event.target.style.cursor = chartElement[0] ? 'pointer' : 'default';
    };

    this.barChartOptions = {
      responsive: true,
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
    this.doughnutChartOptions = {
      responsive: true,
      scales: {},
      maintainAspectRatio: false,
      onClick: doughnutClickEvent,
      onHover: mouseHover,
    };
  }

  async goToCandidateList(): Promise<void> {
    await this.router.navigate([
      `/admin/candidate`,
    ], {
      queryParams: {
        gender: this.gender,
        status: this.status
      }
    });
  }
}
