import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { dequeTab, getMenuTab } from '@cxm-smartflow/flow-deposit/data-access';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';


export interface ITabData { name: string; id: string; active: boolean, link: string, parent?: ITabData };

@Component({
  selector: 'cxm-smartflow-deposit-tab-nav',
  templateUrl: './deposit-tab-nav.component.html',
  styleUrls: ['./deposit-tab-nav.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DepositTabNavComponent implements OnInit {

  activatedTabs$: Observable<ITabData[]>;

  ngOnInit(): void {
    this.activatedTabs$ = this.store.select(getMenuTab);
  }

  navigateTo(item: any) {
    if (item.link && !item.active) {
      this.store.dispatch(dequeTab(item));
      this.router.navigateByUrl(item.link);
    }
  }

  constructor(private store: Store, private router: Router) { }
}
