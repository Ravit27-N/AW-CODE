import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  constructor(private _router: Router) { }

  ngOnInit(): void {
    this._router.navigateByUrl(appRoute.cxmAnalytics.navigateToDashboard);
  }

}
