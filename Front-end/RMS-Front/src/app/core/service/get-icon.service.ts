import { USER_ICON, G_USER_ICON } from '../../core/model/icon';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GetIconService {
  icon: any;
  bIcon = USER_ICON;
  gIcon = G_USER_ICON;
  constructor() { }
  checkGender(gender: string): any{
    switch (gender) {
      case 'Male':
        this.icon = this.bIcon;
        break;
      case 'Female':
        this.icon = this.gIcon;
        break;
      default:
        this.icon = this.bIcon;
        break;
    }
    return this.icon;
  }
}
