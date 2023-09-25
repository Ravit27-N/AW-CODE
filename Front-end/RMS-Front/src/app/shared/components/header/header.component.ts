import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../../auth';
import { Observable } from 'rxjs';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router,
} from '@angular/router';
import { IsLoadingService } from '@service-work/is-loading';
import { filter } from 'rxjs/operators';
import { getAssetPrefix } from '../../utils';

export interface CandidateData {
  id: string;
  firstname: string;
  lastname: string;
  priority: string;
  from: string;
  gpa: number;
  position: string;
  telephone: string;
  status: string;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  isLoading: Observable<boolean>;
  assetPrefix = getAssetPrefix();

  constructor(
    private userService: UserService,
    public dialog: MatDialog,
    private isLoadingService: IsLoadingService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.isLoading = this.isLoadingService.isLoading$();

    this.router.events
      .pipe(
        filter(
          (event) =>
            event instanceof NavigationStart ||
            event instanceof NavigationEnd ||
            event instanceof NavigationCancel ||
            event instanceof NavigationError,
        ),
      )
      .subscribe((event) => {
        // if it's the start of navigation, `add()` a loading indicator
        if (event instanceof NavigationStart) {
          this.isLoadingService.add();
          return;
        }

        // else navigation has ended, so `remove()` a loading indicator
        this.isLoadingService.remove();
      });
  }

  logout(): void {
    this.userService.logout();
  }

  get currentUser(): string {
    return this.userService.getCurrentUser()?.preferred_username;
  }
}
