import { AfterViewInit, Directive, ElementRef, Input, Renderer2 } from '@angular/core';
import { CanAccess } from '@cxm-smartflow/shared/data-access/model';
import { UserRightService } from '@cxm-smartflow/shared/data-access/services';

@Directive({
  selector: '[cxmSmartflowAuthFor]'
})
export class AuthForDirective extends CanAccess implements AfterViewInit {
  nativeElement = this.element.nativeElement;

  ngAfterViewInit(): void {
    if (!this.userRightService.requiredRight(this.module, this.feature, this.validateAdmin)
    ) {
      this.setComponentDisplayNone();
    } else {
      this.setComponentDisplayInitial();
    }
  }

  @Input() validateAdmin = false;

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

  /**
   * Method used to hidden component.
   */
   private setComponentDisplayNone() {
    this.renderer.setStyle(this.nativeElement, 'display', 'none');
  }

  /**
   * Method used to show component.
   */
  private setComponentDisplayInitial() {
    this.renderer.setStyle(this.nativeElement, 'display', 'initial');
  }

  constructor(
    private element: ElementRef,
    private renderer: Renderer2,
    private userRightService: UserRightService
  ) {
    super();
  }
}
