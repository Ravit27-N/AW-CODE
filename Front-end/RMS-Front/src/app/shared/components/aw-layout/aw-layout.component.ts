import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-aw-layout',
  templateUrl: './aw-layout.component.html',
  styleUrls: ['./aw-layout.component.scss'],
})
export class AwLayoutComponent {
  @Input() pageTitle = '';
  @Input() pageSubtitle = '';
  @Input() enabledBreadcrumb = true;
}
