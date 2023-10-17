import i18n from 'i18next';
import {en, fr} from '@/i18n/lan/index';
import {initReactI18next} from 'react-i18next';
import Backend from 'i18next-http-backend';
import LanguageDetector from 'i18next-browser-languagedetector';

/** the translations
 (tip move them in a JSON file and import them,
 or even better, manage them separated from your code: https://react.i18next.com/guides/multiple-translation-files)
**/
const resources = {
  en: {
    translation: en,
  },
  fr: {
    translation: fr,
  },
};
const fallbackLng = ['fr'];
export const options = {
  /** order and from where user language should be detected **/
  order: [
    'querystring',
    'cookie',
    'localStorage',
    'sessionStorage',
    'navigator',
    'htmlTag',
    'path',
    'subdomain',
  ],

  /** keys or params to lookup language from **/
  lookupQuerystring: 'lng',
  lookupCookie: 'i18next',
  lookupLocalStorage: 'i18nextLng',
  lookupSessionStorage: 'i18nextLng',
  lookupFromPathIndex: 0,
  lookupFromSubdomainIndex: 0,

  /** cache user language on **/
  caches: ['localStorage', 'cookie'],
  excludeCacheFor: ['cimode'], // languages to not persist (cookie, localStorage)

  /** optional expire and domain for set cookie **/
  cookieMinutes: 10,
  cookieDomain: 'myDomain',

  /** optional htmlTag with lang attribute, the default is:**/
  htmlTag: document.documentElement,

  /** optional set cookie options, reference:https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie**/
  cookieOptions: {path: '/', sameSite: 'strict'},
};
i18n
  .use(Backend)
  .use(LanguageDetector)
  .use(initReactI18next) /** passes i18n down to react-i18next**/
  .init({
    /** returnObjects: true,
         cleanCode: true**/
    debug: true,
    resources,
    fallbackLng,
    lng: 'fr' /** Currently, we use only French version*/,
    detection: options as any,
    /** lng: 'en', // language to use, more information here: https://www.i18next.com/overview/configuration-options#languages-namespaces-resources
         you can use the i18n.changeLanguage function to change the language manually: https://www.i18next.com/overview/api#changelanguage
         if you're using a language detector, do not define the lng option**/

    interpolation: {
      escapeValue: false /** react already safes from xss**/,
    },
    react: {
      useSuspense: false,
    },
  })
  .then(r => r);

export default i18n;
