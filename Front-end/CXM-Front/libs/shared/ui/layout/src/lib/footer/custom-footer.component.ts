import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { CustomFooterModel } from './custom-footer.model';

@Component({
  selector: 'cxm-smartflow-footer',
  templateUrl: './custom-footer.component.html',
  styleUrls: ['./custom-footer.component.scss'],
})
export class CustomFooterComponent {
  @Input() leftFooters: CustomFooterModel[] = [];
  @Input() rightFooters: CustomFooterModel[] = [];
  @Input() position: 'Fixed' | 'Relative' | 'Abs' = 'Fixed';

  @Input() height: number | undefined;
  @Input() padRight = false;
  @Output() downloadPrivacyDoc = new EventEmitter<boolean>();

  get useHeight() {
    return this.height ? this.height + 'px' : '';
  }

  navigateTo(foot: CustomFooterModel) {
    this.downloadPrivacyDoc.next(foot.download);
    if (foot.link) {
      this.router.navigateByUrl(foot.link).then();
    }
  }

  constructor(private router: Router) {}
}
