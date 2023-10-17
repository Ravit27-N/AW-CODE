import {initialState} from '@/redux/slides/corporate-admin/enterprise/enterpriseSlide';
import {Verb} from '@/utils/request/interface/type';
import {createApi, fetchBaseQuery} from '@reduxjs/toolkit/query/react';
import {baseQuery} from '../../RTKBaseQuery';
import {CorporateSettingTheme} from '../corporateSettingSlide';
import {formatBaseAPI} from '@/utils/common/ApiFacade';

export const defaultTheme: CorporateSettingTheme = {
  ...initialState.theme[0],
};
export type IGetCompanyTheme = {
  id: number;
  companyId: number;
  mainColor: string;
  secondaryColor: string;
  linkColor: string;
  logo: string;
  default: boolean;
};

export const corporateSettingPublicSlide = createApi({
  reducerPath: 'corporate-setting-public',
  baseQuery: fetchBaseQuery({
    baseUrl: formatBaseAPI(baseQuery.corporatePublic),
  }),
  tagTypes: ['corporate-setting-votre-marque'],
  endpoints: build => ({
    /**
     * It is published route, On Gravitate, we need to allow this route public.
     * */
    viewFileInCorporate: build.query<any, {fileName: string}>({
      query: ({fileName}) => ({
        url: `/api/corporate-settings/view?fileName=${fileName}`,
        method: Verb.Get,
        responseHandler: 'text',
      }),
      // providesTags: ['corporate-setting-votre-marque'],
    }),
    /**
     * It is published route, On Gravitate, we need to allow this route public.
     * */
    getCompanyTheme: build.query<IGetCompanyTheme[], {uuid: string}>({
      query: ({uuid}) => ({
        url: `/api/corporate-settings/company`,
        params: {uuid},
        method: Verb.Get,
      }),
    }),
  }),
});

export const {useGetCompanyThemeQuery, useLazyGetCompanyThemeQuery} =
  corporateSettingPublicSlide;
