/* eslint-disable @typescript-eslint/ban-ts-comment */
import '@testing-library/jest-dom';

import fetch, {Headers, Request, Response} from 'node-fetch';
import {server} from '@/mocks/server';
import {afterAll, afterEach, beforeAll} from 'vitest';

// @ts-ignore
globalThis.fetch = fetch;
// @ts-ignore
globalThis.Headers = Headers;
// @ts-ignore
globalThis.Request = Request;
// @ts-ignore
globalThis.Response = Response;

beforeAll(() => server.listen({onUnhandledRequest: 'error'}));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());
