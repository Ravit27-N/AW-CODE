import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ProductionDetailsMetadataModel, GlobalProductionDetailsModel } from '@cxm-smartflow/analytics/data-access';
import { TableDatasource } from '@cxm-smartflow/analytics/util';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-global-production-details-table',
  templateUrl: './global-production-details-table.component.html',
  styleUrls: ['./global-production-details-table.component.scss']
})
export class GlobalProductionDetailsTableComponent implements OnChanges {

  @Input() data: GlobalProductionDetailsModel = { metaData: [], result: [], loading: true };


  // Table metadata.
  metadata: ProductionDetailsMetadataModel [] = [];
  displayColumns: any [] = [];

  // Datasource table.
  datasource = new TableDatasource<any>([]);
  loading$ = new BehaviorSubject(true);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.data) {

      if(this.data?.loading){
        this.loading$.next(true);
        setTimeout(() => {
          this.loading$.next(false);
        }, 500);
      }

      const { metaData, result } = this.data;
      if (metaData && result) {
        const columnsMap = metaData?.map((m: any) => m?.col);

        Object.assign(this, { metadata: metaData, displayColumns: columnsMap });
        this.datasource.setData(result);
      }
    }
  }
}
