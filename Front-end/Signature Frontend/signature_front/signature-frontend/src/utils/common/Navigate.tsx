export const Navigate = (goto: string, companyDefault?: string) => {
  const query = window.location.search;
  const company = localStorage.getItem('company');
  let tempCom = undefined;

  const getSearch = (): string | undefined => {
    let search = undefined;
    const getCompany = (): string => {
      return companyDefault ?? company ?? 'certigna';
    };

    if (query) {
      if (query.includes('company')) {
        if (goto.includes('?')) {
          search = `&company=${getCompany()}&lng=${localStorage.getItem(
            'i18nextLng',
          )}`;
        } else {
          tempCom = `?company=${getCompany()}&lng=${localStorage.getItem(
            'i18nextLng',
          )}`;
        }
      }
    }

    return search;
  };
  return {
    pathname: `${goto}${getSearch() ?? ''}`,
    search: !getSearch()
      ? tempCom ?? `lng=${localStorage.getItem('i18nextLng')}`
      : undefined,
  };
};
