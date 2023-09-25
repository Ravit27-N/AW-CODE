import {EnvelopeReferenceEffect} from "./stores/envelope-reference.effect";
import {StoreDevtoolsModule} from "@ngrx/store-devtools";
import {StoreModule} from "@ngrx/store";
import {EffectsModule} from "@ngrx/effects";
import {CommonModule} from "@angular/common";
import {envelopeReferenceReducer, FEATURE_ENVELOPE_REFERENCE_KEY} from "./stores/envelope-reference.reducer";
import {SharedDataAccessServicesModule} from "@cxm-smartflow/shared/data-access/services";
import {NgModule} from "@angular/core";
import {EnvelopeReferenceService} from "./services";

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(FEATURE_ENVELOPE_REFERENCE_KEY, envelopeReferenceReducer),
    EffectsModule.forFeature([EnvelopeReferenceEffect]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
  ],
  providers:[EnvelopeReferenceService]
})
export class EnvelopeReferenceDataAccessModule {}
