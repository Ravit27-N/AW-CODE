import { Component, Input } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.scss'],
})
export class ImageComponent {
  @Input() imageURL: any;
  @Input() cxmClass: string[];
  @Input() cxmStyle: any;
  defaultImage = 'assets/images/loading1.gif';
  errorImage = this.defaultImage;
}
