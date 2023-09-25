import { Component } from '@angular/core';
import { ChatService } from './chat.service';
import { Message } from './message';

@Component({
  selector: 'cxm-smartflow-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
})
export class ChatComponent {
  // Holding the chat messages

  messages: any[] = [];

  constructor(public chatService: ChatService) {}

  // Prepare the chat message then call the chatService method 'sendMessage' to actually send the message
  sendMessage(text: string) {
    const obj: Message = {
      message: text,
    };

    this.chatService.sendMessage(obj);
  }

}
