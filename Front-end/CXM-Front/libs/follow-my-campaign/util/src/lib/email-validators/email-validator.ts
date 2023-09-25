 export const pattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
/**
 * Method used to validate email address.
 * @param email
 * @returns value of boolean.
 */
export const validateEmail = (email: string): boolean => {
  return pattern.test(String(email).toLowerCase());
 }
