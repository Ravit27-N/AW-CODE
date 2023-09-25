import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FlowDepositService } from '@cxm-smartflow/flow-deposit/data-access';
import { appRoute } from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';



@Component({
  selector: 'cxm-smartflow-integration',
  templateUrl: './integration.component.html',
  styleUrls: ['./integration.component.scss']
})
export class IntegrationFlowComponent implements OnInit, OnDestroy {
  subscriptions$: Subscription;

  ngOnInit(): void {
    const { flowid } = this.activatedRoute.snapshot.params;
    this.subscriptions$ = this.depositService.getDepositList(1, 1, {depositModes: ['IV'], fileId: flowid }).subscribe(response => {
      const { contents } = response;
      if(contents && contents.length > 0) {
        const first = contents[0];
        const { step, validated, composedFileId } = first;
        let returnTo = '';
        if(step == 2) {
          returnTo = appRoute.cxmDeposit.navigateToPreAnalysis + `?fileId=${flowid}&step=${step}`;
        } else if(step == 3) {
          returnTo = appRoute.cxmDeposit.navigateToAnalysisResult + `?step=${step}&composedFileId=${composedFileId}&fileId=${flowid}&validation=${validated}`
        } else if(step == 4) {
          returnTo = appRoute.cxmDeposit.navigateToProductionCriteria + `?step=${step}&composedFileId=${composedFileId}&fileId=${flowid}&validation=${validated}`
        } else if(step == 5) {
          returnTo = appRoute.cxmDeposit.navigateToFinished + `?step=${step}&composedFileId=${composedFileId}&fileId=${flowid}&validation=${validated}`
        }
        else {
          returnTo = appRoute.dashboard.baseRoot;
        }

        this.router.navigateByUrl(returnTo);

      } else {
        this.router.navigateByUrl(appRoute.dashboard.baseRoot);
      }

    });
  }

  ngOnDestroy(): void {
    this.subscriptions$.unsubscribe();
  }

  constructor(private activatedRoute: ActivatedRoute, private store: Store, private depositService: FlowDepositService, private router: Router) { }

}
