import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-aw-rating-star',
  templateUrl: './aw-rating-star.component.html',
  styleUrls: ['./aw-rating-star.component.scss'],
})
export class AwRatingStarComponent {
  @Input() rating: number;
  @Input() tooltipMessage: string;
  get stars() {
    return Array(Math.floor(this.rating)).fill(0);
  }
}
