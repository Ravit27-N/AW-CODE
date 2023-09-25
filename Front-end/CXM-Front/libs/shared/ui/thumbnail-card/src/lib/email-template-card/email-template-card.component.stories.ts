import { Meta, Story } from '@storybook/angular/types-6-0';
import { EmailTemplateCardComponent } from './email-template-card.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';

export default {
  title: 'Shared/ui/EmailTemplateCardComponent',
  component: EmailTemplateCardComponent,
} as Meta;

const Template: Story<EmailTemplateCardComponent> = (
  args: EmailTemplateCardComponent
) => ({
  component: EmailTemplateCardComponent,
  moduleMetadata: {
    imports: [MaterialModule],
  },
  props: args,
});

export const Primary = Template.bind({});

Primary.argTypes = {
  width: {
    description: 'The width of the card',
  },
  height: {
    description: 'The height of the card',
  },
  fontSize: {
    description: 'The size of the model name',
  },
  fontWeight: {
    description: 'The font weight of the model name',
  },
  title: {
    description: 'Title of of the model',
  },
  src: {
    description: 'The URL of the image of the model',
  },
  imageURL: {
    description: 'The URL of the image that access to get image from API',
  },
};

Primary.args = {
  width: '600px',
  height: '300px',
  fontSize: '21px',
  fontWeight: 'bold',
  title: 'Campaign 2021',
  src:
    'https://upload.wikimedia.org/wikipedia/commons/8/87/United_States_Antarctic_Program_website_from_2018_02_22.png',
  imageURL: '',
};
