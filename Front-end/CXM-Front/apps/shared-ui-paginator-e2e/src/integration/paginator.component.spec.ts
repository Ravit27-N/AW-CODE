describe('shared-ui-paginator', () => {
  beforeEach(() => cy.visit('/iframe.html?id=paginatorcomponent--primary&knob-length&knob-pageIndex&knob-pageSize&knob-showTotalPages&knob-pageSizeOptions'));

  it('should render the component', () => {
    cy.get('cxm-smartflow-paginator').should('exist');
  });
});
