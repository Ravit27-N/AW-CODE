import { Directive, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { distinctUntilChanged } from 'rxjs/operators';
import { UserService } from '../auth';

@Directive({
  selector: '[appShowAuthed]'
})
export class ShowAuthedDirective implements OnInit {

  condition: boolean;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private userService: UserService) { }

  ngOnInit(): void {
    this.userService.isAuthenticated$.pipe(distinctUntilChanged()).subscribe(authenticated => {
      if ((authenticated && this.condition) || (!authenticated && !this.condition)) {
        this.viewContainer.createEmbeddedView(this.templateRef);
      } else {
        this.viewContainer.clear();
      }
    });
  }

  @Input() set appShowAuthed(condition: boolean) {
    this.condition = condition;
  }
}
