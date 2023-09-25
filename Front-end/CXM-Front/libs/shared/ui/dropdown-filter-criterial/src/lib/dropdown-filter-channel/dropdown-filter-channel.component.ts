import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  CheckListModel,
  SubChannel,
} from '@cxm-smartflow/shared/ui/dropdown-filter-criterial';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-dropdown-filter-channel',
  templateUrl: './dropdown-filter-channel.component.html',
  styleUrls: ['./dropdown-filter-channel.component.scss']
})
export class DropdownFilterChannelComponent implements AfterViewInit, OnDestroy, OnChanges {
  @Input() subChannel: SubChannel [] = [];
  @Output() formChangeEvent = new EventEmitter<{ channels?: string[], subChannels?: string[] }>();

  @Input() useChannelFilter = false;

  channelFormGroup: FormGroup;
  channels: string [] = [];
  filterChannel = 'Multiple';
  subChannel$ = new BehaviorSubject([] as CheckListModel[]);

  constructor(private fb: FormBuilder) {
    this.channelFormGroup = this.fb.group({
      postal: new FormControl(false),
      digital: new FormControl(false)
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Set value to subChannels.
    if (changes?.subChannel) {
      // Filter sub channel by channel.
      const temp = this.subChannel
        .filter((item: any) => item?.key.includes(this.filterChannel.toLowerCase()))
        .map((value: SubChannel) => value?.value?.split(','))[0];

      this.subChannel$.next(this.mappingSubChannel(temp));
    }
  }

  ngAfterViewInit(): void {
    this.channelFormGroup.valueChanges.subscribe(formValue => {
      this.channels = Object.assign([], this.channels);

      const { postal, digital } = formValue;
      if (postal && digital) {
        this.channels = ['Postal', 'Digital'];
        this.filterChannel = 'Multiple';
      } else if (postal && !digital) {
        this.channels = ['Postal'];
        this.filterChannel = 'Postal';
      } else if (digital && !postal) {
        this.channels = ['Digital'];
        this.filterChannel = 'Digital';
      } else {
        this.channels = [];
        this.filterChannel = 'Multiple';
      }

      // Output value to outside.
      this.formChangeEvent.emit({ channels: this.channels });

      // Filter sub channel by channel.
      const temp = this.subChannel
        .filter((item: any) => item?.key.includes(this.filterChannel.toLowerCase()))
        .map((value: SubChannel) => value?.value?.split(','))[0];

      this.subChannel$.next(this.mappingSubChannel(temp));
    });
  }

  subChannelEvent($event: any) {
    this.formChangeEvent.emit({ channels: this.channels, subChannels: $event });
  }

  mappingSubChannel(subChannels: string []): CheckListModel [] {
    return subChannels?.map((subChannel: string) => ({
      key: subChannel?.charAt(0)?.toUpperCase().concat(subChannel.substr(1)),
      value: subChannel,
      checked: false
    }));
  }

  ngOnDestroy(): void {
    this.subChannel$.complete();
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }
}
