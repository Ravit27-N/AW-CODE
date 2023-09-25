import { Component, OnInit } from "@angular/core";
import { selectFlowBackground } from "@cxm-smartflow/flow-deposit/data-access";
import { Store } from "@ngrx/store";
import { TranslateService } from "@ngx-translate/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

@Component({
  selector: 'cxm-smartflow-background-list',
  templateUrl: './background-list.component.html',
  styleUrls: ['./background-list.component.scss']
})
export class BackgroundListComponent implements OnInit {

  background$: Observable<any>;


  ngOnInit(): void {
    this.background$ = this.store.select(selectFlowBackground)
    .pipe(map(background => {
      if(background) {
        return Object.keys(background).map(key => {
          if(key === 'background') { return { name: `background.position.${background.position}`, value: background[key] } }
          else if(key === 'backgroundFirst') { return { name: `background.position.${background.positionFirst}`, value: background[key] }  }
          else if(key === 'backgroundLast') { return { name: `background.position.${background.positionLast}`, value: background[key] }  }
          else return { }
        })
        .filter(y => Object.keys(y).length > 0 && y.value !== '');
      }
      return background;
    }));
  }

  constructor(private store: Store, private translate: TranslateService) {
    //
  }
}
