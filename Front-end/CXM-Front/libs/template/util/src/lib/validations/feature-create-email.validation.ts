/**
 * Method for validate subject mail, return {boolean, subjectMails}
 * @param value
 * @returns
 */
export const isSubjectMailValid = (value: string) => {
  let result = false;
  const arr = value.split('{');
  const subjectMails: string[] = [];

  arr.forEach((item) => {
    if (item) {
      if (item && item.includes('}')) {
        // have variable
        const firstIndex = item.indexOf('{');
        const lastIndex = item.indexOf('}');
        const finalItem = item.substring(firstIndex + 1, lastIndex);
        if (finalItem) {
          if (!(subjectMails.indexOf(finalItem) >= 0)) {
            subjectMails.push(finalItem);
          }
        }
      }
      result = true;
    }
  });

  return {
    isValid: result,
    subjectMails: subjectMails,
  };
};

/**
 * validate with URL Pattern, returnn true or false
 * @param url
 * @returns
 */

export const isValidURL = (url: string) => {
  let valid = true;
  try {
    if (url) {
      const validUrl = new URL(url);
      if (validUrl.host == '' && validUrl.origin == 'null') {
        valid = false;
      }
    }
  } catch {
    valid = false;
  }
  return valid;
};

/**
 * validate Email Pattern, return true or false
 * @param email
 * @returns
 */
export const isEmail = (email: string) => {
  // eslint-disable-next-line no-useless-escape
  const pattern = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return pattern.test(email);
};

/**
 * method for validate UnSubscribeLink, return true or false
 * @param value
 * @returns
 */
export const validateUnSubscribeLink = (value: string) => {
  if (value === undefined) return false;
  if (value?.includes('mailto:')) {
    const unSubScribeLinkSplit = value?.split(':');
    return isEmail(unSubScribeLinkSplit[1]?.trim());
  } else {
    return isValidURL(value);
  }
};
