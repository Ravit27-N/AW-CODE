export const regSpecialCharacter = (pass: string) => {
  return !/^[-@/#$&%^*+=(){}\\|'";:<>,.?~![\]\w\s]*$/.test(pass);
};
/**ALL special characters*/
export const SpecialCharacterReg = (pass: string) => {
  return /[~!@#$%^&*_\-+=|/\\[(){}:;"' <>,\].?]+/.test(pass);
};
export const NumReg = (pass: string) => {
  return /\d+/.test(pass);
};
export const UpperReg = (pass: string) => {
  return /[A-Z]+/.test(pass);
};
export const LowerReg = (pass: string) => {
  return /[a-z]+/.test(pass);
};
export const Characters_12_Reg = (pass: string) => {
  return /.{12,20}/.test(pass);
};
/** Accept only number, alphabet and some character that we allow **/
export const AcceptOnlyNumAlphabetAndSomeSpecialCharacter = (pass: string) => {
  return /^[A-Za-z0-9~!@#$%^&*_\-+=|/\\[(){}:;"' <>,\].?]*$/.test(pass);
};
/** Accept only number and Alphabet **/
export const IsNumberAndAlphabet = (pass: string) => {
  return /^[a-zA-Z0-9]*$/.test(pass);
};
