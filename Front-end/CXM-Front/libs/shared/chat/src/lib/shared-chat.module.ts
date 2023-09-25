import { MaterialModule } from '@cxm-smartflow/shared/material';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ChatComponent } from './chat/chat.component';
import { ChatService } from './chat/chat.service';

@NgModule({
  imports: [CommonModule, MaterialModule],
  declarations: [ChatComponent],
  exports: [ChatComponent],
  providers: [ChatService],
})
export class SharedChatModule {}
