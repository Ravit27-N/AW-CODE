import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { createAction, createFeatureSelector, createReducer, createSelector, on, props, Store } from '@ngrx/store';
import { of } from 'rxjs';
import { filter, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import * as fromCampaignAction from './campaign.actions';
import * as fromCampaignListAction from './../feature-campaign-list/feature-campaign-list.action';
import { Router } from '@angular/router';
import * as fromEmailCampaignSelector from './campaign.selectors';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';
import { selectSmsState } from '../feature-campaign-sms';


export const FeatureCampaignEmailStepKey = 'Feature-campaign-step-email-key'

const intialState = {
  show: false,
  steps: [
    { link: appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel, name: 'cxmCampaign.followMyCampaign.stepper.model', completed: true, active: true, step: 1 },
    { link: appRoute.cxmCampaign.followMyCampaign.emailCampaignDestination, name: 'cxmCampaign.followMyCampaign.stepper.destination',completed: false, active: false, step: 2 },
    { link: appRoute.cxmCampaign.followMyCampaign.emailCampaignParameter, name: 'cxmCampaign.followMyCampaign.stepper.parameter', completed: false, active: false, step: 3 },
    { link: appRoute.cxmCampaign.followMyCampaign.emailCampaignSummary, name: 'cxmCampaign.followMyCampaign.stepper.sending', completed: false, active: false, step: 4 },
  ],
  type: 'EMAIL'
}


const stepForSMS = [
  { link: appRoute.cxmCampaign.followMyCampaign.smsCampaignList, name: 'cxmCampaign.followMyCampaign.stepper.model', completed: true, active: true, step: 1 },
  { link: appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination, name: 'cxmCampaign.followMyCampaign.stepper.destination',completed: false, active: false, step: 2 },
  { link: appRoute.cxmCampaign.followMyCampaign.smsCampaignParameter, name: 'cxmCampaign.followMyCampaign.stepper.parameter', completed: false, active: false, step: 3 },
  { link: appRoute.cxmCampaign.followMyCampaign.smsCampaignEnvoy, name: 'cxmCampaign.followMyCampaign.stepper.sending', completed: false, active: false, step: 4 },
];

const remember = {
  step: 0
}


export const StepOnCampaign = createAction('[step email campaign / step on]', props<{ step: number }>());
export const StepOnCampaignComplete = createAction('[step email campaign / step on completed]');
export const StepOnReset = createAction('[step email campaign / reset]');
export const StepOnPreloadCampaign = createAction('[step email campaign / step on preload]');
export const navigateToStep = createAction('[step email campaign] / step on nav', props<{ step: any }>());
export const StepOnActivated = createAction('[step email campaign / activate]', props<{ active: boolean, step?: number, modify?: boolean, leave?: boolean, specification?: { stepFor: 'SMS' | 'EMAIL' }}>());

export const navigateSmsStep = createAction('[step sms campaing / step on sms nav]',props<{ step: any }>());
export const navigateEmailStep = createAction('[step email campaing / step on email nav]', props<{ step: any }>());

export const campaignEmailSteRedcuer = createReducer(
  intialState,
  on(StepOnCampaign, (state, props) => {
    let { steps } = state;
    steps = Array.from(steps).map((item, index) => index === Math.max(0, props.step - 1) ? { ...item, active: true } : { ...item, active: false })

    return { ...state, steps };
  }),
  on(StepOnCampaignComplete, (state, props) => {
    let { steps } = state;
    steps = Array.from(steps).map((item, index) => item.active ? { ...item, completed: false } : item);
    return { ...state, steps };
  }),
  on(StepOnReset, (state) => ({ ...intialState })),
  on(StepOnPreloadCampaign, (state) => {
    let { steps } = state;
    steps = Array.from(steps).map((item, index) => {
      return { ...item, active: index === 2 }; // parameter step
     });

    return { ...state, steps }
  }),
  on(StepOnActivated, (state, props) => {
    if (props.leave) {
      remember.step = 0;
    }
    if (remember.step <= (props.step || 0) || props.modify) {
      remember.step = props.step || 0;
    }
    const isSmsCampaign = props.specification && props.specification.stepFor === 'SMS';
    const chooseCampaign = isSmsCampaign ? stepForSMS : intialState;
    const campaign = JSON.parse(JSON.stringify(chooseCampaign));
    const selectCampaign = isSmsCampaign ? campaign : campaign.steps;
    selectCampaign.filter((result: any) => result.step <= remember.step).map((result: any) => {
      result.completed = true;
      return result;
    })
    if(props.specification) {
      if(props.specification.stepFor === 'SMS') {
        return { ...state, show: props.active, steps: selectCampaign, type: 'SMS' }
      }
    }
    return { ...state, show: props.active, steps: selectCampaign, type: 'EMAIL' }
  })
);


const selectFeatureCampaignEmailStep = createFeatureSelector(FeatureCampaignEmailStepKey);
export const selectCampaignEmailStep = createSelector(selectFeatureCampaignEmailStep, (state: any) => state?.steps);
export const showStepSeq = createSelector(selectFeatureCampaignEmailStep, (state: any) => state?.show);
const forStepType = createSelector(selectFeatureCampaignEmailStep, (state: any) => state.type);

@Injectable()
export class CampaignStepEffect
{

  submitDestinationOnStepEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromCampaignAction.submitDestinationSuccess, fromCampaignAction.submitEmailCampaignParameterSuccess),
    switchMap(args => of(StepOnCampaignComplete()))
  ))

  unloadFormEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromCampaignAction.submitEmailCampaignSummaryStepSuccess, fromCampaignListAction.loadFeatureCampaignList,fromCampaignAction.unloadEmailCampaignFormData),
    switchMap(args => of(StepOnReset()))
  ))

  peloadCampaignFormEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromCampaignAction.preloadEmailCampaign),
    switchMap(args => of(StepOnPreloadCampaign()))
  ))

  navigateStepEffect$ = createEffect(() => this.actions$.pipe(
    ofType(navigateToStep),
    withLatestFrom(
      this.store.select(forStepType),
    ),
    switchMap(([args, type]) => {
      if(type === 'SMS') {
        return of(navigateSmsStep({ step: args.step }))
      } else {
        return of(navigateEmailStep({ step: args.step }))
      }
    })
  ), { dispatch: true } )


    navigateStepSmsEffect$ = createEffect(() => this.actions$.pipe(
      ofType(navigateSmsStep),
      withLatestFrom(
        this.store.select(selectCampaignEmailStep),
        this.store.select(selectSmsState),
      ),
      tap(([args, steps, smsState]) => {
        const { step } = args;
        const requestIndex = Array.from(steps).findIndex((x: any) => x.name == step.name);
        const { templateId, campaign  } = smsState;

        if(requestIndex === 0) // TODO: show popup warning of losing process
        {
          //
        } else if(requestIndex === 1 && templateId) {
          this.router.navigate([step.link], { queryParams: { templateId: templateId, id: campaign.id } });
        } else if(requestIndex === 2 && campaign) {
          this.router.navigateByUrl(step.link + `/` + campaign.id)
        } else if(requestIndex === 3 && campaign) {
          this.router.navigate([step.link]);
        }
      })
    ), { dispatch: false });

    navigateStemEmailEffect$ = createEffect(() => this.actions$.pipe(
      ofType(navigateEmailStep),
      withLatestFrom(
      this.store.select(selectCampaignEmailStep),
      this.store.select(fromEmailCampaignSelector.selectTemplateDetails),
      this.store.select(fromEmailCampaignSelector.selectCampaignDetail)
      ),
      tap(([args, steps, template, campaign]) => {
        const { step } = args;
        const requestIndex = Array.from(steps).findIndex((x: any) => x.name == step.name);

        if(requestIndex === 0) // TODO: show popup warning of losing process
        {
          //
        } else if(requestIndex === 1) {
          this.router.navigate([step.link], { queryParams: { templateId: template.id, id: campaign.id } });
        } else if(requestIndex === 2) {
          this.router.navigateByUrl(step.link + `/` + campaign.id)
        } else if(requestIndex === 3) {
          this.router.navigate([step.link]);
        }
      })
    ), { dispatch: false })

  constructor(private actions$: Actions, private store: Store, private router: Router) { }

}
