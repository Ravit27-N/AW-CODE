import { Meta, Story } from '@storybook/angular/types-6-0';
import { ButtonComponent } from './button.component';

export default {
  title: 'Shared/ui/ButtonComponent',
  component: ButtonComponent,
  argTypes: {
    backgroundColor: { control: 'color' },
  },
} as Meta;

const Template: Story<ButtonComponent> = (args: ButtonComponent) => ({
  component: ButtonComponent,
  props: args,
});

export const Primary = Template.bind({});
Primary.argTypes = {
  size: {
    options: ['small', 'medium', 'large', 'medium'],
    control: { type: 'select' },
    description: 'Size of the button',
  },
  label: {
    description: 'The main label of the button \n string',
    defaultValue: 'Button',
  },
  borderRadius: {
    description: 'The radius of the button',
    defaultValue: '48px',
  },
  primary: {
    description: 'Primary style of the button',
    defaultValue: true,
  },
  backgroundColor: {
    description: 'Color of the button',
  },
  labelColor: {
    control: { type: 'color' },
    description: 'Color of the label of button',
  },
};

Primary.args = {
  primary: true,
  label: 'Button',
  size: 'small',
  borderRadius: '48px',
  labelColor: '#fff'
};
