import { Component, OnInit } from "@angular/core";
import {selectFlowSignature, selectTreatmentResponse} from "@cxm-smartflow/flow-deposit/data-access";
import { Store } from "@ngrx/store";
import { Observable } from "rxjs";



@Component({
  selector: 'cxm-smartflow-signature-list',
  templateUrl: './signature-list.component.html',
  styleUrls: ['./signature-list.component.scss'],
})
export class SignatureListComponent implements OnInit
{
  signature$: Observable<any>;

  ngOnInit(): void {
    this.signature$ = this.store.select(selectFlowSignature);
    this.signature$.subscribe(console.log);
  }

  constructor(private store: Store) {

  }

}
