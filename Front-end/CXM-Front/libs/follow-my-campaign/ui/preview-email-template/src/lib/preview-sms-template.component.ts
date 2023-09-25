import { Component, Input } from "@angular/core";



@Component({
  selector: 'cxm-smartflow-preview-sms-template',
  template: `
    <div class="sms-wrapper" [ngClass]="isHasBackground ? 'bg-white' : ''">
      <div style="background: url('assets/images/iphone-sms.png'); background-size: contain; background-repeat: no-repeat;" class="iphone relative">
        <div class="sms-content-wrapper">
          <div class="sms-content" [ngStyle]='smsContentMinMaxHeight'  [innerHTML]="smsContent|preview" *ngIf="smsContent.length > 0"></div>
        </div>
      </div>
  </div>
  `,
  styles: [`
    .sms-wrapper {
      width: 100%;
      height: 100%;
    }



    .iphone {
      background-size: contain;
      background-repeat: no-repeat;
      height: 100%;
      width: 463px;
    }

    .sms-content-wrapper {
      width: 350px;
      margin-right: auto;
      margin-left: auto;
      height: 432px;
      overflow-y: auto;
      overflow-x: hidden;
      position: absolute;
      top: 64px;
      right: 23px;
    }

    .sms-content {
      @apply px-4 py-4 shadow-sm;
      margin-top: 8.5rem;
      background-color: #3B82F6;
      font-size: 18px;
      width: 311px;
      /*min-height: 183px;*/
      /*max-height: 270px;*/
      border-radius: 5px;
      display: block;
      word-wrap: break-word;
      text-align: left;

      /*display: -webkit-box;*/
      /*-webkit-line-clamp: 6;*/
      /*-webkit-box-orient: vertical;*/
      /*text-overflow: ellipsis;*/
      /*overflow: hidden;*/

      overflow-y: auto;
      overflow-x: hidden;
      color: #f5f8fc;
    }

    .sms-content p {
      word-wrap: break-word;
    }
  `]
})
export class PreviewSmsTemplateComponent {

  @Input() smsContent: string;
  @Input() isHasBackground = false;
  @Input() width = '463px';

  @Input() smsContentMinHeight = '183px';
  @Input() smsContentMaxHeight = '265px';

  get smsContentMinMaxHeight(){
    return {"min-height": this.smsContentMinHeight, "max-height": this.smsContentMaxHeight};
  }
}
