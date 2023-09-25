import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-email-preview',
  templateUrl: './email-preview.component.html',
  styleUrls: ['./email-preview.component.scss'],
  encapsulation: ViewEncapsulation.ShadowDom

})
export class EmailPreviewComponent implements OnInit {
  @Input() m: any;
  constructor() { }

  ngOnInit(): void {
  }

}
