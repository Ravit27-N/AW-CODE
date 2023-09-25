import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { pickColor, numberFormatter } from '../label.utils';
import { graphActions, graphSelector } from '@cxm-smartflow/dashboard/data-access';
import { Subscription } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-graph-channel-envoy',
  templateUrl: './graph-channel-envoy.component.html',
  styleUrls: ['./graph-channel-envoy.component.scss']
})
export class GraphChannelEnvoyComponent implements OnInit, OnDestroy {

  view: [number, number] = [200, 200];
  data: any[];

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
            <div>${ numberFormatter.format(item.data.value)} %</div>
          </div>
        `;
  };

  ngOnInit(): void {
    this.subscription =
    this.store.select(graphSelector.selectGraphChannel).subscribe(graphChannel => {
      const { data, error, fetching, isError } = graphChannel;
      if(data) {
        const schemas = {
          domain: pickColor(data.result.length)
        }
        Object.assign(this, {
          data: data.result,
          schemas,
          isEmptyValue: data.result.every(x => parseFloat(x.value) <= 0)
        });
      }
      Object.assign(this, { fetching, isError });
    });

    this.store.dispatch(graphActions.fetchGraphChannel())
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  constructor(private store: Store) { }

}
