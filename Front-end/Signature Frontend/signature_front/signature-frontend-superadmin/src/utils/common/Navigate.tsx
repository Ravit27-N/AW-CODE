export const Navigate = (goto: string, companyDefault?: string) => {
  const query = window.location.search;
  const company = localStorage.getItem('company');
  let tempCom = undefined;

  const getSearch = (): string | undefined => {
    let search = undefined;
    if (query) {
      if (query.includes('company')) {
        if (goto.includes('?')) {
          search = `&company=${
            companyDefault ? companyDefault : company ?? 'certigna'
          }&lng=${localStorage.getItem('i18nextLng')}`;
        } else {
          tempCom = `?company=${
            companyDefault ? companyDefault : company ?? 'certigna'
          }&lng=${localStorage.getItem('i18nextLng')}`;
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
