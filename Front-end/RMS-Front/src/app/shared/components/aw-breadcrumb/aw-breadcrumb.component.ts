import { Component, Input } from '@angular/core';
import { AwBreadcrumbService } from './aw-breadcrumb.service';

@Component({
  selector: 'app-aw-breadcrumb',
  templateUrl: './aw-breadcrumb.component.html',
})
export class AwBreadcrumbComponent {
  breadcrumb$ = this.breadcrumbService.breadcrumb$;
  @Input() symbol = '>';
  @Input() customLastLabel: string = '';

  constructor(private breadcrumbService: AwBreadcrumbService) {}
}
