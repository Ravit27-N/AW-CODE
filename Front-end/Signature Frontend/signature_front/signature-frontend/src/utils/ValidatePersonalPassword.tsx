import React from 'react';
import {NumReg, SpecialCharacterReg} from '@/utils/common/RegSpecialCharacter';
export interface TypeValidatePassword {
  Num: boolean;
  Upper: boolean;
  Lower: boolean;
  Special: boolean;
  Character12: boolean;
}
const ValidatePersonalPassword = (pass: string) => {
  const [state, setState] = React.useState<TypeValidatePassword>({
    Num: false,
    Upper: false,
    Lower: false,
    Special: false,
    Character12: false,
  });
  React.useEffect(() => {
    SpecialCharacterReg(pass)
      ? setState(prevState => {
          return {...prevState, Special: true};
        })
      : setState(prevState => {
          return {...prevState, Special: false};
        });
    NumReg(pass)
      ? setState(prevState => {
          return {...prevState, Num: true};
        })
      : setState(prevState => {
          return {...prevState, Num: false};
        });
    /[A-Z]+/.test(pass)
      ? setState(prevState => {
          return {...prevState, Upper: true};
        })
      : setState(prevState => {
          return {...prevState, Upper: false};
        });
    /[a-z]+/.test(pass)
      ? setState(prevState => {
          return {...prevState, Lower: true};
        })
      : setState(prevState => {
          return {...prevState, Lower: false};
        });
    /.{12,20}/.test(pass)
      ? setState(prevState => {
          return {...prevState, Character12: true};
        })
      : setState(prevState => {
          return {...prevState, Character12: false};
        });
  }, [pass]);
  return {state};
};
export default ValidatePersonalPassword;
