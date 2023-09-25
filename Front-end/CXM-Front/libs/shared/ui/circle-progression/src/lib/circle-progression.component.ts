import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { interval } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-circle-progression',
  templateUrl: './circle-progression.component.html',
  styleUrls: ['./circle-progression.component.scss']
})
export class CircleProgressionComponent implements OnChanges{

  @Input() progressTitle = '';
  @Input() progressMessage = '';
  @Input() types: 'success' | 'danger' | 'info' | 'warning' = 'info';
  @Input() percentage = 65;
  displayPercentage = 0;

  // State
  totalRange = 472;
  OneHundredPercent = 0.01;

  ngOnChanges(changes: SimpleChanges): void {

    this.displayPercentage = 0;
    interval(500).pipe(take(1)).subscribe(() => {
      this.displayPercentage = this.percentage;
    });

  }

}
