import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {selectFlowWatermark, selectTreatmentResponse, selectWatermark} from "@cxm-smartflow/flow-deposit/data-access";
import {Store} from "@ngrx/store";

@Component({
  selector: 'cxm-smartflow-watermark-list',
  templateUrl: './watermark-list.component.html',
  styleUrls: ['./watermark-list.component.scss']
})
export class WatermarkListComponent implements OnInit {
  watermark$: Observable<any>;

  ngOnInit(): void {
    this.watermark$ = this.store.select(selectFlowWatermark);
  }
  constructor(private store: Store) {
  }

}
