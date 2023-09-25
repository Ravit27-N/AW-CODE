import { AfterViewInit, Directive, ElementRef, Input, Renderer2 } from '@angular/core';
import { UserService } from './user.service';

export abstract class CanAccess {
  protected module: string;
  protected right: string[];
  protected accessRight;

  protected checkRight(): boolean {
    return true; // TODO: fix check privillege.

    const permissionRequired = {
      view: (module, access) => !!access && access[module].viewAble,
      add: (module, access) => !!access && access[module].insertAble,
      edit: (module, access) => !!access && access[module].editAble,
      delete: (module, access) => !!access && access[module].deleteAble,
    };

    if (this.isNotModule(this.module)) {
      return false;
    }

    if (this.right && this.right.length > 0) {
      return this.right.every(r => permissionRequired[r](this.module, this.accessRight));
    }

    return this.hasAll(permissionRequired, this.module, this.accessRight);
  }

  protected setAccessRight(value: any) {
    this.accessRight = value;
  }

  private isNotModule(module: string): boolean {
    return this.accessRight && this.accessRight[module] === undefined;
  }

  private hasAll(command, module: string, accessRight): boolean {
    return accessRight && command.add(module, accessRight) && command.delete(module, accessRight)
    && command.edit(module, accessRight) && command.view(module, accessRight);
  }
}

@Directive({
  selector: '[appCanAccess]'
})
export class CanAccessDirective extends CanAccess implements AfterViewInit {

  constructor(
    private element: ElementRef,
    private renderer: Renderer2,
    private userService: UserService
  ) {
    super();
  }

  ngAfterViewInit(): void {
    this.userService.userAccess$.subscribe(value => {
      this.setAccessRight(value);
      if (!this.checkRight()) {
        this.disableComponent();
      }
    });
  }

  disableComponent(): void {
    const theElement = this.element.nativeElement;
    if (theElement instanceof HTMLDivElement) {
      this.renderer.setStyle(theElement, 'visibility', 'hidden');
    } else if (theElement instanceof HTMLButtonElement) {
      this.renderer.setAttribute(theElement, 'disabled', 'true');
      this.renderer.addClass(theElement, 'mat-button-disabled');
    }
  }

  @Input() set perm(value: string) {
    if (!value) {
      console.log('appCanAccess: Null or undefine value');
      return;
    }

    const arr = value.match(/[^:]+/gm);
    if (arr.length > 0) {
      this.module = arr[0];
      if (arr.length > 1) {
        this.right = arr.slice(1);
        this.right = this.right.filter(x => ['add', 'edit', 'view', 'delete'].includes(x));
      }
    }
  }
}
