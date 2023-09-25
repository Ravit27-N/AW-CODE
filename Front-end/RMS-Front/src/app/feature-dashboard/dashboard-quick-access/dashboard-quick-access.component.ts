import { Component, Input } from '@angular/core';
import { DashboardQuickAccessModel } from '../../core';

@Component({
  selector: 'app-dashboard-quick-access',
  templateUrl: './dashboard-quick-access.component.html',
  styleUrls: ['./dashboard-quick-access.component.scss'],
})
export class DashboardQuickAccessComponent {
  @Input() dashboardQuickAccess: DashboardQuickAccessModel;
}
