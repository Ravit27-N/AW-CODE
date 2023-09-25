import { OverlayModule } from '@angular/cdk/overlay';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Meta, Story } from '@storybook/angular/types-6-0';
import { RoundActionButtonComponent } from './round-action-button.component';

const story: Meta = {
  title: 'Shared/ui/RoundActionButtonComponent',
  component: RoundActionButtonComponent,

}

const temlate: Story<RoundActionButtonComponent> = (args: RoundActionButtonComponent) => ({
  component: RoundActionButtonComponent,
  template: `
    <cxm-smartflow-round-action-button hint='Click to open folder'>folder</cxm-smartflow-round-action-button>
  `,
  moduleMetadata: {
    imports: [MatIconModule, MatButtonModule, OverlayModule]
  },
  props: args
});

export const Normal = temlate.bind({ });
Normal.argTypes = {
  hint: {
    description: 'text tooltip to show when mouse over',
    control: { type: 'text' },
  },
  disabled: {
    description: 'disable the button',
    control: { type: 'boolean' }
  }
}

Normal.args = {
  hint: 'Click to open folder',
}



const temlateDisabled: Story<RoundActionButtonComponent> = (args: RoundActionButtonComponent) => ({
  component: RoundActionButtonComponent,
  template: `
    <cxm-smartflow-round-action-button [disabled]='true' hint='Click to open folder'>folder</cxm-smartflow-round-action-button>
  `,
  moduleMetadata: {
    imports: [MatIconModule, MatButtonModule, OverlayModule]
  },
  props: args
});

export const Disabled = temlateDisabled.bind({ });


export default story;
