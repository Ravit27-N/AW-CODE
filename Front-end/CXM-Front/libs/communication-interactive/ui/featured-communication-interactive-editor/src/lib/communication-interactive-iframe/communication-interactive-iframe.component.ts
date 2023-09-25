import { Component, ElementRef, EventEmitter, HostListener, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'cxm-smartflow-communication-interactive-iframe',
  templateUrl: './communication-interactive-iframe.component.html',
  styleUrls: ['./communication-interactive-iframe.component.scss']
})
export class CommunicationInteractiveIframeComponent implements OnChanges {


  @Input() ticketUrl: SafeResourceUrl | null;

  @Output() onclosed = new EventEmitter();

  @Output() onloaded = new EventEmitter();

  @ViewChild("frame") frame: ElementRef;

  isContentLoaded = false;


  private handleOnMessage(message: MessageEvent<any>) {


    if (message.data && typeof message.data === 'string') {
      try {
        const json = JSON.parse(message.data);

        const icframe = document.getElementById('iframe') as HTMLIFrameElement;
        const samplePayload = { source: 'InteractivePlusFrame', type: '' }


        if (json.source === 'InteractivePlusEditor') {

          if (json.type === 'initialized') {
            // Send register handshake
            const payload = { ...samplePayload, type: 'register'  };
            icframe.contentWindow?.postMessage(JSON.stringify(payload), "*");

          }

          if(json.type === 'closed') {
            this.onclosed.emit();
          }
        }
      } catch(e) {
        console.error({e });
      }
    }
  }


  private setupIFrameCommunication() {
    if (!window) {
      throw new Error("Not support");
    } else {
      window.addEventListener("message", this.handleOnMessage.bind(this), false);
    }
  }

  handleFrameLoaded() {
    this.isContentLoaded = true;
    this.onloaded.emit();
  }


  @HostListener('window:message', ['$event']) handleOnMessageEvent(message: MessageEvent<any>) {
    this.handleOnMessage(message);
  }


  ngOnChanges(changes: SimpleChanges): void {
  if(changes.ticketUrl.firstChange) { return; }

    if(changes.ticketUrl.currentValue) {
      this.isContentLoaded = false;
    }
  }
}
