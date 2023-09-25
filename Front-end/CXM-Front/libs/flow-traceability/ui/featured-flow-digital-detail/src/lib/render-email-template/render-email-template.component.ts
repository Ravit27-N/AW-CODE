import { Component, Input, OnChanges, ViewEncapsulation } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { interval, Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-render-email-template',
  templateUrl: './render-email-template.component.html',
  styleUrls: ['./render-email-template.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class RenderEmailTemplateComponent implements OnChanges {

  @Input() htmlFile = '';
  @Input() width = '100%';
  @Input() type: 'email' | 'batch' = 'email';

  // Validation properties.
  scaleLimit = 1;
  isPresentRendering = false;

  constructor(private sanitizer: DomSanitizer) {
  }

  transformHTML(htmlFile: string) {
    // Bypass safe HTML code.
    return this.sanitizer.bypassSecurityTrustHtml(htmlFile);
  }

  scaleIframe(targetClass: string, modifyClass: string): void {
    if (this.type === 'email') {
      const destroy$ = new Subject<boolean>();
      // Scale HTML rendering container until remove horizontal scroll successfully.
      interval(0).pipe(takeUntil(destroy$)).subscribe(() => {
        const iframe: any = document.querySelector(targetClass);
        const wrapperElement = document.querySelector('.overview-popup--data-wrapper');
        const clientWidth = iframe?.clientWidth || 0;
        const scrollWidth = iframe?.scrollWidth || 0;

        this.isPresentRendering = clientWidth >= scrollWidth;
        // Check if element's client width less than element's scroll width.
        if (clientWidth < scrollWidth) {

          this.scaleLimit = this.scaleLimit - 0.01;
           document.querySelector(modifyClass)?.setAttribute('style', `transform: scale(${this.scaleLimit}); transform-origin: top left;`);

          // Terminate scaling if scale limit less than 0.1
          if (this.scaleLimit < 0.1) {
            this.isPresentRendering = true;
            destroy$.next(true);
          }
        } else {
          // Set container height and overflow properties.
          wrapperElement?.setAttribute('style',
            `overflow-y: auto; min-height: ${clientWidth}px; padding-bottom: 5px;`);
          // Terminate scaling if element's client width more than or equal to scroll width
          destroy$.next(true);
        }
      });
    }

    if (this.type === 'batch') {
      const wrapperElement = document.querySelector('.overview-popup--data-wrapper');
      wrapperElement?.setAttribute(
        'style',
        `overflow-y: auto; padding-bottom: 5px;`);
      this.isPresentRendering = true;
    }
  }

  ngOnChanges(): void {

    // Scale rendering HTML container when data change.
    interval(1000).pipe(take(1)).subscribe(() => {
      this.scaleIframe('.iframe-render-html-container', '.iframe-render__wrapper');
    });
  }

}
