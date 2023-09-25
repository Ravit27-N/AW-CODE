import { addDecorator, configure } from '@storybook/angular';
import { withKnobs } from '@storybook/addon-knobs';

addDecorator(withKnobs);
configure(require.context('../..', true, /\.stories\.tsx?$/), module);
