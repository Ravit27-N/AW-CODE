import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-link-button',
  templateUrl: './link-button.component.html',
  styleUrls: ['./link-button.component.scss']
})
export class LinkButtonComponent {

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
  boxShadow: string;

  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onClick = new EventEmitter<Event>();

  public get classes(): string[] {
    return ['storybook-button', 'storybook-link-button'];
  }
}
