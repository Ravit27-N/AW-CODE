import { Injectable } from '@angular/core';
import { Resolve, Router, RoutesRecognized } from '@angular/router';
import { Store } from '@ngrx/store';
import { filter, pairwise } from 'rxjs/operators';
import { ItemState } from '../store/feature-list-email-template';
import { getFeatureListEmailTemplate } from '../store/feature-list-email-template';

@Injectable({
  providedIn: 'root',
})
export class EmailTemplateListResolverService implements Resolve<any> {
  paginationProps: { page?: number; pageSize?: number };
  previousPageUpdate = false;
  previousPageEdit = false;
  constructor(private store: Store, private router: Router) {}

  resolve() {
    this.router.events
      .pipe(
        filter((evt: any) => evt instanceof RoutesRecognized),
        pairwise()
      )
      .subscribe((events: RoutesRecognized[]) => {
        this.previousPageEdit = events[0].urlAfterRedirects.includes(
          '/feature-email-template-composition/'
        );
        this.previousPageUpdate = events[0].urlAfterRedirects.includes(
          '/feature-update-email-template/'
        );
      });

    this.store
      .select(getFeatureListEmailTemplate)
      .subscribe((res: ItemState) => {
        if (res?.response) {
          this.paginationProps = {
            page: res?.response?.page,
            pageSize: res?.response?.pageSize,
          };
        }
      });

    if (this.previousPageEdit || this.previousPageUpdate) {
      return this.paginationProps;
    }
    return { page: 1, pageSize: 12 };
  }
}
