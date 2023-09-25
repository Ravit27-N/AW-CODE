import { Meta, Story } from '@storybook/angular/types-6-0';
import { LinkButtonComponent } from './link-button.component';

export default {
  title: 'Shared/ui/LinkButtonComponent',
  component: LinkButtonComponent,
  argTypes: {
    backgroundColor: { control: 'color' },
  },
} as Meta;

const Template: Story<LinkButtonComponent> = (args: LinkButtonComponent) => ({
  component: LinkButtonComponent,
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
    control: { type: 'text' },
    defaultValue: 'link Button',
  },
  backgroundColor: {
    description: 'Color of the button',
    control: { type: 'color' },
  },
  labelColor: {
    control: { type: 'color' },
    description: 'Color of the label of button',
  },
};

// Primary.args = {
//   label: 'Button',
//   size: 'small',
//   borderRadius: '48px',
//   labelColor: '#fff'
// };
