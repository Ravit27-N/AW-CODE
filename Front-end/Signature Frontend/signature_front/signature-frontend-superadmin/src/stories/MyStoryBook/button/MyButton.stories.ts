import type {Meta, StoryObj} from '@storybook/react';

import {NGButton} from '@components/ng-button/NGButton';

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta = {
  title: 'Example/Button/ButtonAddProject',
  component: NGButton,
  tags: ['autodocs'],
};
export default meta;
type Story = StoryObj<typeof NGButton>;
export const primary: Story = {
  args: {
    title: 'hello',
    bgColor: 'primary',
  },
};
export const Secondary: Story = {
  args: {
    title: 'hello',
    bgColor: 'secondary',
  },
};
export const Warning: Story = {
  args: {
    title: 'hello',
    bgColor: 'warning',
  },
};
export const info: Story = {
  args: {
    title: 'hello',
    bgColor: 'info',
  },
};
export const error: Story = {
  args: {
    title: 'hello',
    bgColor: 'error',
  },
};
export const small: Story = {
  args: {
    title: 'hello',
    bgColor: 'primary',
    size: 'small',
  },
};
export const medium: Story = {
  args: {
    title: 'hello',
    bgColor: 'primary',
    size: 'medium',
  },
};
export const large: Story = {
  args: {
    title: 'hello',
    bgColor: 'primary',
    size: 'large',
  },
};
export const contained: Story = {
  args: {
    title: 'hello',
    bgColor: 'primary',
    variant: 'contained',
    color: ['red'],
  },
};
export const text: Story = {
  args: {
    title: 'hello',
    bgColor: 'primary',
    variant: 'text',
    color: ['blue'],
  },
};
export const outlined: Story = {
  args: {
    title: 'hello',
    bgColor: 'primary',
    variant: 'outlined',
    color: ['blue'],
  },
};
