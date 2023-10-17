import {Provider} from 'react-redux';
import ReactDOM from 'react-dom/client';
import {RouterProvider} from 'react-router-dom';
import {router} from './router';
import './global.css';
import './i18n/i18n';
import {store} from './redux';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <Provider store={store}>
    <RouterProvider router={router} />
  </Provider>,
);
