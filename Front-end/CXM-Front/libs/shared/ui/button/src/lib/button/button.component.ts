import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss'],
})
export class ButtonComponent {
  /**
   * Is this the principal call to action on the page?
   */
  @Input()
  primary = false;

  @Input()
  labelColor?: string;

  /**
   * What background color to use
   */
  @Input()
  backgroundColor?: string;

  @Input()
  borderRadius?: string;

  /**
   * How large should the button be?
   */
  @Input()
  size: 'small' | 'medium' | 'large' = 'medium';

  /**
   * Button contents
   *
   * @required
   */
  @Input()
  label = 'Button';

  @Input()
  width: string;

  @Input()
  margin: string;

  @Input()
  fontSize: string;

  @Input()
  fontWeight: string;

  @Input()
  disable = false;

  @Input()
  pathRouterLink: string;

  @Input()
  cxmClass = ['']
  @Input()
  cxmStyle: any;

  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onClick = new EventEmitter<Event>();

  public get classes(): string[] {
    const mode = this.primary
      ? 'storybook-button--primary'
      : 'cxm-btn cxm-btn-primary';
    return ['storybook-button', `storybook-button--${this.size}`, mode,].concat(this.cxmClass);
  }
}
