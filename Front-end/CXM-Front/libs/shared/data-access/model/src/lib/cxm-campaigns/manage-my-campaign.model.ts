export interface sendMailForm {
  template: {
    name: string;
  };
  email: {
    isPublished: string;
    replyToAddress: string;
  };
  contacts: [
    {
      contact: {
        firstname: string;
        lastname: string;
      };
      tokens: {
        nom: string;
        prenom: string;
        email: string;
        webview_url: string;
        unsubscribe_url: string;
      }
    }
  ]
}
