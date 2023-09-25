import { Component, Input } from '@angular/core';

export declare type ButtonType = 'error' | 'info';

@Component({
  selector: 'cxm-smartflow-rich-button',
  templateUrl: './rich-button.component.html',
  styleUrls: ['./rich-button.component.scss'],
})
export class RichButtonComponent {
  @Input() isRounded = false;
  @Input() style = '';
  @Input() type: ButtonType = 'info';
  @Input() class = '';
  @Input() disabled: boolean | null = null;

  classes(): string {
    return this.isRounded
      ? `button-alter ${this.type} ${this.class} `
      : `button-main ${this.type} ${this.class} `;
  }
}
