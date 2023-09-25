import { CanAccess } from '@cxm-smartflow/shared/data-access/model';
import { AfterViewInit, Directive, ElementRef, Input, Renderer2 } from '@angular/core';
import { UserRightService } from '@cxm-smartflow/shared/data-access/services';

@Directive(
  {
    selector: '[cxmSmartflowCanAccess]'
  })
export class CanAccessDirective extends CanAccess implements AfterViewInit {

  nativeElement = this.element.nativeElement;

  constructor(private element: ElementRef,
              private renderer: Renderer2,
              private userRightService: UserRightService) {
    super();
  }

  @Input() set perm(value: string) {
    if (!value) {
      return;
    }
    const arr = value.match(/[^:]+/gm) || [''];
    if (arr.length > 0) {
      this.module = arr[0];
    }
    if (arr.length > 1) {
      this.feature = arr[1];
    }
  }

  ngAfterViewInit(): void {
    if (this.userRightService.requiredRight(this.module, this.feature, true)) {
      this.renderer.setStyle(this.nativeElement, 'display', 'initial');
    } else {
      this.renderer.setStyle(this.nativeElement, 'display', 'none');
    }
  }
}
