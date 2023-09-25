import { AfterViewInit, Directive, ElementRef, Input, OnChanges, Renderer2, SimpleChanges } from '@angular/core';
import { CanAccess, CheckByLevel, CheckByType } from '@cxm-smartflow/shared/data-access/model';
import { UserRightService } from '@cxm-smartflow/shared/data-access/services';

@Directive({
  selector: '[cxmSmartflowCanModification]'
})
export class CanModificationDirective extends CanAccess implements AfterViewInit, OnChanges{
  nativeElement = this.element.nativeElement;
  constructor(
    private element: ElementRef,
    private renderer: Renderer2,
    private userRightService: UserRightService
  ) {
    super();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.setCreatedBy?.isFirstChange()) return;
    this.ngAfterViewInit();
  }

  ngAfterViewInit(): void {
    if(!this.userRightService.getUserRight(this.module, this.feature, this.createdBy, CheckByLevel.modificationLevel, CheckByType.directive)){
      this.setComponentDisplayNone();
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

  @Input() set setCreatedBy(value: string) {
    this.createdBy = value;
  }
}
