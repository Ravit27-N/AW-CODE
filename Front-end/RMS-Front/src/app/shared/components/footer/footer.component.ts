import { Component, Input } from '@angular/core';

// @ts-ignore
import * as config from '../../../../../package.json';
@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {

  @Input() marginless: boolean;
  version = config.version ;
  currentYear = new Date().getFullYear();
  constructor() { }

}
