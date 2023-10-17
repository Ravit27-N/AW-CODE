import {Route} from '@/constant/Route';
import FileCorporate from '@/pages/corporate-admin/sidebar-corporate/Models';
import SetPersonalPassword from '@/pages/form/change-password/SetPersonalPassword';
import Login from '@/pages/form/login/Login';
import SignDocument from '@/pages/participant/sign/SignDocument';
import {Navigate as Navigator} from '@/utils/common';
import NotFound404 from '@pages/NotFound404';
import DashboardCorporate from '@pages/corporate-admin/Home.corporate';
import ContactCorporate from '@pages/corporate-admin/sidebar-corporate/Contact';

import QuestionCorporate from '@pages/corporate-admin/sidebar-corporate/Question';
import CompanyPage from '@pages/corporate-admin/sidebar-corporate/company-page/Enterprise';
import Brand from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/Brand';
import Services from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/Services';
import SettingsCorporate from '@pages/corporate-admin/sidebar-corporate/company-page/sidebar-company-page/setting/Settings';
import GroupCorporate from '@pages/corporate-admin/sidebar-corporate/customer/Customers';
import {Folder, MyProfile, Question, Setting} from '@pages/end-user/sidebar';
import {
  ForgotPassword,
  PasswordModify,
  ResetPassword,
} from '@pages/form/forgot-password';
import {CreateCorporator, Enduser} from '@pages/index';
import InvitationPages from '@pages/participant/invitation/Invitation';
import {CorporateRoute, EndUserRoute, SuperRoute} from '@pages/protect-route';
import {Navigate, createBrowserRouter} from 'react-router-dom';
import App from './App';
import ApproveDocument from './pages/participant/approve/ApproveDocument';
import ViewFileToApprove from './pages/participant/approve/ViewFileToApprove';
import ExpiredProject from './pages/participant/invitation/ExpiredProject';
import ApproveInvitation from './pages/participant/invitation/approve/ApproveInvitation';

import CorporateSignatureProjects from '@pages/corporate-admin/sidebar-corporate/signature-projects';
import CorporateSignatureProjectDetail from '@pages/corporate-admin/sidebar-corporate/signature-projects/signature-project-detail';
import ProfileEndUser from '@pages/end-user/profile';
import EmptyProjectDetail from '@pages/end-user/project-detail/empty/EmptyProjectDetail';
import ModelsCorporate from '@pages/end-user/sidebar/Models';
import IdentityPage from '@pages/participant/advance-signature/identity/IdentityPage';
import NationalCard from '@pages/participant/advance-signature/identity/option-identity/NationalCard';
import Passport from '@pages/participant/advance-signature/identity/option-identity/Passport';
import StayBook from '@pages/participant/advance-signature/identity/option-identity/StayBook';
import ValidateIdentity from '@pages/participant/advance-signature/identity/option-identity/validate-identity/ValidateIdentity';
import ExpiredAdvanceProject from '@pages/participant/advance-signature/invitation/ExpiredProject';
import AdvanceInvitationPages from '@pages/participant/advance-signature/invitation/Invitation';
import ViewFileToSignAdvance from '@pages/participant/sign/ViewFileToSignAdavance';
import AuthActivate from '@pages/protect-route/AuthActivate';
import UnAuthorize from '@pages/protect-route/provider/UnAuthorize';
import ContentSuperAdmin from '@pages/super-admin/layout/Layout';
import CompanyPageSuper from '@pages/super-admin/sidebar-super-admin/company/EntrepriseSuper';
import SettingSuper from '@pages/super-admin/sidebar-super-admin/company/setting/SettingSuper';
import {SignatureProjectDetail} from './pages/end-user/project-detail';
import AssignedProjectEndUser from './pages/end-user/sidebar/AssignProject';
import RecipientInvitation from './pages/participant/invitation/recipient/RecipientInvitation';
import ViewerInvitation from './pages/participant/invitation/viewer/ViewerInvitation';
import RecipientDocument from './pages/participant/receipt/RecipientDocument';
import ViewFileToDownload from './pages/participant/receipt/ViewFileToDownload';
import RefuseDocument from './pages/participant/sign/RefuseDocument';
import ViewFileToSign from './pages/participant/sign/ViewFileToSign';
import PDFTronView from './pages/participant/signatory/PDFTronView';
import ViewFileDocument from './pages/participant/viewer/ViewFileDocument';
import HomeSuper from './pages/super-admin/temp/Home.super';

export const routesConfig = [
  /** Page wait to Auth activated */
  {
    path: `${Route.profile.waitAuthActivate}/:uuid`,
    element: <AuthActivate />,
    // element: <WaitActivateAccount />,
  },
  /** Auth activated */
  // {
  //   path: `${Route.profile.authActivate}/:uuid`,
  //   element: <AuthActivate />,
  // },

  {
    element: <UnAuthorize />,
    children: [
      {
        path: `${Route.participant.expiredProject}/:id`,
        element: <ExpiredProject />,
      },
      /** Viewer routes */
      {
        path: `${Route.participant.viewerInvite}/:id`,
        element: <ViewerInvitation />,
      },
      {
        path: `${Route.participant.viewDocument}/:id`,
        element: <ViewFileDocument />,
      },

      /** Recipient routes */
      {
        path: `${Route.participant.recipientInvite}/:id`,
        element: <RecipientInvitation />,
      },
      {
        path: `${Route.participant.receiptDocument}/:id`,
        element: <RecipientDocument />,
      },
      {
        path: `${Route.participant.viewRecipientFile}/:id`, // flowId
        element: <ViewFileToDownload />,
      },

      /** Approve routes */
      {
        path: `${Route.participant.approveInvite}/:id`,
        element: <ApproveInvitation />,
      },
      {
        path: `${Route.participant.viewApproveFile}/:id`,
        element: <ViewFileToApprove />,
      },
      {
        path: `${Route.participant.approveDocument}/:id`,
        element: <ApproveDocument />,
      },

      /** Sign routes */
      {
        path: `${Route.participant.viewSignatoryFile}/:id`, // flowId
        element: <ViewFileToSign />,
      },
      {
        path: `${Route.participant.signDocument}/:id`,
        element: <SignDocument />,
      },
      {
        path: `${Route.participant.refuseDocument}/:id`,
        element: <RefuseDocument />,
      },

      /**Advance Signature routes */
      {
        path: `${Route.participant.advance.root}/:id`,
        element: <AdvanceInvitationPages />,
      },
      /**Advance Signature expired page */
      {
        path: `${Route.participant.advance.expiredProject}/:id`,
        element: <ExpiredAdvanceProject />,
      },
      {
        path: `${Route.participant.advance.viewFileToSign}/:id`,
        element: <ViewFileToSignAdvance />,
      },
      /**Advance Signature Identity page */
      {
        path: `${Route.participant.advance.identity}/:id`,
        element: <IdentityPage />,
      },
      /**Advance Signature Identity card national page */
      {
        path: `${Route.participant.advance.identityDoc.cardNational}/:id`,
        element: <NationalCard />,
      },
      {
        path: `${Route.participant.advance.identityDoc.cardNationalElement.cardNationalValidatePage}/:id`,
        element: <ValidateIdentity />,
      },

      /**Advance Signature Identity passport page */
      {
        path: `${Route.participant.advance.identityDoc.passport}/:id`,
        element: <Passport />,
      },
      /**Advance Signature Identity stay book page */
      {
        path: `${Route.participant.advance.identityDoc.stayBook}/:id`,
        element: <StayBook />,
      },

      /** Forgot password */
      {
        path: `${Route.RESET_PASSWORD}/:token`,
        element: <ResetPassword />,
      },
      {
        path: Route.FORGOT_PASSWORD,
        element: <ForgotPassword />,
      },
      {
        path: Route.PASSWORD_MODIFY,
        element: <PasswordModify />,
      },

      /** Sign routes */
      {
        path: `${Route.participant.root}/:id`,
        element: <InvitationPages />,
      },
      {
        path: `${Route.participant.viewFile}/:id`, // flowId
        element: <PDFTronView />,
      },
    ],
  },
  {
    path: Route.ROOT,
    element: <App />,
    errorElement: <NotFound404 />,
    children: [
      {
        index: true,
        path: Route.LOGIN,
        element: <Login />,
      },
      {
        path: Route.REGISTER_CORPORATE,
        element: <CreateCorporator />,
      },
      /** End user-Route */
      {
        path: '/',
        element: <EndUserRoute />,
        children: [
          {
            index: true,
            element: (
              <Navigate
                to={`${Navigator(Route.HOME_ENDUSER).pathname}`}
                replace
              />
            ),
          },
          {
            path: Route.HOME_ENDUSER,
            element: <Enduser />,
          },
          {
            path: Route.HOME_ENDUSER_WITH_PROJECT_ID,
            element: <Enduser />,
          },
          {
            path: `${Route.endUser.viewProfile}`,
            element: <ProfileEndUser />,
          },
          {
            path: `${Route.project.projectDetail}/:id`,
            element: <SignatureProjectDetail />,
          },
          {
            path: `${Route.project.projectDetail}`,
            element: <EmptyProjectDetail />,
          },
          {
            path: Route.QUESTION,
            element: <Question />,
          },
          {
            path: Route.SETTING,
            element: <Setting />,
          },
          {
            path: Route.endUser.assignedProject,
            element: <AssignedProjectEndUser />,
          },
          {
            path: Route.MODEL,
            element: <ModelsCorporate />,
          },
          {
            path: Route.MODEL_PROJECT_WITH_ID,
            element: <ModelsCorporate />,
          },
          {
            path: Route.MODEL_WITH_ID,
            element: <ModelsCorporate />,
          },
          {
            path: Route.FOLDER,
            element: <Folder />,
          },
          {
            path: Route.MY_PROFILE,
            element: <MyProfile />,
          },
        ],
      },
      /** SuperAdmin-Route */
      {
        path: '/',
        element: <SuperRoute />,
        children: [
          {
            index: true,
            element: <Navigate to={`${Route.HOME_SUPER}`} replace />,
          },
          {
            path: Route.HOME_SUPER,
            element: <HomeSuper />,
            // element: <CorporateAdmin />,
          },
          {
            path: `${Route.superAdmin.main}`,
            element: <ContentSuperAdmin />,
            children: [
              {
                index: true,
                element: (
                  <Navigate to={`${Route.superAdmin.company}`} replace />
                ),
              },
              {
                path: Route.superAdmin.company,
                element: <CompanyPageSuper />,
                children: [
                  {
                    index: true,
                    element: (
                      <Navigate
                        to={`${Route.superAdmin.companyPage.BRAND}`}
                        replace
                      />
                    ),
                  },
                  {
                    path: Route.superAdmin.companyPage.BRAND,
                    element: <Brand />,
                  },
                  {
                    path: Route.corporate.companyPage.SERVICE,
                    element: <Services />,
                  },
                  {
                    path: Route.superAdmin.companyPage.SETTING,
                    element: <SettingSuper />,
                  },
                ],
              },
              {
                path: Route.superAdmin.user,
                element: <GroupCorporate />,
              },
              {
                path: Route.superAdmin.question,
                element: <QuestionCorporate />,
              },
            ],
          },
        ],
      },
      /** CorporateAdmin-Route */
      {
        path: '/',
        element: <CorporateRoute />,
        children: [
          {
            index: true,
            element: <Navigate to={`${Route.HOME_CORPORATE}`} replace />,
          },
          {
            path: Route.HOME_CORPORATE,
            element: <DashboardCorporate />,
          },
          {
            path: Route.corporate.COMPANY_PAGE,
            element: <CompanyPage />,
            children: [
              {
                index: true,
                element: (
                  <Navigate
                    to={`${Route.corporate.companyPage.BRAND}`}
                    replace
                  />
                ),
              },
              {
                path: Route.corporate.companyPage.BRAND,
                element: <Brand />,
              },
              {
                path: Route.corporate.companyPage.SERVICE,
                element: <Services />,
              },
              {
                path: Route.corporate.companyPage.SETTING,
                element: <SettingsCorporate />,
              },
            ],
          },
          {
            path: Route.corporate.GROUP,
            element: <GroupCorporate />,
          },
          {
            path: Route.corporate.SIGNATURE_PROJECTS,
            element: <CorporateSignatureProjects />,
          },
          {
            path: `${Route.corporate.SIGNATURE_PROJECTS}/:userId`,
            element: <CorporateSignatureProjects />,
          },
          {
            path: `${Route.corporate.SIGNATURE_PROJECT_DETAIL}/:projectId`,
            element: <CorporateSignatureProjectDetail />,
          },

          {
            path: Route.corporate.FILE,
            element: <FileCorporate />,
          },
          {
            path: Route.corporate.CONTACT,
            element: <ContactCorporate />,
          },
          {
            path: Route.corporate.QUESTION,
            element: <QuestionCorporate />,
          },
        ],
      },
      {
        path: Route.CHANGE_PASSWORD,
        element: <SetPersonalPassword />,
      },
    ],
  },
];

export const router = createBrowserRouter(routesConfig);
