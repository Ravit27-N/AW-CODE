import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Store } from '@ngrx/store';
import { flowNavDoc } from '@cxm-smartflow/flow-traceability/data-access'
import { Observable } from 'rxjs';


@Component({
  selector: 'cxm-smartflow-flow-document-navigator',
  templateUrl: './flow-document-navigator.component.html',
  styleUrls: ['./flow-document-navigator.component.scss']
})
export class FlowDocumentNavigatorComponent implements OnInit {

  @Output() next = new EventEmitter();
  @Output() prev = new EventEmitter();
  @Input() currentPage = 0;
  @Input() totalPage = 0;

  selectDocNavigator$: Observable<any>;

  nextDoc() {
    this.store.dispatch(flowNavDoc.navNextDoc());
  }

  prevDoc() {
    this.store.dispatch(flowNavDoc.navPrevDoc());
  }


  ngOnInit(): void {
     this.selectDocNavigator$ =  this.store.select(flowNavDoc.selectDocNavigator);
  }

  constructor(private store: Store) {
    //
  }

}
