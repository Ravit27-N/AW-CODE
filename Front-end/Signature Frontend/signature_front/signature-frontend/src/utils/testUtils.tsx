import {render} from '@testing-library/react';
import type {RenderOptions} from '@testing-library/react';
import React, {PropsWithChildren, isValidElement} from 'react';
import {Provider} from 'react-redux';
import {setupStore} from '@/redux/index';
import type {AppStore, RootState} from '@/redux/index';
import type {PreloadedState} from '@reduxjs/toolkit';
import {Mock, vi, beforeEach} from 'vitest';
import {useTranslation} from 'react-i18next';
import {
  SnackbarKey,
  SnackbarMessage,
  useSnackbar,
  OptionsObject,
} from 'notistack';

// This type interface extends the default options for render from RTL, as well
// as allows the user to specify other things such as initialState, store. For
// future dependencies, such as wanting to test with react-router, you can extend
// this interface to accept a path and route and use those in a <MemoryRouter />
interface ExtendedRenderOptions extends Omit<RenderOptions, 'queries'> {
  preloadedState?: PreloadedState<RootState>;
  store?: AppStore;
}

function renderWithProviders(
  ui: React.ReactElement,
  {
    preloadedState = {},
    store = setupStore(preloadedState),
    ...renderOptions
  }: ExtendedRenderOptions = {},
) {
  function Wrapper({children}: PropsWithChildren<unknown>): JSX.Element {
    return <Provider store={store}>{children}</Provider>;
  }
  return {store, ...render(ui, {wrapper: Wrapper, ...renderOptions})};
}

// mock react-i18next
vi.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: vi.fn(),
}));
const tSpy = vi.fn(str => str);
const changeLanguageSpy = vi.fn((lng: string) => new Promise(() => lng));
const useTranslationSpy = useTranslation as Mock;

//mock notistack
vi.mock('notistack', async () => {
  const actual = await vi.importActual('notistack');
  return {
    ...(actual as object),
    useSnackbar: vi.fn(),
  };
});
const enqueueSnackbarSpy = vi.fn(
  (
    messageOrOptions:
      | SnackbarMessage
      | (OptionsObject & {message?: SnackbarMessage}),
    optsOrUndefined: OptionsObject = {},
  ): SnackbarKey => {
    const isOptions = (
      messageOrOptions:
        | SnackbarMessage
        | (OptionsObject & {message?: SnackbarMessage}),
    ): messageOrOptions is OptionsObject & {message?: SnackbarMessage} => {
      const isMessage =
        typeof messageOrOptions === 'string' ||
        isValidElement(messageOrOptions);
      return !isMessage;
    };

    const isDefined = (value: string | null | undefined | number): boolean =>
      !!value || value === 0;

    const {key} = isOptions(messageOrOptions)
      ? messageOrOptions
      : optsOrUndefined;

    return isDefined(key)
      ? (key as SnackbarKey)
      : new Date().getTime() + Math.random();
  },
);
const closeSnackbarSpy = vi.fn((key?: SnackbarKey) => key);
const useSnackbarSpy = useSnackbar as Mock;

// set up mock function
beforeEach(() => {
  // clean up
  vi.clearAllMocks();

  useTranslationSpy.mockReturnValue({
    t: tSpy,
    i18n: {
      changeLanguage: changeLanguageSpy,
      language: 'fr',
    },
  });

  useSnackbarSpy.mockReturnValue({
    enqueueSnackbar: enqueueSnackbarSpy,
    closeSnackbar: closeSnackbarSpy,
  });
});

export {
  renderWithProviders,
  tSpy,
  changeLanguageSpy,
  useTranslationSpy,
  useSnackbarSpy,
  enqueueSnackbarSpy,
  closeSnackbarSpy,
};
