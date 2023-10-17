import {rest} from 'msw';
import {endUserMockData, loginMockData} from '@/mocks/data';
import env from '../../env.config';
import {baseQuery} from '@/redux/slides/RTKBaseQuery';
import {UNKOWNERROR} from '@/constant/NGContant';
import {ApiFacade, formatKeycloakAPI} from '@/utils/common/ApiFacade';

// fake api call. when ever the api is call the mockData return instead

const handlers = [
  rest.post(
    `${formatKeycloakAPI}/realms/${env.VITE_REALM}/protocol/openid-connect/token`,
    (req, res, ctx) => {
      return res(ctx.json(loginMockData));
    },
  ),
  rest.get(
    `${ApiFacade(baseQuery.profileManagement)}/users/end-user`,
    (req, res, ctx) => {
      return res(ctx.json(endUserMockData));
    },
  ),
];

const errorHandlers = [
  rest.post(
    `${formatKeycloakAPI}/realms/${env.VITE_REALM}/protocol/openid-connect/token`,
    (req, res, ctx) => {
      return res.once(ctx.status(500), ctx.json({message: UNKOWNERROR}));
    },
  ),
  rest.get(
    `${ApiFacade(baseQuery.profileManagement)}/users/end-user`,
    (req, res, ctx) => {
      return res.once(ctx.status(500), ctx.json({message: UNKOWNERROR}));
    },
  ),
];

export {handlers, errorHandlers};
