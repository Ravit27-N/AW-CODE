export class colorEmailSms {

  private static sms_color: any = {
    "in_progress": 'rgb(0,116,186)',
    "sent": 'rgb(255,103,35)',
    "cancelled": 'rgb(252,213,63)',
    "in_error": 'rgb(158,158,158)'
  };
  private static email_color: any = {
    "hard_bounce": 'rgb(189,20,32)',
    "soft_bounce": 'rgb(72,116,196)',
    "clicked": 'rgb(238,143,78)',
    "resent": 'rgb(161,161,161)',
    "opened": 'rgb(255,192,0)',
    "sent": 'rgb(91,155,213)',
    "blocked": 'rgb(241,99,126)',
    "in_error": 'rgb(112,173,71)',
    "in_progress": 'rgb(228,0,238)'
  };

  static getColorSms(): any {
    return this.sms_color;
  }

  static getColorEmail(): any {
    return this.email_color;
  }
}
