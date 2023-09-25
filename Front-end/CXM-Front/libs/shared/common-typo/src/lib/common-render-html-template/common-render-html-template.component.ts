import {Component, HostListener, Input, OnChanges, ViewEncapsulation} from '@angular/core';
import { interval, Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-common-render-html-template',
  templateUrl: './common-render-html-template.component.html',
  styleUrls: ['./common-render-html-template.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class CommonRenderHtmlTemplateComponent implements OnChanges {
  @Input() htmlFile: any = '';
  @Input() width = '100%';
  @Input() type: 'email' | 'batch' = 'email';

  // Validation properties.
  scaleLimit = 1;
  isPresentRendering = false;

  scaleIframe(targetClass: string, modifyClass: string): void {
    if (this.type === 'email') {
      const destroy$ = new Subject<boolean>();
      // Scale HTML rendering container until remove horizontal scroll successfully.
      interval(0)
        .pipe(takeUntil(destroy$))
        .subscribe(() => {
          const iframe: any = document.querySelector(targetClass);
          const wrapperElement = document.querySelector(
            '.overview-popup--data-wrapper'
          );
          const clientWidth = iframe?.clientWidth || 0;
          const scrollWidth = iframe?.scrollWidth || 0;

          this.isPresentRendering = clientWidth >= scrollWidth;
          // Check if element's client width less than element's scroll width.
          if (clientWidth < scrollWidth) {
            this.scaleLimit = this.scaleLimit - 0.01;
            document
              .querySelector(modifyClass)
              ?.setAttribute(
                'style',
                `transform: scale(${this.scaleLimit}); transform-origin: top left;`
              );

            // Terminate scaling if scale limit less than 0.1
            if (this.scaleLimit < 0.1) {
              this.isPresentRendering = true;
              destroy$.next(true);
            }
          } else {
            // Set container height and overflow properties.
            wrapperElement?.setAttribute(
              'style',
              `overflow-y: auto; min-height: ${clientWidth}px; padding-bottom: 5px; padding-top: 5px;`
            );
            // Terminate scaling if element's client width more than or equal to scroll width
            destroy$.next(true);
          }
        });
    }

    if (this.type === 'batch') {
      const wrapperElement = document.querySelector(
        '.overview-popup--data-wrapper'
      );
      wrapperElement?.setAttribute(
        'style',
        `overflow-y: auto; padding-bottom: 5px; padding-top: 5px;`
      );
      this.isPresentRendering = true;
    }
  }

  ngOnChanges(): void {
    // Scale rendering HTML container when data change.
    this.scaleIframeChange();
  }

  @HostListener('window:resize', ['$event'])
  onMouseEnter() {
    this.scaleIframeChange();
  }

  scaleIframeChange(): void {
    interval(1000)
      .pipe(take(1))
      .subscribe(() => {
        this.scaleIframe(
          '.iframe-render-html-container',
          '.iframe-render__wrapper'
        );
      });
  }
}

@Component({
  selector: 'cxm-smartflow-preview-html-template',
  template: '<div [innerHTML]=\'(htmlTemplate || "") | safeHtml\'></div>',
  encapsulation: ViewEncapsulation.ShadowDom,
})
export class PreviewHtmlTemplateComponent {
  @Input() htmlTemplate: any;
}
