import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { appRoute, Language } from '@cxm-smartflow/shared/data-access/model';
import { BehaviorSubject, Subject } from 'rxjs';
import {
  CanAccessibilityService,
  CanVisibilityService,
  TranslateConfigService,
} from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { LoginGuard } from '@cxm-smartflow/auth/data-access';
import { CustomHeaderHandlerService } from './custom-header-handler.service';
import { Router, RoutesRecognized } from '@angular/router';
import { debounceTime, filter, pairwise, takeUntil, tap } from 'rxjs/operators';
import { PREVIOUS_URL } from '@cxm-smartflow/follow-my-campaign/data-access';
import { ChangePasswordDialogService } from '../../../../../../auth/ui/feature-change-password/src/lib/change-password-form/change-password-dialog.service';
import { OAuthService } from 'angular-oauth2-oidc';
import { Location } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ValidationPopupService } from './validation-popup/validation-popup.service';

@Component({
  selector: 'cxm-smartflow-custom-header',
  templateUrl: './custom-header.component.html',
  styleUrls: ['./custom-header.component.scss'],
})
export class CustomHeaderComponent implements OnInit, OnDestroy {
  languages: Language[] = [
    { value: 'fr', viewValue: 'FR' },
    { value: 'en', viewValue: 'EN' },
  ];

  toggleSelect = new BehaviorSubject(false);
  label = 'FR';

  isLogged = new BehaviorSubject(false);
  user$ = new BehaviorSubject('');

  private _isLogout: boolean;
  private _destroy$ = new Subject<boolean>();
  private _routeListener$ = new Subject();

  btnToggleHideShow = new BehaviorSubject(true);

  hasActiveAccount$ = new BehaviorSubject<boolean>(false);

  constructor(
    private translateConfigService: TranslateConfigService,
    private translate: TranslateService,
    private loginGuard: LoginGuard,
    private headerHandlerService: CustomHeaderHandlerService,
    private _router: Router,
    private changePasswordDialogService: ChangePasswordDialogService,
    private _oauthService: OAuthService,
    private _dialogRef: MatDialog,
    private _location: Location,
    private _validationPopup: ValidationPopupService,
    private canVisible: CanVisibilityService,
    private canAccess: CanAccessibilityService
  ) {
    this.hasActiveAccount$.next(canAccess.hasActiveAccount());
    this.loadUserProfile();
  }

  ngOnDestroy(): void {
    this.user$.complete();
    this._destroy$.complete();
  }

  ngOnInit(): void {
    this.checkPreviousRoute();
    this.setupLanguage();
    this.setupMenu();
    this.checkFlowValidationDocExist();
    this._routeListener$.next("Init");
  }

  changeLocale(language: Language) {
    this.translateConfigService.changeLocale(language.value);
    this.label = language.viewValue;
    this.toggleSelect.next(false);
  }

  toggleSelectLang(): void {
    this.toggleSelect.next(!this.toggleSelect.value);
  }

  loadUserProfile(): void {
    this.headerHandlerService.getUser().subscribe(user => this.user$.next(user));
  }

  changePassword(): void{
    if (!this._oauthService.hasValidAccessToken()) {
      location.replace('/login');
    } else {
      this.btnToggleHideShow.next(true);
      this.changePasswordDialogService.showForm();
    }
  }

  logout() {
    this.btnToggleHideShow.next(true);
    this.headerHandlerService.logout();
  }

  checkPreviousRoute(): void {
    this._router.events
      .pipe(
        tap(() => {
          this._routeListener$.next("Check routes");
        }),
        filter((evt: any) => evt instanceof RoutesRecognized),
        pairwise(),
        takeUntil(this._destroy$))
      .subscribe((events: RoutesRecognized[]) => {
        let previous: any[] = [];
        if (localStorage.getItem(PREVIOUS_URL)) {
          previous = [...new Set(JSON.parse(localStorage.getItem(PREVIOUS_URL) || ''))];
        }

        previous = [...previous, events[0].urlAfterRedirects];
        localStorage.setItem(PREVIOUS_URL, JSON.stringify(previous));

        this.setupMenu();
      });
  }

  setupMenu() {
    this.isLogged.next(this.loginGuard.isLogged());
  }

  setupLanguage() {
    this.label = localStorage.getItem('locale') || 'fr';
    this.translate.use(this.label);
  }

  toggleButton() {
    this.btnToggleHideShow.next(!this.btnToggleHideShow.value);
  }

  @HostListener('window:beforeunload', ['$event'])
  unloadHandler() {
    let previous: any[] = [];
    if (localStorage.getItem(PREVIOUS_URL)) {
      previous = [...new Set(JSON.parse(localStorage.getItem(PREVIOUS_URL) || ''))];
    }

    previous = [...previous, location.pathname];
    localStorage.setItem(PREVIOUS_URL, JSON.stringify(previous));
  }

  private _validateToken(): void {
    const isExcept = ['/forgot-password'].some(e => location.pathname.includes(e));
    if (!this._oauthService.hasValidAccessToken() && !isExcept) {
      location.replace(document.baseURI);
    }
  }

  private checkFlowValidationDocExist(): void {
    this._routeListener$.pipe(takeUntil(this._destroy$), debounceTime(400)).subscribe(() => {
      if (location.pathname.includes(appRoute.cxmAnalytics.navigateToDashboard)) {
        if (!localStorage.getItem('checkValidationDocExist')) {
          this.headerHandlerService
            .fetchValidationDocumentExists()
            .toPromise()
            .then((userValidation) => {
              if (userValidation.total > 0) {
                this._validationPopup
                  .open()
                  .toPromise()
                  .then((res) => {
                    // Disable request to check validation document existed.
                    localStorage.setItem('checkValidationDocExist', 'true');

                    // Navigate to validation space.
                    if (res) {
                      this._router.navigate([`/${appRoute.cxmApproval.navigateToValidateFlow}`]);
                    }
                  });
              }
            });
        }
      } else {
        this._validationPopup.dismiss();
      }
    });
  }


  @HostListener('window:storage')
  onStorageChange() {
    this._validateToken();
  }
}
