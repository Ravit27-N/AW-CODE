import { Component, OnInit } from '@angular/core';
import {Observable} from "rxjs";
import {exportCsvFile, selectCanExportCsv} from "@cxm-smartflow/analytics/data-access";
import {Store} from "@ngrx/store";
import {DateRequest} from "@cxm-smartflow/analytics/util";

@Component({
  selector: 'cxm-smartflow-analytics-feature',
  templateUrl: './analytics-feature.component.html',
  styleUrls: ['./analytics-feature.component.scss'],
})
export class AnalyticsFeatureComponent implements OnInit {
  constructor(
    private _store$: Store
  ) {}

  isCanExport$ : Observable<any>;
  ngOnInit(): void {
    this.isCanExport$ = this._store$.select(selectCanExportCsv);
  }


  onExportReport() {
    const requestedAt = DateRequest.getRequestedAt();
    this._store$.dispatch(exportCsvFile({requestedAt: requestedAt}));
  }
}
