import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileSaverUtil } from './file-saver.service';

@NgModule({
  imports: [CommonModule],
  exports: [],
  providers: [FileSaverUtil]
})
export class SharedUtilsModule {}
