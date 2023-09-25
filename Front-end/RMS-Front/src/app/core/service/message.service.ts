import { ToastrService } from 'ngx-toastr';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(
    private toastr: ToastrService
  ) { }

  showSuccess(content?: string, title?: string, timeOut = 2000): void{
    this.toastr.success(content, title, {
      progressAnimation: 'increasing',
      progressBar: true,
      closeButton: true,
      enableHtml: true,
      timeOut,
      newestOnTop: true,
    });
  }

  showError(content?: string, title?: string, timeOut = 2000): void{
    this.toastr.error(content, title, {
      progressAnimation: 'increasing',
      progressBar: true,
      closeButton: true,
      enableHtml: true,
      timeOut,
      newestOnTop: true,
    });
  }

  showWarning(content?: string, title?: string, timeOut = 2000): void{
    this.toastr.warning(content, title, {
      progressAnimation: 'increasing',
      progressBar: true,
      closeButton: true,
      enableHtml: true,
      timeOut,
      newestOnTop: true,
    });
  }

}
