import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { OverlayContainer } from '@angular/cdk/overlay';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-campaign-filter-component',
  templateUrl: './campaign-filter-component.component.html',
  styleUrls: ['./campaign-filter-component.component.scss']
})
export class CampaignFilterComponentComponent implements OnInit {

  @Input() usingFilter = false;

  @Output() valueChange = new EventEmitter<{
    mode: string,
    type: string,
    useFilter: boolean
  }>();

  filterFormGroup: FormGroup;
  filterTitle = new BehaviorSubject('');

  @ViewChild(MatMenuTrigger, { static: true }) menuTrigger: MatMenuTrigger;
  constructor(private fb: FormBuilder, private translate: TranslateService, private overlayContainer: OverlayContainer) {

    this.filterFormGroup = this.fb.group({
      manual: new FormControl(false),
      automated: new FormControl(false),
      email: new FormControl(false),
      sms: new FormControl(false)
    });

    this.translate.get('cxmCampaign.followMyCampaign.list.filterComponent')
      .subscribe(value => this.filterTitle.next(value?.defaultTitle));
  }

  ngOnInit(): void {
    this.filterFormGroup.valueChanges?.subscribe(() => this.filterChange());
  }

  filterChange(){
    const hasFilters = Object.keys(this.filterFormGroup.controls).map(k => this.filterFormGroup.controls[k]).some( x => x.value === true);

    this.valueChange.emit({type: this.type, mode: this.mode, useFilter:hasFilters });
  }

  get type() {
    if(this.email?.value && this.sms?.value){
      return 'EMAIL,SMS';
    }else if(this.email?.value || this.sms?.value) {
      return this.email?.value ? 'EMAIL' : 'SMS';
    }
    return 'EMAIL,SMS';
  }

  get mode(){
    if(this.manual?.value&& this.automated?.value){
      return 'Manual,Automated';
    }
    else if(this.manual?.value || this.automated?.value){
      return this.manual?.value ? "Manual" : "Automated";
    }
    return 'Manual,Automated';
  }

  get manual() {
    return this.filterFormGroup.get('manual');
  }

  get automated(){
    return this.filterFormGroup.get('automated');
  }

  get email(){
    return this.filterFormGroup.get('email');
  }

  get sms(){
    return this.filterFormGroup.get('sms');
  }

  resetForm(){
    this.filterFormGroup?.reset();
  }

  menuOpen(){
    this.translate.get('cxmCampaign.followMyCampaign.list.filterComponent')
      .subscribe(value => this.filterTitle.next(value?.newTitle));
    // decrease z-index.
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  menuClose(){
    this.translate.get('cxmCampaign.followMyCampaign.list.filterComponent')
      .subscribe(value => this.filterTitle.next(value?.defaultTitle));
    // increase z-index.
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  preventCloseOnClickOut() {
    this.overlayContainer.getContainerElement().classList.add('custom-disable-backdrop-click');
  }

  allowCloseOnClickOut() {
    this.overlayContainer.getContainerElement().classList.remove('custom-disable-backdrop-click');
  }
}
