import { Component, Input, OnDestroy, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-iframely-embed',
  templateUrl: './iframely-embed.component.html',
  styleUrls: ['./iframely-embed.component.scss']
})
export class IframelyEmbedComponent implements OnDestroy{

  @Input() srcDoc = '';
  @Input() width = 467;
  @Input() height = 586;



  ngOnDestroy(): void {
    this.srcDoc = '';
  }
}



@Component({
  selector: 'cxm-smartflow-html-preview',
  template: `<section [innerHtml]="html|safeHtml"></section>`,
  encapsulation: ViewEncapsulation.ShadowDom
})
export class HtmlPrewviewComponent {

  @Input() html: string;

}
