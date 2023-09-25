import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-preview-dom',
  templateUrl: './preview-dom.component.html',
  styleUrls: ['./preview-dom.component.scss'],
  encapsulation: ViewEncapsulation.ShadowDom
})
export class PreviewDomComponent implements OnInit {
  @Input() m: any;
  constructor() { }

  ngOnInit(): void {
  }

}
