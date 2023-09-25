import {ChangeDetectionStrategy, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute} from '@angular/router';
import {
  AttachmentDetail,
  attachSettingOptionPopup,
  attemptProductCriteriaOption,
  DefaultConfiguration,
  deleteOptionAttribute,
  deleteWatermark,
  fetchAddedAttachmentDetail,
  fetchWatermark,
  fetchWatermarkEdit,
  getAllAttachmentSettingOption,
  getLimitUploadFileSize,
  Go2PdfAttachmentPosition,
  Go2pdfBackgroundPosition,
  hasPortalSignatureConfig,
  selectAttachmentList,
  selectBackgroundList,
  selectCanAddAttachment,
  selectCanAddBackground,
  selectCanAddSignature,
  selectCanAddWatermark,
  selectIsValidSignature,
  selectLimitUploadFileSize,
  selectProcessControlResponseState,
  selectSignaturesList,
  selectWatermark,
  SettingOptionCriteriaType,
  WatermarkAttribute
} from '@cxm-smartflow/flow-deposit/data-access';
import {FileUtils} from '@cxm-smartflow/shared/utils';
import {Store} from '@ngrx/store';
import {BehaviorSubject, Observable, Subject, Subscription} from 'rxjs';
import {filter, map, mergeMap, take, takeUntil} from 'rxjs/operators';
import {UserProfileUtil, UserUtil} from '@cxm-smartflow/shared/data-access/services';
import {EnrichmentMailing, WatermarkResource} from '@cxm-smartflow/shared/data-access/model';
import {TranslateService} from '@ngx-translate/core';
import {WatermarkColorUtil} from '@cxm-smartflow/flow-deposit/util';


@Component({
  selector: 'cxm-smartflow-criteria-options',
  templateUrl: './criteria-options.component.html',
  styleUrls: ['./criteria-options.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class CriteriaOptionsComponent implements OnInit, OnDestroy, OnChanges {

  @Input() defaultConfiguration: DefaultConfiguration;
  @Input() openOptions: boolean = false;

  optionsForm: FormGroup;
  disabledRadioBtn = true;

  sizeLimit$: Observable<string>;
  destroy$ = new Subject<boolean>();
  waterMarkBeforeChangePosition: any;
  // Option attributes.
  signatures$: Observable<Array<AttachmentDetail>>;
  backgrounds$: Observable<Array<AttachmentDetail>>;
  attachments$: Observable<Array<AttachmentDetail>>;
  waterMark$: Observable<WatermarkAttribute>;
  canAddWatermark$: Observable<boolean>;
  // Option attribute privileges.
  canAddSignatures$: Observable<boolean>;
  canAddBackground$: Observable<boolean>;
  canAddAttribute$: Observable<boolean>;
  isValidSignature$: Observable<boolean>;
  isCanAdd = UserProfileUtil.canAccess(
    EnrichmentMailing.CXM_ENRICHMENT_MAILING,
    EnrichmentMailing.ADD_RESOURCE
  );
  isCanUpload = UserProfileUtil.canAccess(
    EnrichmentMailing.CXM_ENRICHMENT_MAILING,
    EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE
  );


  isCanCreateWatermark = UserProfileUtil.canAccess(
    WatermarkResource.CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY,
    WatermarkResource.CXM_CREATE_WATERMARK
  );

  isCanUpdateWatermark = UserProfileUtil.getInstance().canModify({
    func: WatermarkResource.CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY,
    priv: WatermarkResource.CXM_MODIFY_WATERMARK,
    ownerId: UserUtil.getOwnerId(),
    checkAdmin: false
  });

  isCanDeleteWaterMark = UserProfileUtil.getInstance().canModify({
    func: WatermarkResource.CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY,
    priv: WatermarkResource.CXM_DELETE_WATERMARK,
    ownerId: UserUtil.getOwnerId(),
    checkAdmin: false
  });

  isUsingLibrary = UserProfileUtil.canAccess(
    EnrichmentMailing.CXM_ENRICHMENT_MAILING,
    EnrichmentMailing.USE_RESOURCE_IN_LIBRARY
  );

  workflowDefaultConfig = false;
  hasAppliedDefaultConfig = false;
  signatureHasDefaultConfig = false;
  backgroundHasDefaultConfig = false;
  attachmentHasDefaultConfig = false;
  watermarkHasDefaultConfig = false;

  internalBackgroundMap$ = new BehaviorSubject<Array<AttachmentDetail>>([]);
  internalAttachmentMap$ = new BehaviorSubject<Array<AttachmentDetail>>([]);

  private _subscriptions$ = new Subscription();

  constructor(
    private _formBuilder: FormBuilder,
    private _store: Store,
    private _activateRoute: ActivatedRoute,
    private _matDialog: MatDialog,
    private _translateService: TranslateService
  ) {
    this.optionsForm = this._formBuilder.group({
      confirmation: new FormControl(false),
      validation: new FormControl(false),
      archiving: new FormControl(false),
      ged: new FormControl(false),
      recoveryMode: new FormControl(false),
      recoveryTime: new FormControl(null),
      watermark: new FormControl(''),
      recoveryType: new FormControl(null)
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.defaultConfiguration) {
      this._activateRoute.queryParams.pipe(take(1)).subscribe((v) => {
        if (v?.validation) {
          this.optionsForm.patchValue({validation: JSON.parse(v?.validation)}, {onlySelf: true, emitEvent: true});
        } else {
          this.optionsForm.patchValue({validation: false}, {onlySelf: true, emitEvent: true});
        }
      });
    }
    if (changes?.openOptions && !changes?.openOptions.firstChange) {
      // Select option attribute privilege.
      this.canAddSignatures$ = this._store.select(selectCanAddSignature);
      this.canAddBackground$ = this._store.select(selectCanAddBackground);
      this.canAddAttribute$ = this._store.select(selectCanAddAttachment);
      this.isValidSignature$ = this._store.select(selectIsValidSignature);
      this.canAddWatermark$ = this._store.select(selectCanAddWatermark);
      this.prefillDefaultConfigurationColor();
    }
  }

  ngOnInit(): void {
    // Navigate with production criteria params.
    this.optionsForm.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this._store.dispatch(
          attemptProductCriteriaOption({productionCriteriaOption: data})
        );
      });

    this._store.dispatch(getLimitUploadFileSize());
    this.sizeLimit$ = this._store.select(selectLimitUploadFileSize);

    // Select options attributes.
    this._store.dispatch(getAllAttachmentSettingOption());
    this._store
      .select(selectProcessControlResponseState)
      .pipe(filter((res) => res && Object.keys(res).length !== 0), take(1))
      .subscribe((res) => {
        const modelName = res?.data?.ModeleName;
        this._store.dispatch(hasPortalSignatureConfig({modelName}));
      });

    this.signatures$ = this._store.select(selectSignaturesList);
    this.attachments$ = this._store.select(selectAttachmentList);
    this.backgrounds$ = this._store.select(selectBackgroundList);

    this._store.dispatch(fetchWatermark());

    const translates = this._translateService.get('background.position')
      .pipe(mergeMap(background => this._translateService.get('flow.deposit.setting_option_display')
        .pipe(map(flowDeposit => {
          return {background, flowDeposit}
        }))));
    this.waterMark$ = this._store.select(selectWatermark).pipe(
      mergeMap(value => translates.pipe(
        filter(value1 => value1.background[`${value?.position}`]),
        map(value1 => {

          this.waterMarkBeforeChangePosition = value;

          const background = value1.background[`${value.position}`] ? value1.background[`${value.position}`] : '';
          const size = value1.flowDeposit[`size`];
          const rotation = value1.flowDeposit[`rotation`];
          const position = !background ? '' : `${background} ( ${size} ${value.size} ${rotation} ${value.rotation} )`;
          const color = WatermarkColorUtil.textToColorPicker(value.color);

          this.watermarkHasDefaultConfig = value?.default || false;
          this.checkHasApplyDefault();

          return {
            ...value,
            position,
            color
          };
        })))
    );

    this._store.select(selectWatermark).subscribe(value => {
      this.watermarkHasDefaultConfig = value?.default || false;
      this.checkHasApplyDefault();
    });


  }

  transformWorkflow(workflow?: string): boolean {
    if(workflow === 'Validate'){
      return true;
    }
    if(workflow === 'NO'){
      return false;
    }
    return false;
  }

  prefillDefaultConfigurationColor() {
    this.optionsForm.valueChanges.subscribe(form => {
      // workflow configuration color.
      const workflowDefaultConfig = ['Validate', 'NO'];
      if (this.defaultConfiguration.Workflow && workflowDefaultConfig.some(item => item === this.defaultConfiguration.Workflow)) {
        const workflow = this.transformWorkflow(this.defaultConfiguration.Workflow);
        if (form?.validation === workflow) {
          this.workflowDefaultConfig = true;
        } else {
          this.workflowDefaultConfig = false;
        }
      } else {
        this.workflowDefaultConfig = false;
      }

      this.checkHasApplyDefault();
    });

    const signaturesSubscription$ = this.signatures$.subscribe((signature: Array<AttachmentDetail>) => {
      this.signatureHasDefaultConfig = signature.some(item => `${item.fileId}.png` === this.defaultConfiguration.Signature);
      this.checkHasApplyDefault();
    });
    this._subscriptions$.add(signaturesSubscription$);

    const backgroundSubscription$ = this.backgrounds$.subscribe((background: Array<AttachmentDetail>) => {
      const backgroundMap = background.map(item => {
        if ((this.mapBackgroundPosition(item) === this.defaultConfiguration.PositionFDP) &&
          (item.fileId + '.pdf' === this.defaultConfiguration.FDP)) {
          return {
            ...item,
            selected: true
          };
        }
        return item;
      });

      this.backgroundHasDefaultConfig = backgroundMap.some(item => `${item.fileId}.pdf` === this.defaultConfiguration.FDP);

      this.internalBackgroundMap$.next(backgroundMap);
      this.checkHasApplyDefault();
    });
    this._subscriptions$.add(backgroundSubscription$);

    const attachmentSubscription$ = this.attachments$.subscribe((attachment: Array<AttachmentDetail>) => {
      const pjs = [
        Go2PdfAttachmentPosition.Pj1,
        Go2PdfAttachmentPosition.Pj2,
        Go2PdfAttachmentPosition.Pj3,
        Go2PdfAttachmentPosition.Pj4,
        Go2PdfAttachmentPosition.Pj5
      ];

      const defaultPJs = Object.keys(this.defaultConfiguration).filter(key => pjs.some(item => item === key))
        .map(objectKey => {
          let key = null;
          let value = null;
          if (objectKey === Go2PdfAttachmentPosition.Pj1) {
            key = objectKey;
            value = this.defaultConfiguration[objectKey];
          }
          if (objectKey === Go2PdfAttachmentPosition.Pj2) {
            key = objectKey;
            value = this.defaultConfiguration[objectKey];
          }
          if (objectKey === Go2PdfAttachmentPosition.Pj3) {
            key = objectKey;
            value = this.defaultConfiguration[objectKey];
          }
          if (objectKey === Go2PdfAttachmentPosition.Pj4) {
            key = objectKey;
            value = this.defaultConfiguration[objectKey];
          }
          if (objectKey === Go2PdfAttachmentPosition.Pj5) {
            key = objectKey;
            value = this.defaultConfiguration[objectKey];
          }
          return {
            key: key,
            value: value
          };
        });

      const attachmentMap = attachment.map(item => {
        if (defaultPJs.some(pj => pj.key === this.mapAttachmentPosition(item) && pj.value === item.fileId + '.pdf')) {
          return {
            ...item,
            selected: true
          };
        }

        return item;
      });

      this.attachmentHasDefaultConfig = attachment.some(item => {
        return defaultPJs.some(pj => pj.key ==  this.mapAttachmentPosition(item) && pj.value === `${item.fileId}.pdf`);
      });

      this.internalAttachmentMap$.next(attachmentMap);
      this.checkHasApplyDefault();
    });

    this._subscriptions$.add(attachmentSubscription$);

    const watermarkSubscription$ = this.waterMark$.subscribe(value => {

      const {FilSize, FilRotation, FilText, FilPosition, FilColor} = this.defaultConfiguration;

      const isDefaultFileSize = (FilSize == "" && this.waterMarkBeforeChangePosition.size == 0) ||
        Number(FilSize) == this.waterMarkBeforeChangePosition.size;
      const isDefaultFileRotation = (FilSize == "" && this.waterMarkBeforeChangePosition.rotation == 0) ||
        Number(FilRotation) == this.waterMarkBeforeChangePosition.rotation;
      const isDefaultFileText = FilText == this.waterMarkBeforeChangePosition.text;
      const isDefaultFilePosition = FilPosition == this.waterMarkBeforeChangePosition.position;
      const isDefaultFileColor = FilColor == this.waterMarkBeforeChangePosition.color;

      this.watermarkHasDefaultConfig = isDefaultFileColor &&
        isDefaultFileSize &&
        isDefaultFilePosition &&
        isDefaultFileRotation &&
        isDefaultFileText;
      this.checkHasApplyDefault();
    });
    this._subscriptions$.add(watermarkSubscription$);

  }

  checkHasApplyDefault() {
    this.hasAppliedDefaultConfig = this.workflowDefaultConfig || this.signatureHasDefaultConfig
      || this.backgroundHasDefaultConfig || this.attachmentHasDefaultConfig || this.watermarkHasDefaultConfig;
  }

  mapBackgroundPosition(background: AttachmentDetail) {
    if (background.position === 'FIRST_PAGE') {
      return Go2pdfBackgroundPosition.First;
    } else if (background.position === 'NEXT_PAGES') {
      return Go2pdfBackgroundPosition.Next;
    } else if (background.position === 'LAST_PAGE') {
      return Go2pdfBackgroundPosition.Last;
    } else if (background.position === 'ALL_PAGES') {
      return Go2pdfBackgroundPosition.All;
    }
    return null;
  }

  mapAttachmentPosition(attachment: AttachmentDetail) {
    if (attachment.position === 'FIRST_POSITION') {
      return Go2PdfAttachmentPosition.Pj1;
    } else if (attachment.position === 'SECOND_POSITION') {
      return Go2PdfAttachmentPosition.Pj2;
    } else if (attachment.position === 'THIRD_POSITION') {
      return Go2PdfAttachmentPosition.Pj3;
    } else if (attachment.position === 'FOURTH_POSITION') {
      return Go2PdfAttachmentPosition.Pj4;
    } else if (attachment.position === 'FIFTH_POSITION') {
      return Go2PdfAttachmentPosition.Pj5;
    }
    return null;
  }

  ngOnDestroy(): void {
    this.internalAttachmentMap$.unsubscribe();
    this.internalBackgroundMap$.unsubscribe();
    this._subscriptions$.unsubscribe();
    this.destroy$.next(true);
    this._store?.complete();
  }

  /**
   * Get uploading file size.
   * @param size
   */
  getLimitSize(size: string): string {
    return FileUtils.getLimitSize(size);
  }

  /**
   * Add option attribute.
   */
  addAttribute(popupType: SettingOptionCriteriaType): void {
    this._store.dispatch(attachSettingOptionPopup({popupType}));
  }

  /**
   * Modify option attribute.
   * @param attributeId refers to identification of option attribute.
   * @param popupType
   */
  modifyAttribute(attributeId: number, popupType: SettingOptionCriteriaType): void {
    if (!this.isCanUpload && !this.isUsingLibrary) {
      return;
    }
    this._store.dispatch(
      fetchAddedAttachmentDetail({attributeId, popupType})
    );
  }

  modifyAttributeWatermark(attributeId: number, popupType: SettingOptionCriteriaType): void {
    if (!this.isCanUpdateWatermark) {
      return;
    }
    this._store.dispatch(fetchWatermarkEdit({fetchModeWaterMark: "edit", attributeId, popupType}));
  }

  /**
   * Delete option attribute.
   * @param attributeId refers to identification of option attribute.
   */
  deleteAttribute(attributeId: number): void {
    this._store.dispatch(deleteOptionAttribute({attributeId}));
  }

  deleteWatermark(): void {
    if (!this.isCanDeleteWaterMark) {
      return;
    }
    this._store.dispatch(deleteWatermark());
  }

  addTitle(originalName: string, paragraphElement: HTMLParagraphElement): string {
    return this.isOverflow(paragraphElement) ? originalName : '';
  }

  isOverflow(paragraphElement: HTMLParagraphElement): boolean {
    return paragraphElement.clientWidth < paragraphElement.scrollWidth;
  }

  enterParagraph(
    originalName: string,
    paragraphElement: HTMLParagraphElement
  ): void {
    paragraphElement.setAttribute(
      'title',
      this.addTitle(originalName, paragraphElement)
    );
  }

  getStyle(paragraphElement: HTMLParagraphElement): any {
    const withinScreenWidth = window.screen.width >= 1765 && window.screen.width <= 2064;
    const isShowThreeDots = paragraphElement.clientWidth < paragraphElement.scrollWidth;
    if (withinScreenWidth) {
      if (isShowThreeDots) {
        return {'width': '500px'};
      }
      return {'max-width': '500px', 'width': 'auto'};
    }
    return {};
  }
}
