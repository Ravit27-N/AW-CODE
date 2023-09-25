import { Component, OnDestroy, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import {
  CommunicationInteractiveControlService,
  getRemotedUrl,
  getRemotedUrlSuccess,
  selectedRemotedUrl, unloadFormCIform
} from '@cxm-smartflow/communication-interactive/data-access';
import { Store } from '@ngrx/store';
import { Observable, of, ReplaySubject, Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';

export interface IframeModel {
  url?: string,
  height?: string,
  width?: string,
  message?: string,
}




@Component({
  selector: 'cxm-smartflow-communication-interactive-editor',
  templateUrl: './communication-interactive-editor.component.html',
  styleUrls: ['./communication-interactive-editor.component.scss']
})
export class CommunicationInteractiveEditorComponent implements OnInit, OnDestroy {

  destroy$ = new Subject<boolean>();

  ticketUrl$: Observable<SafeResourceUrl>;

  loading$ = new ReplaySubject<boolean>(1);


  ngOnInit(): void {

    this.ticketUrl$ = this.store.select(selectedRemotedUrl).pipe(takeUntil(this.destroy$))
    .pipe(filter(url => !!url)) // filter not blank
    .pipe(map(url => this.domSanitizer.bypassSecurityTrustResourceUrl(url)));

    this.activateRoute.queryParams.pipe(takeUntil(this.destroy$))
    .subscribe(q => {
      this.store.dispatch(getRemotedUrl({ id: Number(atob(q.id)) }));
      this.loading$.next(true);
    });
  }

  handleClose() {
    // close iframe and navigation success page
    this.ticketUrl$ = of();
    this.interactiveService.navigateToSuccessPage();
  }

  handleAfterFrameLoaded() {
    // turn off content placeholder loader
    this.loading$.next(false);
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.store.dispatch(unloadFormCIform());
  }


  constructor(private readonly domSanitizer: DomSanitizer,
    private readonly store: Store,
    private readonly activateRoute: ActivatedRoute,
    private readonly interactiveService: CommunicationInteractiveControlService,
    ) {
  }


  public mockTest() {
    const url = window.prompt("Enter interactive URL :");
    if(url) {
      this.store.dispatch(getRemotedUrlSuccess({ url }));
    }
  }

}
