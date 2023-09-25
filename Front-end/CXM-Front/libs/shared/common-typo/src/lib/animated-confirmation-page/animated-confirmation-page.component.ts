import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AnimatedConfirmationPageOptions } from './AnimatedConfirmationPageOptions';
import { BehaviorSubject, interval } from 'rxjs';
import { take } from 'rxjs/operators';

export type AnimatedConfirmationType = 'Main' | 'Alter';

@Component({
  selector: 'cxm-smartflow-animated-confirmation-page',
  templateUrl: './animated-confirmation-page.component.html',
  styleUrls: ['./animated-confirmation-page.component.scss'],
  animations: [
    trigger('playShow', [
      state('prepared', style({
        // opacity: 0,
        fontSize: '0.1rem',
        position: 'absolute',
        top: '50%',
        right: 'calc(50% - 32px)'
        // transform:'translate(300px, 200px)'
      })),


      state('scaleUp', style({
        opacity: 1,
        fontSize: '20rem',
        position: 'absolute',
        width: '200px',
        height: '200px',
        top: '50%',
        right: 'calc(50% - 124px)'
      })),

      state('scaleDown', style({
        opacity: 1,
        fontSize: '4rem',
        position: 'absolute',
        right: 'calc(50% - 32px)'
      })),



      state('up', style({
        transform: 'translateY(0)',
        position: 'relative',
        opacity: 1,

      })),

       state('down', style({
        transform: 'translateY(110vh)',
        position: 'fixed',
        opacity: 0,
      })),


      // transition
      transition('prepared => scaleUp', [ animate('1s') ]),
      transition('scaleUp => scaleDown', [ animate('1s') ]),
      transition('scaleDown => scaleUp', [ animate('1s') ]),
      transition('down => up', [ animate('1s') ]),
      transition('up => down', [ animate('1s') ])

    ])
  ]
})
export class AnimatedConfirmationPageComponent implements OnInit {

  animation1$ = new BehaviorSubject<boolean>(false);
  animation2$ = new BehaviorSubject<boolean>(false);
  animation3$ = new BehaviorSubject<boolean>(false);
  animation4$ = new BehaviorSubject<boolean>(false);

  @Input() options: AnimatedConfirmationPageOptions;
  @Output() onactions = new EventEmitter<AnimatedConfirmationType>();

  isShown = false;

  constructor() {}

  ngOnInit(): void {
    interval(600).pipe(take(1)).subscribe(() => this.animation1$.next(true));
    interval(600).pipe(take(1)).subscribe(() => this.animation2$.next(true));
    interval(850).pipe(take(1)).subscribe(() => this.animation3$.next(true));
    interval(1500).pipe(take(1)).subscribe(() => {
      this.animation3$.next(false);
      this.animation4$.next(true);
    });
  }

  click() {
    this.isShown = true;
  }
}
