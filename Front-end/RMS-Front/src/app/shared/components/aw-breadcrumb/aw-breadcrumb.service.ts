import { Injectable } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

interface AwBreadcrumb {
  label: string;
  link: string;
}

@Injectable({
  providedIn: 'root',
})
export class AwBreadcrumbService {
  private breadcrumbSubject: BehaviorSubject<AwBreadcrumb[]> = new BehaviorSubject<AwBreadcrumb[]>([]);
  breadcrumb$: Observable<AwBreadcrumb[]> = this.breadcrumbSubject.asObservable();

  constructor(private router: Router) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        const breadcrumb = this.createBreadcrumb(this.router.routerState.root);
        this.breadcrumbSubject.next(breadcrumb);
      });
  }

  private createBreadcrumb(route: ActivatedRoute, url: string = '', breadcrumb: AwBreadcrumb[] = []): AwBreadcrumb[] {
    const children: ActivatedRoute[] = route.children;
    if (children.length === 0) {
      return breadcrumb;
    }

    children.forEach(child => {
      const routeURL: string = child.snapshot.url.map(segment => segment.path).join('/');
      const label = child.snapshot.data.breadcrumb;
      url = routeURL ? `${url}/${routeURL}` : url;
      breadcrumb.push({
        label,
        link: url,
      });
      this.createBreadcrumb(child, url, breadcrumb);
    });

    return breadcrumb;
  }
}
