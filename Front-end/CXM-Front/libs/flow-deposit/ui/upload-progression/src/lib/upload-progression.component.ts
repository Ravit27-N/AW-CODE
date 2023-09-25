import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { selectFileUploadState } from '@cxm-smartflow/flow-deposit/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Subscription } from 'rxjs';
import {  delay, tap } from 'rxjs/operators';

type ProgressionState = 'none' | 'error' | 'done' | 'progression';

@Component({
  selector: 'cxm-smartflow-upload-progression',
  templateUrl: './upload-progression.component.html',
  styleUrls: ['./upload-progression.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UploadProgressionComponent implements OnInit, OnDestroy {


  subscription: Subscription;
  value = 0;
  state$ = new BehaviorSubject<any>({});

  ngOnInit(): void {

    // Set default speed loading progression.
    let speed = 100;
    this.subscription =
      this.store.select(selectFileUploadState).pipe(tap(e => {

        // Apply speed 80 to loading progression.
        if (e.progress < 30 || e.progress > 80) speed = 80;
      }), delay(speed)).subscribe(state => {
        this.state$.next(state);

        // Set real percentage uploading.
        if (state.progress < 80) {
          this.value = state.progress;
        }

        // Set 100% if uploading has error.
        if (state.progress === 100 && state.error) {
          this.value = state.progress;
        }

        if (state.progress === 100 && state.done) {
          if(state.isCannotIdentify === null) {

            // Set 80% if uploading has 100% without identify document.
            this.value = 80;
          } else {

            // Set 100% if uploading has 100% with identify document.
            this.value = 100;
          }
        }
      })
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  constructor(private store: Store) { }

  getInnerBackground(state: any): string {

    // Error progress bar when uploading complete and occur error.
    if (state.progress === 100 && state.error) {
      return "#e50a0b 0% 0% no-repeat padding-box";
    }

    if (state.progress === 100 && state.done && state.isCannotIdentify !== null) {

      // Error progress bar when uploading complete and
      if(state.isCannotIdentify === false) {
        return "#04C60B 0% 0% no-repeat padding-box";
      } else {
        return "#e50a0b 0% 0% no-repeat padding-box";
      }
    }

    // Set blue color if during uploading.
    return "#a2bef6";
  }

  getOutsideBorder(state: any): string {

    // Remove border when document didn't identify.
    if (state.isCannotIdentify !== null || state.isValidateBeforeUpload) {
      return "";
    }

    // Add border when document identified.
    return "2px solid #7499e6";
  }

  getTransition(state: any): string {
    if(state.progress < 80) {
      return "all 2s ease-in"
    }

    return "all 0.5s ease-in";
  }
}
