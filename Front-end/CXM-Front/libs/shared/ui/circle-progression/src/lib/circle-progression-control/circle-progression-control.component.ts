import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { interval } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-circle-progression-control',
  templateUrl: './circle-progression-control.component.html',
  styleUrls: ['./circle-progression-control.component.scss'],
})
export class CircleProgressionControlComponent implements OnChanges {
  @Input() progressTitle = '';
  @Input() progressMessage = '';
  @Input() types: 'success' | 'danger' | 'info' | 'warning' = 'info';
  @Input() percentage = 0;
  @Input() r = 0;
  @Input() size = 200;
  @Input() strokeWidth = 50;

  displayPercentage = 75;
  strokeDashoffset = 100;

  ngOnChanges(changes: SimpleChanges): void {
    this.displayPercentage = 0;
    interval(500)
      .pipe(take(1))
      .subscribe(() => {
        this.strokeDashoffset = 100 - this.percentage;
        this.displayPercentage = this.percentage;
      });
  }
}
