import {useTranslation} from 'react-i18next';
import {Localization} from './i18n/lan';

function App() {
  const {t, i18n, ready} = useTranslation();

  return (
    <div>
      {ready && <h1>{t(Localization('title', 'Welcome to React'))}</h1>}
      <button
        onClick={async () => {
          return i18n.changeLanguage('en');
        }}>
        change lan
      </button>
    </div>
  );
}

export default App;
