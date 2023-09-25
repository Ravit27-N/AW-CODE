import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {selectFlowAttachment} from "@cxm-smartflow/flow-deposit/data-access";
import {map} from "rxjs/operators";
import {Store} from "@ngrx/store";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'cxm-smartflow-attachment-list',
  templateUrl: './attachment-list.component.html',
  styleUrls: ['./attachment-list.component.scss'],
})
export class AttachmentListComponent implements OnInit {

  attachments$: Observable<any>;
  constructor(private store: Store, private translate: TranslateService) {}

  ngOnInit(): void {
    this.attachments$ = this.store.select(selectFlowAttachment)
      .pipe(map(attachment => {
        if(attachment) {
          return Object.keys(attachment).map(key => {
            if(key === 'attachment1') { return { name: `background.position.FIRST_POSITION`, value: attachment[key] } }
            else if(key === 'attachment2') { return { name: `background.position.SECOND_POSITION`, value: attachment[key] }  }
            else if(key === 'attachment3') { return { name: `background.position.THIRD_POSITION`, value: attachment[key] }  }
            else if(key === 'attachment4') { return { name: `background.position.FOURTH_POSITION`, value: attachment[key] }  }
            else if(key === 'attachment5') { return { name: `background.position.FIFTH_POSITION`, value: attachment[key] }  }
            else return { }
          })
            .filter(y => Object.keys(y).length > 0 && y.value !== '');
        }

        return attachment;
      }));

  }
}
