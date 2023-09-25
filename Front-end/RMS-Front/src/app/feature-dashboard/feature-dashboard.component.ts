import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../core/service/dashboard.service';
import { DashboardQuickAccessModel } from '../core';
import { DemandService } from '../core/service/demand.service';

@Component({
  selector: 'app-feature-dashboard',
  templateUrl: './feature-dashboard.component.html',
  styleUrls: ['./feature-dashboard.component.scss'],
})
export class FeatureDashboardComponent implements OnInit {
  dashboardQuickAccess: DashboardQuickAccessModel;

  constructor(
    private dashboardService: DashboardService,
    private demandService: DemandService,
  ) {}

  async ngOnInit(): Promise<void> {
    await this.fetchDashboardQuickAccess();
  }

  async fetchDashboardQuickAccess(): Promise<void> {
    this.dashboardQuickAccess = await this.dashboardService.getDashboardCounts().toPromise();
  }
}
