
import { Component } from '@angular/core';
import { globalPropertiesLable } from '@cxm-smartflow/shared/data-access/model';
interface SideBarDataSource{
  icon?: string;
  text: string;
  link: string;
}


@Component({
  selector: 'cxm-smartflow-manage-my-campaign',
  templateUrl: './manage-my-campaign.component.html',
  styleUrls: ['./manage-my-campaign.component.scss']
})
export class ManageMyCampaignComponent {
  myCampaignLabel = globalPropertiesLable.cxmCampaign.manageMyCampaign;
  sideBarDataSource: SideBarDataSource[];
  constructor(){
    this.sideBarDataSource = [
    {icon: 'view_module',text: 'Model', link: 'model'},
     {icon: 'settings', text: 'Settings', link: 'setting'},
     {icon: 'near_me', text: 'Recipients', link: 'destination'},
     {icon: 'description', text: 'Summary', link: 'summary'}
    ];
  }

}
