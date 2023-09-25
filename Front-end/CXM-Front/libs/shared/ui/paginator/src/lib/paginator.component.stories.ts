import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { Meta, Story } from '@storybook/angular/types-6-0';
import { PaginatorComponent } from './paginator.component';
import { PaginatorDirective } from './paginator.directive';

export default {
  title: 'PaginatorComponent',
  component: PaginatorComponent,
} as Meta;

const Template: Story<PaginatorComponent> = (args: PaginatorComponent) => ({
  component: PaginatorComponent,
  moduleMetadata: {
    imports: [CommonModule, MaterialModule, BrowserAnimationsModule],
    declarations: [PaginatorDirective],
  },
  props: args,
});

export const Primary = Template.bind({});
Primary.argTypes = {};

Primary.args = {
  pageSize: 3,
  pageSizeOptions: [5, 10, 20],
  length: 50,
  showTotalPages: 3,
  isShowFoundItems: false,
};
