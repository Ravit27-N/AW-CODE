import { Component, Input, OnChanges, OnDestroy, SimpleChanges } from '@angular/core';
import { ProductionDetails, ProductionDetailsMetadataModel } from '@cxm-smartflow/analytics/data-access';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { BehaviorSubject } from 'rxjs';

/**
 * Production details data with nested structure.
 * Each node has properties and list of children.
 */
interface ProductionDetailNode {
  filler: string;
  volumeReceived: number;
  processed: number;
  inProgress: number;
  pndMnd: number;
  processedPercentage: number;
  pndMndPercentage: number;
  data: ProductionDetailNode[] | null;
}

/** Flat node with expandable and level information */
interface FlatNodeInfo {
  expandable: boolean;
  level: number;
  filler: string;
  volumeReceived: number;
  processed: number;
  inProgress: number;
  pndMnd: number;
  processedPercentage: number;
  pndMndPercentage: number;
}

@Component({
  selector: 'cxm-smartflow-table-production-detail',
  templateUrl: './table-production-detail.component.html',
  styleUrls: ['./table-production-detail.component.scss']
})
export class TableProductionDetailComponent implements OnChanges, OnDestroy{

  @Input() productionDetails: ProductionDetails = { metaData: [], data: [], total: {}, loading: true };
  @Input() HeaderTitle: string = 'cxm_analytics.detail_of_production_by_filler_grouping.title';
  @Input() customClass: string  = '';

  // Properties of table.
  loading$ = new BehaviorSubject(true);
  displayedColumns: string[] = [];
  displayRows: string [] = ['total', 'receive'];
  metadata: ProductionDetailsMetadataModel [] = [];
  showTotal$ = new BehaviorSubject(false);

  constructor() {
    this.dataSource.data = this.productionDetails.data;
  }

  total(fieldName: string): any {
    return this.productionDetails?.total[fieldName];
  }

  private _transformer = (node: ProductionDetailNode, level: number): FlatNodeInfo => {
    return {
      expandable: !!node.data && node.data.length > 0,
      filler: node.filler,
      volumeReceived: node.volumeReceived,
      processed: node.processed,
      inProgress: node.inProgress,
      pndMnd: node.pndMnd,
      processedPercentage: node.processedPercentage,
      pndMndPercentage: node.pndMndPercentage,
      level: level
    };
  };

  // eslint-disable-next-line @typescript-eslint/member-ordering
  treeControl = new FlatTreeControl<FlatNodeInfo>(
    node => node.level,
    node => node.expandable
  );

  // eslint-disable-next-line @typescript-eslint/member-ordering
  treeFlattener = new MatTreeFlattener(
    this._transformer,
    node => node.level,
    node => node.expandable,
    node => node.data
  );

  // eslint-disable-next-line @typescript-eslint/member-ordering
  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  hasChild = (_: number, node: FlatNodeInfo) => node.expandable;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.productionDetails) {
      const { metaData, data, loading } = this.productionDetails;
      this.loading$.next(Boolean(loading));

      if (metaData && data) {
        const columnsMap = metaData?.map((m: any) => m?.col);
        Object.assign(this, { metadata: metaData, displayedColumns: columnsMap });

        if (this.metadata && this.displayedColumns) {
          this.dataSource.data = data;
          this.treeControl.expandAll();
          this.showTotal$.next(this.metadata.some(value => value.col === 'filler'));
        }
      }
    }
  }

  ngOnDestroy(): void {
    this.loading$.unsubscribe();
    this.showTotal$.unsubscribe();
  }
}
