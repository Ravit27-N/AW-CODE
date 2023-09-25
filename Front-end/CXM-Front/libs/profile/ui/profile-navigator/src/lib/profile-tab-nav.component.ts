import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { dequeTab, getMenuTab, ITabData  } from '@cxm-smartflow/profile/data-access';

@Component({
  selector: 'cxm-smartflow-profile-tab-nav',
  templateUrl: './profile-tab-nav.component.html',
  styleUrls: ['./profile-tab-nav.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProfileTabNavComponent implements OnInit {

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
