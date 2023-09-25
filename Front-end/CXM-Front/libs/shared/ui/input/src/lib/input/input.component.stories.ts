import { CommonModule } from '@angular/common';
import {
  ControlContainer,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { Meta, Story } from '@storybook/angular/types-6-0';
import { InputComponent } from './input.component';

export default {
  title: 'Shared/ui/InputComponent',
  component: InputComponent,
} as Meta;

const Template: Story<InputComponent> = (args: InputComponent) => ({
  component: InputComponent,
  moduleMetadata: {
    imports: [CommonModule, FormsModule, ReactiveFormsModule],
    providers: [ControlContainer],
  },
  props: args,
});

export const Primary = Template.bind({});
Primary.argTypes = {
  placeHolder: {
    description: 'The main label of the button \n string',
    defaultValue: 'text',
  },
  borderRadius: {
    description: 'The radius of the button',
    defaultValue: '3px',
  },
  backgroundColor: {
    description: 'Color of the button',
    control: { type: 'color' },
  },
  color: {
    control: { type: 'color' },
    description: 'Color of the label of input',
  },
  padding: {
    control: { type: 'text' },
    description: 'Padding of the input',
  },
};

Primary.args = {
  type: 'text',
  backgroundColor: '#f0f0f5',
  color: '#000033',
  borderRadius: '3px',
  placeHolder: 'hello',
  primary: false,
  padding: '10px',
};
