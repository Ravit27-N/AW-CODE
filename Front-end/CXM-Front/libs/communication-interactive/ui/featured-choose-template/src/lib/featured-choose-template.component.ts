import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';
import {
  CommunicationInteractiveControlService,
  CommunicationInteractiveResponse,
  loadCommunicationFilterChanged,
  loadCommunicationTemplate,
  selectCommunicationResponse,
  unloadFormCIform,
} from '@cxm-smartflow/communication-interactive/data-access';
import { Store } from '@ngrx/store';
import { Observable, ReplaySubject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-featured-choose-template',
  templateUrl: './featured-choose-template.component.html',
  styleUrls: ['./featured-choose-template.component.scss']
})
export class FeaturedChooseTemplateComponent implements OnInit, OnDestroy {

  templateResponse$: Observable<CommunicationInteractiveResponse>;

  selectedTemplate: any;
  searchTerm$ = new ReplaySubject<string>(1);

  preselectFirstItemSubscription: Subscription;

  @ViewChild(MatMenuTrigger, { static: true }) menuTrigger: MatMenuTrigger;

  constructor(private readonly store: Store, private readonly controlService: CommunicationInteractiveControlService) {
    this.templateResponse$ = this.store.select(selectCommunicationResponse);
    this.store.dispatch(loadCommunicationTemplate());
  }

  navigateURL() {
    if(this.selectedTemplate) {
      const { id } = this.selectedTemplate;
      this.controlService.navigateToEditor(id);
    }
  }

  chooseTemplate(item: any) {
    this.selectedTemplate = item;
    this.menuTrigger.closeMenu();
  }

  onsearchChanged($event: any) {
    this.searchTerm$.next($event.target.value);
    $event.target.focus();
  }

  onmenuOpened(inputElement: HTMLInputElement) {
    inputElement.focus();
  }

  ngOnInit(): void {
    this.searchTerm$.pipe(debounceTime(500), distinctUntilChanged())
    .subscribe(value => this.store.dispatch(loadCommunicationFilterChanged({ filters: { filter: value } })));

    this.preselectFirstItemSubscription = this.templateResponse$.pipe(filter(item => item.contents.length === 1))
    .subscribe(items => this.selectedTemplate = items.contents[0] );
  }

  ngOnDestroy(): void {
    this.preselectFirstItemSubscription?.unsubscribe();
    this.store.dispatch(unloadFormCIform());
  }
}
