import {DistributionByStatus} from "@cxm-smartflow/analytics/data-access";
import {colorEmailSms} from "./color-email-sms";

export class DistributionByStatusUtil {
  static getEmailLabels(data: DistributionByStatus[], messages: any): Array<string> {
    return data.map(item => {
      return messages.email_status[item.key.toLowerCase().replace(/\s+/g, '_')];
    });
  }

  static getSmsLabels(data: DistributionByStatus[], messages: any): Array<string> {
    return data.map(item => {
      return messages.sms_status[item.key.toLowerCase().replace(/\s+/g, '_')];
    });
  }

  static getEmailColor(data: DistributionByStatus[], messages: any): Array<string> {

    return data.map(item => {
      const value = colorEmailSms.getColorEmail()[item.key.toLowerCase().replace(/\s+/g, '_')];
      return value ? value : `#${Math.floor(Math.random() * 0xFFFFFF + 1).toString(16).padStart(6, '0')}`;
    });
  }

  static getSmsColor(data: DistributionByStatus[], messages: any): Array<string> {

    return data.map(item => {
      const value = colorEmailSms.getColorSms()[item.key.toLowerCase().replace(/\s+/g, '_')];
      return value ? value : `#${Math.floor(Math.random() * 0xFFFFFF + 1).toString(16).padStart(6, '0')}`;
    });
  }

}
