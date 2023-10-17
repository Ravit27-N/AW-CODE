import {Localization} from '@/i18n/lan';
import isEmail from 'validator/lib/isEmail';
import {useTranslation} from 'react-i18next';
interface TYPE {
  val: string;
  NameId: string;
  setIsValid: any;
  setTextError: any;
  setValue: any;
}
function ValidateEmail({
  val,
  NameId,
  setIsValid,
  setTextError,
  setValue,
}: TYPE) {
  const {t} = useTranslation();

  if (NameId === t(Localization('form', 'email'))) {
    if (t(Localization('form', 'email'))) {
      if (isEmail(val)) {
        setIsValid(true);

        setTextError(t(Localization('status', 'success')) ?? '');
      } else {
        if (val === '') {
          setIsValid(false);

          setTextError(t(Localization('status', 'required')) ?? '');
        } else {
          setIsValid(false);

          setTextError(t(Localization('status', 'invalid')) ?? '');
        }
      }
    }
  } else {
    if (val !== t(Localization('form', 'email'))) {
      if (val === '') {
        setIsValid(false);
        setTextError(t(Localization('status', 'required')) ?? '');
      } else {
        setIsValid(true);
        setTextError(t(Localization('status', 'success')) ?? '');
      }
    }
  }
  setValue(val);
}

export default ValidateEmail;
