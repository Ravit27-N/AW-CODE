import {
  CorporateCompanyInterface,
  CorporateSettingTheme,
} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {createSlice} from '@reduxjs/toolkit';

export type IThemeEnterPrise = {
  companyId: number;
  default: boolean;
  id: number;
  linkColor: string;
  logo: string;
  mainColor: string;
  secondaryColor: string;
};

export const initialState: CorporateCompanyInterface = {
  name: null,
  email: null,
  contactFirstName: null,
  contactLastName: null,
  fixNumber: null,
  country: 'fr',
  theme: [
    {
      mainColor: null,
      secondaryColor: null,
      linkColor: null,
      logo: null,
    },
  ],
  companyProviderTheme: {
    mainColor: null,
    secondaryColor: null,
    linkColor: null,
    logo: null,
  },
};

export interface ColorTheme {
  mainColor: string;
  secondaryColor: string;
  linkColor: string;
}

export const enterpriseSlice = createSlice({
  name: 'enterprise',
  initialState,
  reducers: {
    setCompanyProviderTheme: (
      state,
      action: {
        payload: Pick<CorporateCompanyInterface, 'companyProviderTheme'>;
      },
    ) => {
      const {companyProviderTheme} = action.payload;
      state.companyProviderTheme = companyProviderTheme;
    },

    setCorporateThemeSetting: (
      state,
      action: {payload: {reduxTheme: CorporateSettingTheme}},
    ) => {
      const {reduxTheme} = action.payload;
      return {
        ...state,
        theme: [reduxTheme],
      };
    },
    setCorporateActiveColor: (
      state,
      action: {payload: {colors: ColorTheme}},
    ) => {
      const {colors} = action.payload;
      return {
        ...state,
        companyProviderTheme: {
          ...state.companyProviderTheme,
          mainColor: colors.mainColor,
          secondaryColor: colors.secondaryColor,
          linkColor: colors.linkColor,
        },
        theme: [
          {
            ...state.theme[0],
            mainColor: colors.mainColor,
            secondaryColor: colors.secondaryColor,
            linkColor: colors.linkColor,
          },
        ],
      };
    },
    setCorporateLogo: (state, action: {payload: {logo: string}}) => {
      const {logo} = action.payload;
      return {
        ...state,
        theme: [
          {
            ...state.theme[0],
            logo,
          },
        ],
      };
    },
  },
});

export const {
  setCorporateThemeSetting,
  setCorporateActiveColor,
  setCompanyProviderTheme,
} = enterpriseSlice.actions;

export default enterpriseSlice.reducer;
