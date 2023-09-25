import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { ExtAuthService, initializeIntegrationTicket } from '@cxm-smartflow/auth/data-access';
import { Store } from '@ngrx/store';

@Component({
  selector: 'cxm-smartflow-integration',
  templateUrl: './integration.component.html',
  styleUrls: ['./integration.component.scss']
})
export class IntegrationComponent implements OnInit, OnDestroy {

  subscriptions$: Subscription;

  ngOnInit(): void {
    this.subscriptions$ = this.activatedRoute.queryParams.subscribe(query => {
      const { ticket } = query;
      this.store.dispatch(initializeIntegrationTicket({ ticket: ticket }));

    });
  }

  ngOnDestroy(): void {
    this.subscriptions$?.unsubscribe();
  }

  constructor(private activatedRoute: ActivatedRoute, private router: Router, private extAuthService: ExtAuthService, private store: Store) { }

}
