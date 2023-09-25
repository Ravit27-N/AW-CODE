import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { getDefinitionDirectoryDetail } from '@cxm-smartflow/definition-directory/data-access';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-feature-view-directory',
  templateUrl: './feature-view-directory.component.html',
  styleUrls: ['./feature-view-directory.component.scss']
})
export class FeatureViewDirectoryComponent implements OnInit, OnDestroy {
  #subscription: Subscription = new Subscription();

  constructor(private store: Store,
              private activateRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    const subscription = this.activateRoute.queryParams.subscribe((data) => {
      this.store.dispatch(getDefinitionDirectoryDetail({ id: data.id }));
    });
    this.#subscription.add(subscription);
  }

  ngOnDestroy() {
    this.#subscription.unsubscribe();
  }
}
