import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CommunicationInteractiveEditorComponent } from './communication-interactive-editor.component';
import { RouterModule } from '@angular/router';
import { CommunicationInteractiveEditorPlaceholderComponent } from './communication-interactive-editor-placeholder/communication-interactive-editor-placeholder.component';
import { CommunicationInteractivePageComponent } from './communication-interactive-page/communication-interactive-page.component';
import { CommunicationInteractiveIframeComponent } from './communication-interactive-iframe/communication-interactive-iframe.component';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner'

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      { path: '', component: CommunicationInteractiveEditorComponent }
    ]),
    SharedUiSpinnerModule
  ],
  declarations: [
    CommunicationInteractiveEditorComponent,
    CommunicationInteractiveEditorPlaceholderComponent,
    CommunicationInteractivePageComponent,
    CommunicationInteractiveIframeComponent
  ]
})
export class CommunicationInteractiveUiFeaturedCommunicationInteractiveEditorModule {
}
