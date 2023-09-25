import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeaturedFlowDocumentDetailComponent } from './featured-flow-document-detail.component';
import { RouterModule } from '@angular/router';
import { FlowTraceabilityUiFlowTraceabilityPageHeaderModule } from '@cxm-smartflow/flow-traceability/ui/featured-flow-page-header';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { RecipientComponent } from './recipient/recipient.component';
import { DocumentDetailComponent } from './document-detail/document-detail.component';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { MatIconModule } from '@angular/material/icon';
import { FlowTraceabilityUiFlowEventHistoryModule } from '@cxm-smartflow/flow-traceability/ui/featured-flow-event-history';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { FlowDocumentNavigatorComponent } from './flow-document-navigator/flow-document-navigator.component';
import {MatExpansionModule} from "@angular/material/expansion";

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild([
            {path: '', component: FeaturedFlowDocumentDetailComponent},
        ]),
        FlowTraceabilityUiFlowTraceabilityPageHeaderModule,
        FlowTraceabilityUiFlowEventHistoryModule,
        SharedPipesModule,
        SharedUiButtonModule,
        SharedTranslateModule.forRoot(),
        MatIconModule,
        MatExpansionModule,
    ],
  declarations: [
    FeaturedFlowDocumentDetailComponent,
    RecipientComponent,
    DocumentDetailComponent,
    FlowDocumentNavigatorComponent,
  ],
  exports: [FeaturedFlowDocumentDetailComponent],
})
export class FlowTraceabilityUiFeaturedFlowDocumentDetailModule {}
