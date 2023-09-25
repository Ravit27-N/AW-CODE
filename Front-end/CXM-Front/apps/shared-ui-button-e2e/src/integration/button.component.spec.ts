describe('shared-ui-button', () => {
  beforeEach(() => cy.visit('/iframe.html?id=buttoncomponent--primary'));

  it('should render the component', () => {
    cy.get('cxm-smartflow-button').should('exist');
  });
});
