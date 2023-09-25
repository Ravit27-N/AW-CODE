import { WebsocketService } from './services/websocket.service';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [CommonModule],
  providers: [WebsocketService],
})
export class SharedWebsocketModule {}
