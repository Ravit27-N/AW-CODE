import { createReducer, on } from "@ngrx/store";
import * as tabMenuAction from './profile-tab-menu.action';

export interface ITabData { name: string; id: string; active: boolean, link: string, parent?: ITabData };

export const ProfileTabs = {
  listProfile: { id: 'list-profile', name: 'profile.list.title', active: false, link: 'list-profiles' },
  create: { id: 'create-profile', name: 'profile.create.title', active: false, link: 'create-profile' },
  update: { id: 'update-profile', name: 'profile.update.title', active: false, link: 'update-profile' }
}

export const ProfileTablinks = {
  listProfile: { link: '/cxm-profile/list-profiles' },
  create: {  link: '/cxm-profile/create-profile' },
  update: {  link: '/cxm-profile/update-profile' }
}


export const featureProfileTabMenuKey = 'feature-profile-menu';

export interface ProfileTabmenuState {
  lasttab?: ITabData,
  current?: ITabData,
  linkNext: boolean
}

const initialTabMenuState: ProfileTabmenuState = {
  linkNext: false
}

export const featureTabMenuReducer = createReducer(
  initialTabMenuState,
  on(tabMenuAction.enqueTab, (state, props) => {
    if (state.linkNext) {
      const theLastTab = state.lasttab ? { ...state.lasttab, active: false } : undefined;
      const newtab: ITabData = { ...props, parent: theLastTab, active: true };
      return { ...state, current: newtab, linkNext: false }
    } else {
      return { ...state, lasttab: state.current, current: {...props, active: true} };
    }
  }),
  on(tabMenuAction.dequeTab, (state, props) => {
    return { ...state, lasttab: props.parent, linkNext: true };
  }),
  on(tabMenuAction.linkTab, (state) => {
    return { ...state, linkNext: true, lasttab: state.current }
  })
)
