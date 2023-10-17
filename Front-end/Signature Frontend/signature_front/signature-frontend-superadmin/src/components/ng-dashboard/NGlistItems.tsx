import {Route} from '@/constant/Route';
import {VStack} from '@/theme';
import * as React from 'react';
import MyRoute from '../ng-myRouteCustom/NGRoute';

import {Localization} from '@/i18n/lan';
import {
  NGCookies,
  NGDashboard,
  NGEdit,
  NGEnterprise,
  NGFile,
  NGFolder,
  NGGroupOfPeople,
  NGQuestion,
  NGSetting,
  NGSpeed,
  NGThreePeople,
} from '@assets/iconExport/ExportIcon';
import Stack from '@mui/material/Stack';
import {t} from 'i18next';
export const MainListItems = () => {
  return (
    <React.Fragment>
      <VStack
        height={'100%'}
        sx={{justifyContent: 'space-between', paddingY: 2}}>
        <Stack
          sx={{justifyContent: 'flex-end', alignItems: 'flex-end'}}
          spacing={0.5}>
          <MyRoute
            goto={`${Route.HOME_ENDUSER}`}
            listIcon={<NGDashboard />}
            title={t(Localization('folder', 'dashboard'))!}
          />

          <MyRoute
            goto={`${Route.project.projectDetail}`}
            listIcon={<NGFolder />}
            title={t(Localization('folder', 'signature-project'))!}
          />
          <MyRoute
            goto={Route.endUser.assignedProject}
            listIcon={<NGEdit />}
            title={t(Localization('folder', 'assign-project'))!}
          />
          <MyRoute
            goto={Route.MODEL}
            listIcon={<NGFile />}
            title={t(Localization('folder', 'model-signature-path'))!}
          />
          {/**  It will be used in future   (sig-544-want-to-remove-it)
          <MyRoute
            goto={Route.MY_PROFILE}
            listIcon={<NGContact />}
            title={t(Localization('folder', 'contacts'))!}
          />
           **/}
        </Stack>
        <Stack justifyContent={'center'} alignItems={'center'}>
          <MyRoute
            goto={Route.QUESTION}
            listIcon={<NGQuestion />}
            title={'Help'}
          />
        </Stack>
      </VStack>
    </React.Fragment>
  );
};
export const mainListItemsCorporate = (
  <React.Fragment>
    <VStack height={'92vh'} sx={{justifyContent: 'space-between', paddingY: 0}}>
      <Stack
        sx={{justifyContent: 'flex-end', alignItems: 'flex-end'}}
        spacing={0.5}>
        <MyRoute
          goto={Route.HOME_CORPORATE}
          listName={''}
          listIcon={<NGSpeed />}
          title={'Dashboard'}
        />
        <MyRoute
          goto={Route.corporate.COMPANY_PAGE}
          listIcon={<NGEnterprise />}
          title={'Signature projects'}
        />
        <MyRoute
          goto={Route.corporate.GROUP}
          listIcon={<NGGroupOfPeople />}
          title={t(Localization('folder', 'assign-project'))!}
        />
        <MyRoute
          goto={Route.corporate.FILE}
          listIcon={<NGFile />}
          title={t(Localization('folder', 'model-signature-path'))!}
        />
        <MyRoute
          goto={Route.corporate.FOLDER}
          listIcon={<NGFolder />}
          title={t(Localization('folder', 'signature-project'))!}
        />
        {/**  It will be used in future   (sig-544-want-to-remove-it)
         <MyRoute
          goto={Route.corporate.CONTACT}
          listIcon={<NGContact />}
          title={'Contacts'}/>
          **/}
      </Stack>
      <Stack justifyContent={'center'} alignItems={'center'}>
        <MyRoute
          goto={Route.corporate.QUESTION}
          listIcon={<NGQuestion />}
          title={'Help'}
        />
      </Stack>
    </VStack>
  </React.Fragment>
);
/** Menu list of super admin  **/
export const MainListItemsSuperAdmin = () => {
  return (
    <React.Fragment>
      <VStack
        height={'92vh'}
        sx={{justifyContent: 'space-between', paddingY: 0}}>
        <Stack
          sx={{justifyContent: 'flex-end', alignItems: 'flex-end'}}
          spacing={0.5}>
          {/** Entreprise **/}
          <MyRoute
            goto={Route.superAdmin.company}
            listIcon={<NGEnterprise />}
            title={'Company'}
          />
          {/** Users **/}
          <MyRoute
            goto={Route.superAdmin.user}
            listIcon={<NGGroupOfPeople />}
            title={'Administrateurs'}
          />
        </Stack>
        <Stack justifyContent={'center'} alignItems={'center'}>
          <MyRoute
            goto={Route.superAdmin.question}
            listIcon={<NGQuestion />}
            title={'Help'}
          />
        </Stack>
      </VStack>
    </React.Fragment>
  );
};
export const SideBarListItemsSuper = () => {
  return (
    <React.Fragment>
      <VStack
        height={'92vh'}
        sx={{justifyContent: 'space-between', paddingY: 0}}>
        <Stack
          sx={{justifyContent: 'flex-end', alignItems: 'flex-start'}}
          spacing={'10px'}>
          <MyRoute
            goto={Route.superAdmin.companyPage.BRAND}
            listName={t(Localization('enterprise-brand', 'brand')) ?? 'Marque'}
            listIcon={<NGCookies />}
            title={'Dashboard'}
          />
          <MyRoute
            goto={Route.superAdmin.companyPage.SERVICE}
            listName={
              t(Localization('enterprise-services', 'department')) ?? 'Marque'
            }
            listIcon={<NGThreePeople />}
            title={'Signature projects'}
          />
          <MyRoute
            goto={Route.superAdmin.companyPage.SETTING}
            listName={
              t(Localization('enterprise-setting', 'parameters')) ?? 'Marque'
            }
            listIcon={<NGSetting />}
            title={'Signature projects'}
          />
        </Stack>
      </VStack>
    </React.Fragment>
  );
};
export const SideBarListItemsCorporate = () => {
  return (
    <React.Fragment>
      <VStack
        height={'92vh'}
        sx={{justifyContent: 'space-between', paddingY: 0}}>
        <Stack
          sx={{justifyContent: 'flex-end', alignItems: 'flex-start'}}
          spacing={'10px'}>
          <MyRoute
            goto={Route.corporate.companyPage.BRAND}
            listName={t(Localization('enterprise-brand', 'brand')) ?? 'Marque'}
            listIcon={<NGCookies />}
            title={'Dashboard'}
          />
          <MyRoute
            goto={Route.corporate.companyPage.SERVICE}
            listName={
              t(Localization('enterprise-services', 'services')) ?? 'Marque'
            }
            listIcon={<NGThreePeople />}
            title={'Signature projects'}
          />
          <MyRoute
            goto={Route.corporate.companyPage.SETTING}
            listName={
              t(Localization('enterprise-setting', 'parameters')) ?? 'Marque'
            }
            listIcon={<NGSetting />}
            title={'Signature projects'}
          />
        </Stack>
      </VStack>
    </React.Fragment>
  );
};
