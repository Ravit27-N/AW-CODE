import type {Meta, StoryObj} from '@storybook/react';
import NGInput from '@components/ng-inputField/NGInput';

const meta: Meta<typeof NGInput> = {
  title: 'Example/INPUT',
  component: NGInput,
  // This component will have an automatically generated Autodocs entry: https://storybook.js.org/docs/7.0/react/writing-docs/docs-page
  tags: ['autodocs'],
  parameters: {
    // More on Story layout: https://storybook.js.org/docs/react/configure/story-layout
    layout: 'fullscreen',
  },
};
export default meta;
type Story = StoryObj<typeof NGInput>;
export const LoggedIn: Story = {
  args: {
    type: 'text',
    value: 'sila@gmail.com',
    nameId: 'email',
    placeholder: 'email',
    textLabel: 'EMAIL',
    setValue: 'sisdslf',
  },
};

export const LoggedOut: Story = {};
