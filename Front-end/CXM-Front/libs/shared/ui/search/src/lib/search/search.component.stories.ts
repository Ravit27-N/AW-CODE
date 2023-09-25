import { Story } from '@storybook/angular/types-6-0';
import { SearchComponent } from './search.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { CommonModule } from '@angular/common';
import { moduleMetadata } from '@storybook/angular';
export default {
  title: 'Shared/ui/SearchComponent',
  component: SearchComponent,
  decorators: [
    moduleMetadata({
      //👇 Imports both components to allow component composition with Storybook
      declarations: [SearchComponent],
      imports: [CommonModule, MaterialModule],
    })]
}


const Template: Story<SearchComponent> = (args: SearchComponent) => ({
  component: SearchComponent,
  props: args,
});

export const Primary = Template.bind({});
Primary.argTypes = {
  placeHolder: {
    description: 'The main label of the button \n string',
    defaultValue: 'Button',
  },
  borderRadius: {
    description: 'The radius of the button',
    defaultValue: '4px',
  },
  disable: {
    description: 'Enable or Disable input field',
    defaultValue: true,
  },
  backgroundColor: {
    description: 'Color of the button',
    control: {type: 'color'}
  },
  color: {
    control: {type: 'color'},
    description: 'Color of the label of button',
  },
  inputValue: {
    defaultValue: 'search me',
    description: 'for input value '
  }
};

Primary.args = {
  type: 'text',
  backgroundColor: '#f0f0f5',
  color: '#000033',
  borderRadius: '3px',
  disable: false,
  placeHolder: 'Search button work ',
  inputValue: 'search'
};
