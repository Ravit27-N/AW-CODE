import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { appLocalStorageConstant } from '@cxm-smartflow/shared/data-access/model';
import { AuthService } from '@cxm-smartflow/auth/data-access';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import {
  CanAccessibilityService,
  UserUtil,
} from '@cxm-smartflow/shared/data-access/services';
import { Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-navigation-bar',
  templateUrl: './navigation-bar.component.html',
  styleUrls: ['./navigation-bar.component.scss'],
})
export class NavigationBarComponent implements OnInit, OnDestroy {
  readonly currentUserPrivileges: string[] = UserUtil.getCurrentUserPrivilege();
  readonly isAdminUser: boolean = UserUtil.isAdmin();
  readonly destroy$ = new Subject<boolean>();
  presentedMenu: any;
  isEnglishLocale: boolean;

  constructor(
    private readonly authService: AuthService,
    private readonly store: Store,
    private readonly translateService: TranslateService,
    private readonly canAccessService: CanAccessibilityService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.setupLocale();
    this.mapMenu().then();
    this.remapMenu();
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  private setupLocale() {
    // Check if no any initial locale in localstorage. Set French language as default.
    if (!localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale)) {
      localStorage.setItem(
        appLocalStorageConstant.Common.Locale.Locale,
        appLocalStorageConstant.Common.Locale.Fr
      );
    }

    // Translate webpage to selected language.
    this.translateService.use(
      localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) ||
        appLocalStorageConstant.Common.Locale.Fr
    );

    this.isEnglishLocale =
      localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) ===
      appLocalStorageConstant.Common.Locale.En;
  }

  private async mapMenu(): Promise<void> {
    const menu = await this.translateService.get('menuBars').toPromise();
    this.presentedMenu = Object.keys(menu)
      .map((m) => {
        const mappedMenu = this.mapAccessibleMenu(menu[m]);
        if (mappedMenu.visible) {
          return {
            [m]: {
              ...this.mapAccessibleMenu(menu[m]),
              active: this.mapActiveMenu(
                menu[m].activeMenu,
                menu[m].inActiveMenu || ''
              ),
            },
          };
        }
        return;
      })
      .reduce((pre, curr) => {
        return Object.assign(pre, curr);
      }, {});
  }

  private mapAccessibleMenu(menu: any) {
    const menuAccessible =
      (menu.isAdmin && this.isAdminUser) ||
      menu.access
        .split(',')
        .map((e: string) => e.trim())
        .some((e: string) => this.currentUserPrivileges.includes(e));
    const subMenu = [...(menu.subMenu || [])].filter((e) => {
      if (!e.access?.trim()) return false;
      return (
        (e.isAdmin && this.isAdminUser) ||
        e.access
          .split(',')
          .map((e: string) => e.trim())
          .some((k: string) => this.currentUserPrivileges.includes(k))
      );
    });
    const isMenuHasSubMenu = menu.subMenu?.length > 0 || false;
    let visible;
    if (isMenuHasSubMenu) {
      visible = subMenu.length > 0;
    } else {
      visible = menuAccessible;
    }
    return { ...menu, visible, subMenu };
  }

  private mapActiveMenu(activeURL: string, inActiveURL: string): boolean {
    const activeMenus = activeURL.split(',').map((e) => e.trim());
    const inActiveMenus = inActiveURL.split(',').map((e) => e.trim());
    const isExcludedMenuValid = inActiveMenus.some((e) => {
      return e?.trim() ? location.pathname.includes(e) : false;
    });
    const isActiveMenuValid = activeMenus.some((e) => {
      return e?.trim() ? location.pathname.includes(e) : false;
    });

    if (isActiveMenuValid) {
      return !isExcludedMenuValid;
    }
    return false;
  }

  private remapMenu(): void {
    // Remap menu after route navigated.
    this.router.events
      .pipe(takeUntil(this.destroy$), debounceTime(100), distinctUntilChanged())
      .subscribe(() => {
        this.mapMenu().then();
      });
  }
}
