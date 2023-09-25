import { OverlayModule } from '@angular/cdk/overlay';
import { Component, Input } from '@angular/core';
import { Meta, Story } from '@storybook/angular/types-6-0';
import { SharedDirectivesTooltipModule } from './shared-directives-tooltip.module';


@Component({
  selector: 'cxm-smartflow-dummy',
  template: `
    <div cxmSmartflowTooltip hint='Tooltip message component' >The component</div>
  `
})
class DummyComponent {

  @Input() mode: string;
}

const story: Meta = {
  title: 'Shared/directive/tooltip',
  component: DummyComponent
}

const template: Story<DummyComponent> = (args: DummyComponent) => ({
  props: args,
  template: `
    <button class='px-2 py-1 m-4 border' cxmSmartflowTooltip mode="success" hint='This is tooltip message'>Button</button>
    <button class='px-2 py-1 border' cxmSmartflowTooltip mode="error" hint='This is tooltip message'>Button</button>
  `,
  moduleMetadata: {
    imports: [SharedDirectivesTooltipModule, OverlayModule]
  }
})


export const Default = template.bind({});
Default.argTypes = {
  mode: {
    description: 'Type of hint',
    name: 'mode'
  }
}

export default story;
