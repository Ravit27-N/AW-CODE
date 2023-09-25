import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {EventHistorySize, FlowStatusConstant} from '@cxm-smartflow/flow-traceability/util';
import {EventHistory} from './event-history.interface';
import {FalsyUtil} from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-flow-event-history',
  templateUrl: './flow-event-history.component.html',
  styleUrls: ['./flow-event-history.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class FlowEventHistoryComponent implements OnChanges{
  /** Set event histories **/
  @Input() eventHistories: EventHistory[] = [];
  @Output() viewStatusInfo = new EventEmitter<boolean>();

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  ngOnChanges(changes: SimpleChanges) {
    for (const [index, event] of Object.entries(this.eventHistories)) {
      if (event.validatedOrRefused) {
        this.eventHistories[Number(index)].flowCommentStatus = event?.countComment !== 0 && event?.countComment != undefined;
      } else {
        this.eventHistories[Number(index)].flowCommentStatus = false;
        this.eventHistories[Number(index)].comment = "";
      }
    }
  }
  /**
   * Get the HTML class for setting the style of Event history status
   * base on {@link EventHistory.mode} and {@link EventHistory.size}
   *
   * @param eventHistory of {@link EventHistory}
   */
  getClasses(eventHistory: EventHistory): string {
    // Event history mode such as info, danger, secondary, success.
    const mode = FalsyUtil.isTruthyReturnValue(eventHistory?.mode, '');

    // Event history font-size such as small, medium.
    const size = FalsyUtil.isTruthyReturnValue(eventHistory?.size, EventHistorySize.MEDIUM);

    return `event ${mode} ${size}`;
  }

  /**
   * Get the tooltip message for displaying the tooltip.
   * @param eventHistory
   */
  getTooltipMsg(eventHistory: EventHistory): string {
    const status = eventHistory.eventHistoryInfo?.statuses?.join('\n');
    const description = eventHistory.eventHistoryInfo?.description || '';
    return `${status? status?.concat('\n') : ''} ${description}`;
  }

  viewStatus(eventHistory: EventHistory): void {
    if (eventHistory.eventHistoryInfo?.description?.length === 0) {
      this.viewStatusInfo.next(true);
    }
  }

  getIsShowSpinner(eventHistory: EventHistory): boolean {
    return !!eventHistory.eventHistoryInfo && eventHistory.eventHistoryInfo?.description?.length === 0;
  }
}
