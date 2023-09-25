describe('shared-ui-input', () => {
  beforeEach(() => cy.visit('/iframe.html?id=inputcomponent--primary'));

  it('should render the component', () => {
    cy.get('cxm-smartflow-input').should('exist');
  });
});
