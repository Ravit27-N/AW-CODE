import { Injectable } from '@angular/core';
import { WebsocketService } from '@cxm-smartflow/shared/websocket';
import { Message } from './message';
/**
 * Declaring SockJS and Stomp : check the assets/js folder and the index.html script section
 */

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  // Store the chat messages
  public messages: any[] = [];

  public stompClient: any;

  constructor(private socket: WebsocketService) {
    this.initializeWebSocketConnection();
  }

  // {"X-Authorization": "Bearer " + accessToken.accessToken}

  initializeWebSocketConnection() {
    this.stompClient = this.socket.connect();
    // eslint-disable-next-line @typescript-eslint/no-this-alias
    const that = this;
    /**
     * Connect stomp client and subscribe asynchronously to the chat message-handling Controller endpoint and push any message body into the messages array
     */
    this.stompClient.connect({}, (frame: any) => {
      that.stompClient.subscribe('/topic/outgoing', (message: any) => {
        if (message.body) {
          // const obj = JSON.parse(message.body);
          that.addMessage(message.body);
        }
      });
    });
  }

  // Prepare and push the chat messages into the messages array
  addMessage(message: any) {
    this.messages.push({
      message: message,
    });
  }

  // Send a chat message using stomp client
  sendMessage(msg: Message) {
    this.stompClient.send('/app/incoming', {}, JSON.stringify(msg));
  }

}
