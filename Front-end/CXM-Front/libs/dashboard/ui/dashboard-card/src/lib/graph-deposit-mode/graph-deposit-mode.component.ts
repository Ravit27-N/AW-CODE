import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { pickColor, numberFormatter } from '../label.utils';
import { graphActions, graphSelector } from '@cxm-smartflow/dashboard/data-access';
import { Subscription } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-graph-deposit-mode',
  templateUrl: './graph-deposit-mode.component.html',
  styleUrls: ['./graph-deposit-mode.component.scss']
})
export class GraphDepositModeComponent implements OnInit, OnDestroy {

  view: [number, number] = [200, 200];
  data: any[];
  colors: [];

  schemas: any = {
    domain: []
  };

  isError: boolean;
  fetching: boolean;
  isEmptyValue = false;
  subscription: Subscription;

  customFormatTooltip(item: any){
    return`
          <div>
            <div>${item.data.label}</div>
            <div></div>
            <div>${ numberFormatter.format(item.data.value) } %</div>
          </div>
        `;
  };

  ngOnInit(): void {
    this.subscription =
    this.store.select(graphSelector.selectGraphDeposit).subscribe(graphDeposit => {
      const { data, error, fetching, isError } = graphDeposit;
      if(data) {
        const schemas = {
          domain: pickColor(data.result.length)
        }

        Object.assign(this, {
          data: data.result,
          schemas,
          isEmptyValue: data.result.every(x => parseFloat(x.value) <= 0) });

      }
      Object.assign(this, { fetching, isError });
    });

    this.store.dispatch(graphActions.fetchGraphDepositMode());
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  constructor(private store: Store) { }
}
