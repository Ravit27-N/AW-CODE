describe('shared-ui-search', () => {
  beforeEach(() => cy.visit('/iframe.html?id=searchcomponent--primary'));

  it('should render the component', () => {
    cy.get('cxm-smartflow-search').should('exist');
  });
});
