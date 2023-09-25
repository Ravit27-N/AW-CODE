import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ReplaySubject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-common-search-list',
  templateUrl: './common-search-list.component.html',
  styleUrls: ['./common-search-list.component.scss']
})
export class CommonSearchListComponent implements OnInit, OnDestroy {

  selectedItem: any;
  searchTerm$ = new ReplaySubject<string>(1);

  @Output() onselect = new EventEmitter<any>();
  @Output() onsearch = new EventEmitter<string>();

  @Input() items: Array<any>;

  searchSubscription: Subscription;

  onsearchChanged($event: any) {
    this.searchTerm$.next($event.target.value);
    $event.target.focus();
  }

  onmenuOpened(inputElement: HTMLInputElement) {
    inputElement.focus();
  }

  chooseTemplate(item: any) {
    this.selectedItem = item;
    this.onselect.emit(item);
  }



  ngOnInit(): void {
    this.searchSubscription =
    this.searchTerm$.pipe(debounceTime(500), distinctUntilChanged()).subscribe(value => this.onsearch.emit(value));
  }

  ngOnDestroy(): void {
    this.searchSubscription.unsubscribe();
  }

  constructor() { }

}
