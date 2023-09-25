import { Component, Input } from '@angular/core';
import { NavigationItemModel } from '../../../core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-aw-navigation',
  templateUrl: './aw-navigation.component.html',
  styleUrls: ['./aw-navigation.component.scss'],
})
export class AwNavigationComponent {
  @Input() navigationItem: Array<NavigationItemModel> = [];

  constructor(private router: Router) {}

  async navigateTo(url: string, queryParams?: any): Promise<void> {
    await this.router.navigate([url], { queryParams });
  }
}
