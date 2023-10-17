import {act, fireEvent, screen, waitFor} from '@testing-library/react';
import {RouterProvider, createMemoryRouter} from 'react-router-dom';
import {renderWithProviders} from '@/utils/testUtils';
import {describe, expect, it} from 'vitest';
import {Localization} from '@/i18n/lan';
import {server} from '@/mocks/server';
import {Route} from '@/constant/Route';
import {routesConfig} from '@/router';
import {errorHandlers} from '@/mocks/handlers';
import userEvent from '@testing-library/user-event';

describe('<Login />', () => {
  const router = createMemoryRouter(routesConfig, {
    initialEntries: [Route.LOGIN],
  });

  it('Login Successfully', async () => {
    await act(async () =>
      renderWithProviders(<RouterProvider router={router} />),
    );
    expect(
      screen.getByText(Localization('title', 'welcome to signature')),
    ).toBeInTheDocument();
    const emailInput = screen.getByPlaceholderText(
      Localization('form', 'email-placeholder'),
    );
    const passwordInput = screen.getByPlaceholderText(
      Localization('form', 'password-placeholder'),
    );
    const submitButton = screen.getByRole('button', {
      name: Localization('form', 'login'),
    });
    fireEvent.change(emailInput, {
      target: {value: 'dummy@email.com'},
    });
    fireEvent.change(passwordInput, {target: {value: '123'}});
    fireEvent.click(screen.getByRole('checkbox'));
    expect((emailInput as HTMLInputElement).value).toBe('dummy@email.com');
    expect((passwordInput as HTMLInputElement).value).toBe('123');
    expect(submitButton).not.toBeDisabled();
    // await fireEvent.click(submitButton);

    // since it can redirect to different page after login depend on role should write this test
    // as many time as the page is can redirect to
  });

  // Test when login error show error message
  it('Login Error', async () => {
    const user = userEvent.setup();

    server.use(...errorHandlers);
    await act(async () => {
      renderWithProviders(<RouterProvider router={router} />);
    });

    // complete login form
    await act(async () => {
      await user.type(
        screen.getByPlaceholderText(Localization('form', 'email-placeholder')),
        'dummy@email.com',
      );
      await user.type(
        screen.getByPlaceholderText(
          Localization('form', 'password-placeholder'),
        ),
        '123',
      );
      await user.click(screen.getByRole('checkbox'));
    });

    // check button should be enable and than click it
    expect(
      screen.getByRole('button', {
        name: Localization('form', 'login'),
      }),
    ).not.toBeDisabled();
    await act(async () => {
      await user.click(
        screen.getByRole('button', {
          name: Localization('form', 'login'),
        }),
      );
    });

    // show error message
    await waitFor(() => {
      expect(
        screen.getByLabelText('login-page-error-message-popup'),
      ).toBeInTheDocument();
    });
  });

  // Test to check if it redirect to forget-password after click forget-password link
  it('Redirect to forget-password', async () => {
    renderWithProviders(<RouterProvider router={router} />);

    // find link
    const forgetPassword = screen.getByText(
      Localization('form', 'forget-password'),
    );
    // click link
    await fireEvent.click(forgetPassword);

    // check element if it the same from forget-password page
    await waitFor(() => {
      expect(
        screen.getByText(Localization('form', 'Back to the connection')),
      ).toBeInTheDocument();
    });
    expect(
      screen.getByText(Localization('form', 'forget-password')),
    ).toBeInTheDocument();
    expect(
      screen.getByText(Localization('form', 'text-bottom-forgot-password')),
    ).toBeInTheDocument();
  });
});
