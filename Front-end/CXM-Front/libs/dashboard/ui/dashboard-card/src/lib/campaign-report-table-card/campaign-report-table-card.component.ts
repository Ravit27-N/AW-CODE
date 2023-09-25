import { Component, OnDestroy, OnInit } from '@angular/core';
import { graphActions, graphSelector } from '@cxm-smartflow/dashboard/data-access';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { FlowtrackingDatasource } from './flowtracking-report-datasource';


@Component({
  selector: 'cxm-smartflow-campaign-report-table-card',
  templateUrl: './campaign-report-table-card.component.html',
  styleUrls: ['./campaign-report-table-card.component.scss']
})
export class CampaignReportTableCardComponent implements OnInit, OnDestroy {

  data: [] = [];
  metadataTable: any[] = [];
  displayedColumns: [] = [];

  fetching: boolean;
  isError: boolean;
  subscription: Subscription;

  datasource = new FlowtrackingDatasource<any>(this.data);

  updateTable() {
    this.datasource.setData(this.data);
  }

  ngOnInit(): void {

    this.subscription = this.store.select(graphSelector.selectGraphFlowtracking).subscribe(graphFlowtracking => {
      const { fetching, isError, data  } = graphFlowtracking;

      if(data) {
        const { result, metaData } = data;
        const displayedColumns = metaData.map(m => m.col);
        Object.assign(this, { metadataTable: metaData, data: result , displayedColumns});

        this.updateTable();
      }
      Object.assign(this, { fetching, isError });
    });

    this.store.dispatch(graphActions.fetchGraphFlowTracking());
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  constructor(private store: Store) { }
}
