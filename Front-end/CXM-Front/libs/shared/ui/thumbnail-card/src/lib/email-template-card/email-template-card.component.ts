import {
  CampaignConstant,
  SmsTemplate,
  TemplateConstant,
} from '@cxm-smartflow/template/data-access';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { globalPropertiesIcon } from '@cxm-smartflow/shared/data-access/model';
import { BehaviorSubject } from 'rxjs';
import {
  CanModificationService,
  CanVisibilityService,
} from '@cxm-smartflow/shared/data-access/services';
import { campaignReducer } from '@cxm-smartflow/follow-my-campaign/data-access';

@Component({
  selector: 'cxm-smartflow-email-template-card',
  templateUrl: './email-template-card.component.html',
  styleUrls: ['./email-template-card.component.scss'],
})
export class EmailTemplateCardComponent implements OnInit, OnDestroy {
  iconProps = globalPropertiesIcon;

  @Input()
  createdBy = '';

  @Input()
  width = '';

  @Input()
  height = '';

  @Input()
  fontSize = '';

  @Input()
  fontWeight = '';

  @Input()
  src = '';

  @Input()
  imageURL: string;

  @Input()
  title = '';

  @Input()
  createdAt: Date | undefined;

  @Input()
  cxmClass: string[];

  @Input()
  border: string;

  @Input()
  isFocus = false;

  @Input()
  templateType: string;

  @Input()
  cxmStyle: any;
  @Input() isSidebar = true;

  @Input() isChoiceOfModel = false;

  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onClickSettings = new EventEmitter<Event>();
  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onClickEdit = new EventEmitter<Event>();
  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onClickDelete = new EventEmitter<Event>();
  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onClickCopy = new EventEmitter<Event>();
  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onClickCard = new EventEmitter<Event>();
  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onFocus = new EventEmitter<Event>();
  @Output() clickSelect = new EventEmitter<Event>();
  @Output() clickVisible = new EventEmitter<Event>();

  // validated properties
  isSettingOptionOpen$ = new BehaviorSubject(false);
  isCanDuplicate$ = new BehaviorSubject<boolean>(false);
  isCanDelete$ = new BehaviorSubject<boolean>(false);
  isCanEdit$ = new BehaviorSubject<boolean>(false);
  isCanModify$ = new BehaviorSubject<boolean>(false);
  isCanSelect$ = new BehaviorSubject<boolean>(false);
  isCanVisible$ = new BehaviorSubject<boolean>(false);
  isNoAllPrivilege$ = new BehaviorSubject<boolean>(false);
  isShowTools$ = new BehaviorSubject<boolean>(false);

  public get classes(): string[] {
    return ['storybook-email-template'];
  }

  constructor(
    private readonly canVisibilityService: CanVisibilityService,
    private readonly canModificationService: CanModificationService
  ) {}

  ngOnInit(): void {
    this.checkIsCanDuplicate();
    this.checkIsCanDelete();
    this.checkIsCanEdit();
    this.checkIsCanModify();
    this.checkIsCanSelect();
    this.checkIsCanVisible();
    this.checkIsNoAllPrivilege();
  }

  ngOnDestroy(): void {
    this.isSettingOptionOpen$.next(false);
    this.isSettingOptionOpen$.complete();
    this.isCanDuplicate$.complete();
    this.isCanDelete$.complete();
    this.isCanEdit$.complete();
    this.isCanModify$.complete();
    this.isCanSelect$.complete();
    this.isCanVisible$.complete();
    this.isNoAllPrivilege$.complete();
    this.isShowTools$.complete();
  }

  checkIsCanDuplicate() {
    const isValid = () => {
      if (this.templateType === 'EMAILING')
        return this.canVisibilityService.getUserRight(
          TemplateConstant.CXM_TEMPLATE,
          TemplateConstant.DUPLICATE,
          this.createdBy,
          true
        );
      if (this.templateType === 'SMS')
        return this.canVisibilityService.getUserRight(
          SmsTemplate.CXM_SMS_TEMPLATE,
          SmsTemplate.CREATE_BY_DUPLICATE,
          this.createdBy,
          true
        );
      else return false;
    };
    this.isCanDuplicate$.next(isValid() && !this.isChoiceOfModel);
  }

  checkIsCanDelete() {
    const isValid = () => {
      if (this.templateType === 'EMAILING')
        return this.canModificationService.getUserRight(
          TemplateConstant.CXM_TEMPLATE,
          TemplateConstant.DELETE,
          this.createdBy,
          true
        );
      if (this.templateType === 'SMS')
        return this.canModificationService.getUserRight(
          SmsTemplate.CXM_SMS_TEMPLATE,
          SmsTemplate.DELETE,
          this.createdBy,
          true
        );
      else return false;
    };

    this.isCanDelete$.next(isValid() && !this.isChoiceOfModel);
  }

  checkIsCanEdit() {
    const isValid = () => {
      if (this.templateType === 'EMAILING')
        return this.canVisibilityService.getUserRight(
          TemplateConstant.CXM_TEMPLATE,
          TemplateConstant.EDIT,
          this.createdBy,
          true
        );
      if (this.templateType === 'SMS')
        return this.canVisibilityService.getUserRight(
          SmsTemplate.CXM_SMS_TEMPLATE,
          SmsTemplate.EDIT,
          this.createdBy,
          true
        );
      else return false;
    };

    this.isCanEdit$.next(isValid() && !this.isChoiceOfModel);
  }

  checkIsCanModify() {
    const isValid = () => {
      if (this.templateType === 'EMAILING')
        return this.canModificationService.getUserRight(
          TemplateConstant.CXM_TEMPLATE,
          TemplateConstant.MODIFY,
          this.createdBy,
          true
        );
      if (this.templateType === 'SMS')
        return this.canModificationService.getUserRight(
          SmsTemplate.CXM_SMS_TEMPLATE,
          SmsTemplate.MODIFY,
          this.createdBy,
          true
        );
      else return false;
    };

    this.isCanModify$.next(isValid() && !this.isChoiceOfModel);
  }

  checkIsCanSelect() {
    const isValid = () => {
      if (this.templateType === 'EMAILING')
        return this.canVisibilityService.getUserRight(
          CampaignConstant.CXM_CAMPAIGN,
          CampaignConstant.CHOOSE_MODEL,
          this.createdBy,
          true
        );
      else if (this.templateType === 'SMS')
        return this.canVisibilityService.getUserRight(
          CampaignConstant.CXM_CAMPAIGN_SMS,
          CampaignConstant.CHOOSE_MODEL_SMS,
          this.createdBy,
          true
        );
      else return false;
    };

    this.isCanSelect$.next(isValid() && this.isChoiceOfModel);
  }

  checkIsCanVisible() {
    const isValid = () => {
      if (this.templateType === 'EMAILING')
        return this.canVisibilityService.getUserRight(
          TemplateConstant.CXM_TEMPLATE,
          TemplateConstant.EDIT,
          this.createdBy,
          true
        );
      if (this.templateType === 'SMS')
        return this.canVisibilityService.getUserRight(
          SmsTemplate.CXM_SMS_TEMPLATE,
          SmsTemplate.EDIT,
          this.createdBy,
          true
        );
      else return false;
    };

    this.isCanVisible$.next(isValid() && this.isChoiceOfModel);
  }

  checkIsNoAllPrivilege() {
    const value =
      this.isCanDuplicate$.value ||
      this.isCanDelete$.value ||
      this.isCanEdit$.value ||
      this.isCanModify$.value ||
      this.isCanSelect$.value ||
      this.isCanVisible$.value;
    this.isNoAllPrivilege$.next(value);
  }

  toggleSettingOption() {
    this.isSettingOptionOpen$.next(!this.isSettingOptionOpen$.value);
  }
}
